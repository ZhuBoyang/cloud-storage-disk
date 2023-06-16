package online.yangcloud.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;
import online.yangcloud.common.constants.AppConstants;
import online.yangcloud.common.resultcode.AppResultCode;
import online.yangcloud.enumration.FileTypeEnum;
import online.yangcloud.enumration.YesOrNoEnum;
import online.yangcloud.model.BlockMetadata;
import online.yangcloud.model.FileBlock;
import online.yangcloud.model.FileMetadata;
import online.yangcloud.model.User;
import online.yangcloud.model.ao.file.FileSearcher;
import online.yangcloud.model.ao.file.FileUploader;
import online.yangcloud.model.bo.file.FileOperateValidator;
import online.yangcloud.model.vo.PagerView;
import online.yangcloud.model.vo.file.BreadsView;
import online.yangcloud.model.vo.file.FileMetadataView;
import online.yangcloud.service.FileService;
import online.yangcloud.service.meta.BlockMetadataService;
import online.yangcloud.service.meta.FileBlockService;
import online.yangcloud.service.meta.FileMetadataService;
import online.yangcloud.service.meta.UserMetaService;
import online.yangcloud.utils.*;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhuboyang
 * @since 2023年05月17 10:47:25
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FileServiceImpl implements FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Resource
    private UserMetaService userMetaService;

    @Resource
    private FileMetadataService fileMetadataService;

    @Resource
    private FileBlockService fileBlockService;

    @Resource
    private BlockMetadataService blockMetadataService;

    @Resource
    private SystemTools systemTools;

    @Resource
    private RedisTools redisTools;

    @Resource
    private RedissonClient redissonClient;

    @Override
    public Integer checkBlockExist(FileUploader uploader) {
        // 检测文件块是否已在库中
        if (ObjectUtil.isNull(blockMetadataService.queryByHash(uploader.getHash()))) {
            return YesOrNoEnum.NO.code();
        }

        // 将文件块数据存入 redis，待存入文件元数据时使用
        String redisValue = JSONUtil.toJsonStr(uploader);
        double blockIndex = uploader.getBlockIndex().doubleValue();
        redisTools.zSetAdd(AppConstants.Uploader.FILE_BLOCK_UPLOAD_PREFIX + uploader.getIdentifier(), redisValue, blockIndex);
        return YesOrNoEnum.YES.code();
    }

    @Override
    public Integer blockUpload(FileUploader uploader) {
        // 检测当前文件块是否已存到硬盘上。如果没有，则需要存到磁盘上，并入库
        RLock blockUploadLocker = redissonClient.getLock("upload_file_block_lock:" + uploader.getIdentifier());
        try {
            blockUploadLocker.lock();
            BlockMetadata block = blockMetadataService.queryByHash(uploader.getHash());

            // 如果检测到文件块还不存在，就说明还没有上传过，那么先进行落地
            if (ObjectUtil.isNull(block)) {
                String uploadPath = systemTools.systemPath() + AppConstants.Uploader.BLOCK_UPLOAD_PATH;
                if (!FileUtil.exist(uploadPath)) {
                    FileUtil.mkdir(uploadPath);
                }
                uploader.getFile().transferTo(new File(uploadPath + uploader.getHash()));
            }
            block = BlockMetadata.initial(uploader.getHash(), uploader.getBlockSize());
            blockMetadataService.insertBlock(block);

            // 将文件块数据加入到 redis 集合中，便于入库文件元数据，使用 redisson 分布式锁解决并发
            uploader.setFile(null);
            String redisValue = JSONUtil.toJsonStr(uploader);
            redisTools.zSetAdd(AppConstants.Uploader.FILE_BLOCK_UPLOAD_PREFIX + uploader.getIdentifier(),
                    redisValue, uploader.getBlockIndex().doubleValue());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            blockUploadLocker.unlock();
        }
        return YesOrNoEnum.YES.code();
    }

    @Override
    public FileMetadataView fileMerge(String identifier, User user) throws IOException {
        // 1. 获取 redis 中存着的文件块数据
        // 2. 将数据转为文件块上传参数列表和文件块 hash 列表
        List<String> blockUploaderJsons = redisTools.zSetRange(AppConstants.Uploader.FILE_BLOCK_UPLOAD_PREFIX + identifier, 0D, Double.MAX_VALUE);
        List<FileUploader> blockUploaderList = new ArrayList<>();
        List<String> blockHashList = new ArrayList<>();
        blockUploaderJsons.forEach(o -> {
            FileUploader uploader = JSONUtil.toBean(o, FileUploader.class);
            blockUploaderList.add(uploader);
            blockHashList.add(uploader.getHash());
        });

        // 1. 从上传的文件块参数中获取第一个个元素
        // 2. 查询上传的目录下所有含有上传文件名相关的文件
        // 3. 计算存储文件的文件名后的后缀数字
        FileUploader uploader = blockUploaderList.get(0);
        List<FileMetadata> files = fileMetadataService.queryLikePrefix(uploader.getId(), uploader.getFileName(), FileTypeEnum.FILE);
        int fileNumber = FileMetadata.calculateFileSuffixNumber(files, uploader.getFileName());

        // 查询并将文件块的元数据转为 map，方便后续生成文件块的存储路径，用以合并文件
        List<BlockMetadata> blocks = blockMetadataService.queryListByHashList(blockHashList);
        Map<String, BlockMetadata> blocksHashMap = blocks.stream().collect(Collectors.toMap(BlockMetadata::getHash, block -> block));

        // 生成文件 id，用以存储文件与文件块之间的关联关系
        String fileId = IdTools.fastSimpleUuid();
        blockUploaderList.sort(Comparator.comparingInt(FileUploader::getBlockIndex));
        List<FileBlock> fileBlocks = blockUploaderList.stream()
                .map(o -> FileBlock.initial(fileId, o, blocksHashMap.get(o.getHash())))
                .collect(Collectors.toList());
        fileBlockService.batchInsert(fileBlocks);

        // 将文件块合并成文件，用以生成文件 hash
        Map<String, String> blocksPathMap =
                blocks.stream().collect(Collectors.toMap(BlockMetadata::getId, BlockMetadata::getPath));
        List<String> blockPaths = fileBlocks.stream()
                .map(o -> systemTools.systemPath() + blocksPathMap.get(o.getBlockId()))
                .collect(Collectors.toList());
        String filePath = systemTools.systemPath() + AppConstants.Uploader.FILE_UPLOAD_PATH + uploader.getIdentifier();
        FileTools.combineFile(filePath, blockPaths);
        String fileHash = SecureUtil.md5(Files.newInputStream(FileUtil.file(filePath).toPath()));
        if (FileTools.isPic(uploader.getExt())) {
            FileUtil.rename(FileUtil.file(filePath), fileHash, Boolean.TRUE);
        }
        // 如果是图片的话，就保留合并的文件，以便页面回显
        if (!FileTools.isPic(uploader.getExt())) {
            FileUtil.del(filePath);
        }

        // 封装文件元数据并入库
        FileMetadata parent = fileMetadataService.queryById(uploader.getId(), user.getId());
        FileMetadata file = FileMetadata.initial(fileId, fileNumber, fileHash, parent, uploader, user);
        fileMetadataService.insertWidthPrimaryKey(file);

        // 更新账户的空间容量
        user.setUsedSpaceSize(user.getUsedSpaceSize() + file.getSize());
        userMetaService.updateUser(user);
        return BeanUtil.copyProperties(file, FileMetadataView.class);
    }

    @Override
    public void initialUserRoot(String userId) {
        FileMetadata file = FileMetadata.initial(userId);
        fileMetadataService.insertWidthPrimaryKey(file);
    }

    @Override
    public FileMetadataView mkdir(String pid, String name, String userId) {
        // 1. 查询当前目录下与待创建文件夹名相关的文件列表
        // 2. 计算文件夹后缀数字
        List<FileMetadata> files = fileMetadataService.queryLikePrefix(pid, name, FileTypeEnum.DIR);
        int fileNumber = FileMetadata.calculateFileSuffixNumber(files, name);

        // 查询父级文件元数据，用以拼接祖级文件 id
        FileMetadata parent = fileMetadataService.queryById(pid, userId);

        // 拼接文件夹名，并入库
        name = fileNumber == 0 ? name : name + AppConstants.LEFT_BRACKET + fileNumber + AppConstants.RIGHT_BRACKET;
        FileMetadata file = FileMetadata.initialDir(pid, name, parent.queryAncestors(), userId);
        fileMetadataService.insertWidthPrimaryKey(file);
        return FileMetadataView.convert(file);
    }

    @Override
    public void batchDeleteFile(List<String> ids, String userId) {
        // 检测存在的文件。因为有可能有的文件已经不存在了
        ids = fileMetadataService.queryListByIds(ids, userId).stream().map(FileMetadata::getId).collect(Collectors.toList());

        // 删除文件
        fileMetadataService.batchRemove(ids, userId);

        // 查询子级的所有文件与文件夹
        List<FileMetadata> childList = new ArrayList<>();
        for (String fileId : ids) {
            childList.addAll(fileMetadataService.queryChildListByPid(fileId, userId, Boolean.FALSE));
        }
        if (childList.size() == 0) {
            return;
        }

        // 删除所有子级文件与文件夹
        fileMetadataService.batchRemove(childList.stream().map(FileMetadata::getId).collect(Collectors.toList()), userId);
    }

    @Override
    public void batchCopy(List<String> sourcesIds, String targetId, String userId) {
        // 对待操作文件和文件夹与目标目录进行校验
        FileOperateValidator validator = validator(sourcesIds, targetId, userId);

        // 需要复制的文件列表
        List<FileMetadata> copiedFiles = new ArrayList<>();
        // 需要复制的文件与文件块的关联关系列表
        List<FileBlock> copiedBlocks = new ArrayList<>();

        Queue<FileMetadata> files = new ArrayDeque<>(validator.getSources());
        Map<String, FileMetadata> historyThisMap = new HashMap<>(0);

        while (true) {
            FileMetadata o = files.poll();
            if (ObjectUtil.isNull(o)) {
                break;
            }
            // 获取文件的父级目录元数据
            FileMetadata futureParent = historyThisMap.get(o.getPid());
            if (ObjectUtil.isNull(futureParent)) {
                futureParent = validator.getTarget();
            }
            // 封装新的元数据
            FileMetadata file = FileMetadata.packNew(o, futureParent.getId(), futureParent.addIdInAncestors());
            copiedFiles.add(file);
            // 如果是文件的话，那么查询所有文件块的关联数据
            if (FileTypeEnum.FILE.is(o.getType())) {
                copiedBlocks.addAll(fileBlockService.queryBlocks(o.getId()));
            }
            // 如果是文件夹的话，需要查询所有子级的文件及文件夹。并加入队列中，后续统一进行入库
            if (FileTypeEnum.DIR.is(o.getType())) {
                // 将生成的文件元数据记录到 map 中
                historyThisMap.put(o.getId(), file);
                files.addAll(fileMetadataService.queryListByPid(o.getId(), userId));
            }
        }

        fileMetadataService.batchInsert(copiedFiles);
        fileBlockService.batchInsert(copiedBlocks);
    }

    @Override
    public void batchMove(List<String> sources, String target, String userId) {
        // 对待操作文件和文件夹与目标目录进行校验
        FileOperateValidator validator = validator(sources, target, userId);

        // 检测待操作文件是否已在目标目录下
        for (FileMetadata file : validator.getSources()) {
            if (file.getPid().equals(target)) {
                ExceptionTools.businessLogger("The file is already in the directory and does not need to be moved");
            }
        }

        // 需要移动的文件列表
        List<FileMetadata> movedFiles = new ArrayList<>();

        Queue<FileMetadata> files = new ArrayDeque<>(validator.getSources());
        Map<String, FileMetadata> historyThisMap = new HashMap<>(0);

        while (true) {
            FileMetadata o = files.poll();
            if (ObjectUtil.isNull(o)) {
                break;
            }
            // 获取文件的父级目录元数据
            FileMetadata futureParent = historyThisMap.get(o.getPid());
            Boolean isRoot = Boolean.FALSE;
            if (ObjectUtil.isNull(futureParent)) {
                futureParent = validator.getTarget();
                isRoot = Boolean.TRUE;
            }
            // 封装新的元数据，并装入集合，以待后续统一入库
            if (isRoot) {
                o.setPid(futureParent.getId());
            }
            o.setterAncestors(futureParent.addIdInAncestors());
            o.setCreateTime(DateUtil.date().getTime()).setUpdateTime(DateUtil.date().getTime());
            movedFiles.add(o);
            // 将新的文件元数据存入 map
            historyThisMap.put(o.getId(), o);
            // 如果是文件夹的话，需要查询子级文件及文件夹，并放入队列，进行后续的移动操作
            if (FileTypeEnum.DIR.is(o.getType())) {
                files.addAll(fileMetadataService.queryListByPid(o.getId(), userId));
            }
        }

        fileMetadataService.batchUpdate(movedFiles);
    }

    /**
     * 移动、复制时的文件校验
     *
     * @param sources 待操作文件
     * @param target  目标目录
     * @param userId  文件所属用户 id
     * @return result
     */
    private FileOperateValidator validator(List<String> sources, String target, String userId) {
        // 检测是否有选中的文件，如果没有选择任何一个文件，那么无法执行后续操作
        if (sources.size() == 0) {
            ExceptionTools.businessLogger("请选择要操作的文件");
        }

        // 检测是否已选择目标目录
        if (CharSequenceUtil.isBlank(target)) {
            ExceptionTools.businessLogger("请选择目标目录");
        }

        // 将目标目录与待操作文件 id 一同进行查询，减少查库次数
        sources.add(target);
        List<FileMetadata> files = fileMetadataService.queryListByIds(sources, userId);

        // 获取目标目录元数据
        List<FileMetadata> findTargets = files.stream().filter(o -> o.getId().equals(target)).collect(Collectors.toList());
        FileMetadata fileTarget = findTargets.size() == 0 ? null : findTargets.get(0);

        // 校验目标目录是否存在
        if (ObjectUtil.isNull(fileTarget) || YesOrNoEnum.YES.is(fileTarget.getIsDelete())) {
            ExceptionTools.businessLogger("目标目录不存在，请刷新后重试");
        }

        // 检测目标文件是否是目录
        if (ObjectUtil.isNotNull(fileTarget) && FileTypeEnum.FILE.is(fileTarget.getType())) {
            ExceptionTools.businessLogger("无法移动至文件，请选择文件夹");
        }

        // 校验待操作文件是否都存在
        List<FileMetadata> fileSources = files.stream().filter(o -> !o.getId().equals(target)).collect(Collectors.toList());
        if (fileSources.size() != sources.size()) {
            ExceptionTools.businessLogger("有文件不存在，请刷新后重试");
        }

        // 检测目标目录是否为待移动文件或文件夹的子级目录
        if (ObjectUtil.isNotNull(fileTarget)) {
            List<String> ancestors = fileTarget.addIdInAncestors();
            for (FileMetadata source : fileSources) {
                if (FileTypeEnum.DIR.is(source.getType()) && ancestors.contains(source.getId())) {
                    ExceptionTools.businessLogger("文件无法操作至子级目录下");
                }
            }
        }
        return new FileOperateValidator().setTarget(fileTarget).setSources(fileSources);
    }

    @Override
    public FileMetadataView queryById(String id, String userId) {
        return CharSequenceUtil.isBlank(id) ?
                FileMetadataView.convert(fileMetadataService.queryByPid(CharSequenceUtil.EMPTY, userId)) :
                FileMetadataView.convert(fileMetadataService.queryById(id, userId));
    }

    @Override
    public List<BreadsView> queryBreads(String id, String userId) {
        FileMetadata file = fileMetadataService.queryById(id, userId);
        List<String> ancestors = file.queryAncestors();
        if (ancestors.size() == 0) {
            return BreadsView.convert(Collections.singletonList(file));
        }
        List<FileMetadata> files = fileMetadataService.queryListByIds(ancestors, userId);
        files.add(file);
        return BreadsView.convert(files);
    }

    @Override
    public PagerView<FileMetadataView> queryFiles(FileSearcher searcher, String userId) {
        PagerView<FileMetadata> filePager = fileMetadataService.queryFilesInPager(searcher.getPid(), searcher.getName(), userId,
                YesOrNoEnum.YES.is(searcher.getIsOnlyDir()), searcher.getPageIndex(), searcher.getPageSize());
        List<FileMetadataView> views = filePager.getData().stream().map(FileMetadataView::convert).collect(Collectors.toList());
        return new PagerView<FileMetadataView>().setData(views).setTotal(filePager.getTotal());
    }

    @Override
    public void download(String id, HttpServletResponse response) {
        // 校验文件是否存在
        FileMetadata file = fileMetadataService.queryById(id);
        if (ObjectUtil.isNull(file)) {
            ExceptionTools.businessLogger(AppResultCode.DATA_NOT_FOUND.getMessage());
        }

        // 检测文件类型，如果是文件夹，则不能下载
        if (FileTypeEnum.DIR.is(file.getType())) {
            ExceptionTools.businessLogger(AppResultCode.FAILURE.getMessage());
        }

        // 查询所有的文件块，并合并文件到临时目录
        List<FileBlock> fileBlocks = fileBlockService.queryBlocks(file.getId());

        List<BlockMetadata> blocks = blockMetadataService.queryListByIds(fileBlocks.stream()
                .map(FileBlock::getBlockId).collect(Collectors.toList()));
        Map<String, String> blockStorageMap = blocks.stream().collect(Collectors.toMap(BlockMetadata::getId, BlockMetadata::getPath));

        // 合并文件
        List<String> storages = fileBlocks.stream()
                .map(o -> systemTools.systemPath() + blockStorageMap.get(o.getBlockId()))
                .collect(Collectors.toList());
        String filePath = systemTools.systemPath() + AppConstants.Uploader.TMP_PATH;
        FileTools.combineFile(filePath + file.getHash(), storages);

        // 下载文件
        FileTools.downloadResponse(filePath + file.getHash(), file.getName() + file.getExt(), response);

        // 删除临时目录中的文件
        FileUtil.del(filePath + file.getHash());
    }

}
