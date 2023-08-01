package online.yangcloud.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
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
import online.yangcloud.model.business.file.FileOperateValidator;
import online.yangcloud.model.request.file.DirLooker;
import online.yangcloud.model.request.file.FileSearcher;
import online.yangcloud.model.request.file.FileUploader;
import online.yangcloud.model.request.file.TrashQuery;
import online.yangcloud.model.view.PagerView;
import online.yangcloud.model.view.file.BreadsView;
import online.yangcloud.model.view.file.FileMetadataView;
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
                String uploadPath = SystemTools.systemPath() + AppConstants.Uploader.BLOCK_UPLOAD_PATH;
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
                .map(o -> SystemTools.systemPath() + blocksPathMap.get(o.getBlockId()))
                .collect(Collectors.toList());
        String filePath = SystemTools.systemPath() + AppConstants.Uploader.FILE_UPLOAD_PATH + blockUploaderList.get(0).getIdentifier();
        FileTools.combineFile(filePath, blockPaths);
        String fileHash = SecureUtil.md5(Files.newInputStream(FileUtil.file(filePath).toPath()));
        if (FileTools.isPic(blockUploaderList.get(0).getExt())) {
            FileUtil.rename(FileUtil.file(filePath), fileHash, Boolean.TRUE);
        }
        // 如果是图片的话，就保留合并的文件，以便页面回显
        if (!FileTools.isPic(blockUploaderList.get(0).getExt())) {
            FileUtil.del(filePath);
        }

        // 封装文件元数据并入库
        String name = calculateName(blockUploaderList.get(0).getId(), blockUploaderList.get(0).getFileName(), FileTypeEnum.FILE.code());
        FileMetadata parent = fileMetadataService.queryById(blockUploaderList.get(0).getId(), user.getId());
        FileMetadata file = FileMetadata.initial(fileId, name, fileHash, parent, blockUploaderList.get(0), user);
        fileMetadataService.insertWidthPrimaryKey(file);

        // 增加空间使用量
        userMetaService.updateSpaceSize(user, file.getSize());

        // 清空文件块的缓存记录
        redisTools.delete(AppConstants.Uploader.FILE_BLOCK_UPLOAD_PREFIX + identifier);
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
    public void batchDeleteFile(List<String> ids, User user) {
        // 检测存在的文件。因为有可能有的文件已经不存在了
        List<FileMetadata> files = fileMetadataService.queryListByIds(ids, user.getId());
        ids = files.stream().map(FileMetadata::getId).collect(Collectors.toList());

        // 删除文件
        fileMetadataService.batchRemove(ids, user.getId());

        // 查询子级的所有文件与文件夹，并计算占用空间总量
        long total = 0;
        for (FileMetadata o : files) {
            if (FileTypeEnum.FILE.is(o.getType())) {
                total += o.getSize();
            }
            if (FileTypeEnum.DIR.is(o.getType())) {
                List<FileMetadata> childList = fileMetadataService.queryChildListByPid(o.getId(), user.getId(), Boolean.FALSE);
                for (FileMetadata child : childList) {
                    if (FileTypeEnum.FILE.is(child.getType())) {
                        total += child.getSize();
                    }
                }
            }
        }

        // 更新用户账户中的空间使用量
        if (total > 0) {
            userMetaService.updateSpaceSize(user, total * -1);
        }
    }

    @Override
    public void batchCopy(List<String> sourcesIds, String targetId, User user) {
        // 对待操作文件和文件夹与目标目录进行校验
        FileOperateValidator validator = validator(sourcesIds, targetId, user.getId(), "复制");

        // 需要复制的文件列表
        List<FileMetadata> copiedFiles = new ArrayList<>();
        // 需要复制的文件与文件块的关联关系列表
        List<FileBlock> copiedBlocks = new ArrayList<>();

        Queue<FileMetadata> files = new ArrayDeque<>(validator.getSources());
        Map<String, FileMetadata> historyThisMap = new HashMap<>(0);

        // 记录本次操作的文件的总空间占用大小，以判断账户剩余空间是否允许此操作
        long total = user.getUsedSpaceSize();

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
            // 判断目标目录中是否有同样名字的文件名。如果有，则需要加后缀数字
            o.setName(calculateName(futureParent.getId(), o.getName(), o.getType()));
            copiedFiles.add(file);
            total += file.getSize();
            // 如果是文件的话，那么查询所有文件块的关联数据
            if (FileTypeEnum.FILE.is(o.getType())) {
                List<FileBlock> fileBlocks = fileBlockService.queryBlocks(o.getId());
                fileBlocks.forEach(value -> value.setId(IdTools.fastSimpleUuid()).setFileId(file.getId()));
                copiedBlocks.addAll(fileBlocks);
                // 增加用户账户空间已用量
                userMetaService.updateSpaceSize(user, file.getSize());
            }
            // 如果是文件夹的话，需要查询所有子级的文件及文件夹。并加入队列中，后续统一进行入库
            if (FileTypeEnum.DIR.is(o.getType())) {
                // 将生成的文件元数据记录到 map 中
                historyThisMap.put(o.getId(), file);
                files.addAll(fileMetadataService.queryListByPid(o.getId(), user.getId()));
            }
        }

        // 要操作的文件总大小已超过账户可用空间大小，无法继续操作
        if (total > user.getTotalSpaceSize()) {
            ExceptionTools.businessLogger("您的空间容量剩余不足，操作失败");
        }

        fileMetadataService.batchInsert(copiedFiles);
        fileBlockService.batchInsert(copiedBlocks);
    }

    @Override
    public void batchMove(List<String> sourcesIds, String targetId, String userId) {
        // 对待操作文件和文件夹与目标目录进行校验
        FileOperateValidator validator = validator(sourcesIds, targetId, userId, "移动");

        // 检测待操作文件是否已在目标目录下
        for (FileMetadata file : validator.getSources()) {
            if (file.getPid().equals(targetId)) {
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
                // 判断目标目录中是否有同样名字的文件名。如果有，则需要加后缀数字
                o.setName(calculateName(futureParent.getId(), o.getName(), o.getType()));
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

    @Override
    public void rollbackTrash(List<String> idsList, User user) {
        // 查询账户根目录的文件 id
        FileMetadata root = fileMetadataService.queryByPid(StrUtil.EMPTY, user.getId());

        // 循环恢复所有需要恢复的文件及文件夹
        List<FileMetadata> files = new ArrayList<>();
        // 还原的文件大小
        long total = 0;
        for (String id : idsList) {
            FileMetadata file = fileMetadataService.queryByIdInDeleted(id, user.getId());
            if (FileTypeEnum.DIR.is(file.getType())) {
                // 如果是文件夹，则查询文件夹下所有未删除的文件及文件夹，以修改祖级文件 id
                List<FileMetadata> childFiles = fileMetadataService.queryChildListByPid(file.getId(), user.getId(), Boolean.FALSE);
                for (FileMetadata o : childFiles) {
                    List<String> ancestors = o.queryAncestors();
                    int index = ancestors.indexOf(file.getId());
                    ancestors = ListUtil.sub(ancestors, index, ancestors.size());
                    ancestors.add(0, root.getId());
                    files.add(o.setterAncestors(ancestors));
                    if (FileTypeEnum.FILE.is(o.getType())) {
                        total += o.getSize();
                    }
                }
                file.setIsDelete(YesOrNoEnum.NO.code());
                files.add(file.setterAncestors(Collections.singletonList(root.getId())));
            }
            if (FileTypeEnum.FILE.is(file.getType())) {
                file.setIsDelete(YesOrNoEnum.NO.code());
                files.add(file.setterAncestors(Collections.singletonList(root.getId())));
                total += file.getSize();
            }
        }

        // 批量修改文件元数据
        fileMetadataService.batchUpdate(files);

        // 更新账户的空间容量大小
        userMetaService.updateSpaceSize(user, total);
    }

    @Override
    public void rename(String id, String name) {
        // 检测文件是否存在
        FileMetadata file = fileMetadataService.queryById(id);
        if (ObjectUtil.isNull(file)) {
            ExceptionTools.noDataLogger();
        }

        // 检测当前目录下是否有重复的文件名称
        if (validDuplicatedName(id, file.getPid(), name, file.getType())) {
            ExceptionTools.businessLogger("文件名已使用，请更换");
        }

        // 修改文件名
        fileMetadataService.updateById(new FileMetadata().setId(id).setName(name));
    }

    /**
     * 移动、复制时的文件校验
     *
     * @param sources 待操作文件
     * @param target  目标目录
     * @param userId  文件所属用户 id
     * @return result
     */
    private FileOperateValidator validator(List<String> sources, String target, String userId, String operation) {
        // 检测是否有选中的文件，如果没有选择任何一个文件，那么无法执行后续操作
        if (sources.isEmpty()) {
            ExceptionTools.businessLogger("请选择要" + operation + "的文件");
        }

        // 检测是否已选择目标目录
        if (CharSequenceUtil.isBlank(target)) {
            ExceptionTools.businessLogger("请选择目标目录");
        }

        // 检测选择的目标目录是否是待操作文件
        if (sources.contains(target)) {
            ExceptionTools.businessLogger("无法" + operation + "至待" + operation + "文件下");
        }

        // 将目标目录与待操作文件 id 一同进行查询，减少查库次数
        sources.add(target);
        List<FileMetadata> files = fileMetadataService.queryListByIds(sources, userId);

        // 获取目标目录元数据
        List<FileMetadata> findTargets = files.stream().filter(o -> o.getId().equals(target)).collect(Collectors.toList());
        FileMetadata fileTarget = findTargets.isEmpty() ? null : findTargets.get(0);

        // 校验目标目录是否存在
        if (ObjectUtil.isNull(fileTarget) || YesOrNoEnum.YES.is(fileTarget.getIsDelete())) {
            ExceptionTools.businessLogger("目标目录不存在，请刷新后重试");
        }

        // 检测目标文件是否是目录
        if (ObjectUtil.isNotNull(fileTarget) && FileTypeEnum.FILE.is(fileTarget.getType())) {
            ExceptionTools.businessLogger("无法" + operation + "至文件，请选择文件夹");
        }

        // 校验待操作文件是否都存在
        files.removeIf(o -> o.getId().equals(target));
        sources.removeIf(o -> o.equals(target));
        if (files.size() != sources.size()) {
            ExceptionTools.businessLogger("有文件不存在，请刷新后重试");
        }

        // 检测目标目录是否为待移动文件或文件夹的子级目录
        if (ObjectUtil.isNotNull(fileTarget)) {
            List<String> ancestors = fileTarget.addIdInAncestors();
            for (FileMetadata source : files) {
                if (FileTypeEnum.DIR.is(source.getType()) && ancestors.contains(source.getId())) {
                    ExceptionTools.businessLogger("文件无法" + operation + "至子级目录下");
                }
            }
        }
        return new FileOperateValidator().setTarget(fileTarget).setSources(files);
    }

    @Override
    public FileMetadataView queryById(String id, String userId) {
        return CharSequenceUtil.isBlank(id) ?
                FileMetadataView.convert(fileMetadataService.queryByPid(CharSequenceUtil.EMPTY, userId)) :
                FileMetadataView.convert(fileMetadataService.queryById(id, userId));
    }

    @Override
    public List<BreadsView> queryBreads(String id, String userId) {
        if (StrUtil.UNDERLINE.equals(id)) {
            FileMetadata file = fileMetadataService.queryByPid(StrUtil.EMPTY, userId);
            return Collections.singletonList(BreadsView.convert(file));
        }
        FileMetadata file = fileMetadataService.queryById(id, userId);
        List<String> ancestors = file.queryAncestors();
        if (ancestors.isEmpty()) {
            return BreadsView.convert(Collections.singletonList(file));
        }
        List<FileMetadata> files = fileMetadataService.queryListByIds(ancestors, userId);
        files.add(file);
        return BreadsView.convert(files);
    }

    @Override
    public PagerView<FileMetadataView> queryDeletedFiles(TrashQuery query, String userId) {
        List<FileMetadata> files = fileMetadataService.queryDeletedFiles(query.getPageIndex(), query.getPageSize(), userId);
        Integer count = fileMetadataService.queryDeletedCount(userId);
        List<FileMetadataView> views = files.stream().map(FileMetadataView::convert).collect(Collectors.toList());
        return PagerView.initial(count, views);
    }

    @Override
    public PagerView<FileMetadataView> queryFiles(FileSearcher searcher, String userId) {
        PagerView<FileMetadata> filePager = fileMetadataService.queryFilesInPager(searcher.getPid(), searcher.getName(), userId,
                YesOrNoEnum.YES.is(searcher.getIsOnlyDir()), searcher.getPageIndex(), searcher.getPageSize());
        List<FileMetadataView> views = filePager.getData().stream().map(FileMetadataView::convert).collect(Collectors.toList());
        return new PagerView<FileMetadataView>().setData(views).setTotal(filePager.getTotal());
    }

    @Override
    public List<FileMetadataView> queryDirs(DirLooker looker, String userId) {
        String pid = looker.getPid();
        if (StrUtil.UNDERLINE.equals(looker.getPid())) {
            pid = fileMetadataService.queryByPid(StrUtil.EMPTY, userId).getId();
        }
        Long createTime = StrUtil.isBlank(looker.getId()) ? 0L : fileMetadataService.queryById(looker.getId()).getCreateTime();
        List<FileMetadata> dirs = fileMetadataService.queryListAfter(pid, createTime, looker.getSize(), userId, FileTypeEnum.DIR);
        return dirs.stream().map(FileMetadataView::convert).collect(Collectors.toList());
    }

    @Override
    public Integer calculateSuffixNumber(String pid, String name, Integer fileType) {
        List<FileMetadata> tmp = fileMetadataService.queryLikePrefix(pid, name, FileTypeEnum.findType(fileType));
        return FileMetadata.calculateFileSuffixNumber(tmp, name);
    }

    @Override
    public String calculateName(String pid, String name, Integer fileType) {
        Integer fileNumber = calculateSuffixNumber(pid, name, fileType);
        return fileNumber == 0 ? name : name + AppConstants.LEFT_BRACKET + fileNumber + AppConstants.RIGHT_BRACKET;
    }

    @Override
    public Boolean validDuplicatedName(String id, String pid, String name, Integer fileType) {
        List<FileMetadata> tmp = fileMetadataService.queryLikePrefix(pid, name, FileTypeEnum.findType(fileType));
        tmp.removeIf(o -> o.getId().equals(id));
        return FileMetadata.calculateFileSuffixNumber(tmp, name) > 0;
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
                .map(o -> SystemTools.systemPath() + blockStorageMap.get(o.getBlockId()))
                .collect(Collectors.toList());
        String filePath = SystemTools.systemPath() + AppConstants.Uploader.TMP_PATH;
        FileTools.combineFile(filePath + file.getHash(), storages);

        // 下载文件
        FileTools.downloadResponse(filePath + file.getHash(), file.getName() + file.getExt(), response);

        // 删除临时目录中的文件
        FileUtil.del(filePath + file.getHash());
    }

}
