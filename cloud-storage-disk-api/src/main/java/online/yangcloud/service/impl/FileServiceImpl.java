package online.yangcloud.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.*;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import online.yangcloud.common.ResultBean;
import online.yangcloud.common.SystemRecognition;
import online.yangcloud.common.constants.AppConstants;
import online.yangcloud.common.resultcode.AppResultCode;
import online.yangcloud.enumration.FileCategoryEnum;
import online.yangcloud.enumration.FileTypeEnum;
import online.yangcloud.enumration.YesOrNoEnum;
import online.yangcloud.exception.BusinessException;
import online.yangcloud.mapper.BlockMetadataMapper;
import online.yangcloud.mapper.FileBlockMapper;
import online.yangcloud.mapper.FileMetadataMapper;
import online.yangcloud.model.BlockMetadata;
import online.yangcloud.model.FileBlock;
import online.yangcloud.model.FileMetadata;
import online.yangcloud.model.User;
import online.yangcloud.model.ao.file.BlockUploader;
import online.yangcloud.model.ao.file.FileRenameRequest;
import online.yangcloud.model.ao.file.FileSearchRequest;
import online.yangcloud.model.bo.FileOperationValidate;
import online.yangcloud.model.vo.file.FileBreadView;
import online.yangcloud.model.vo.file.FileMetadataView;
import online.yangcloud.model.vo.file.FilePlayView;
import online.yangcloud.service.FileService;
import online.yangcloud.utils.FileTools;
import online.yangcloud.utils.IdTools;
import online.yangcloud.utils.RedisTools;
import online.yangcloud.wrapper.FileMetadataQuery;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.info.MultimediaInfo;

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
    private FileMetadataMapper fileMetadataMapper;

    @Resource
    private BlockMetadataMapper blockMetadataMapper;

    @Resource
    private FileBlockMapper fileBlockMapper;

    @Resource
    private SystemRecognition systemRecognition;

    @Resource
    private RedisTools redisTools;

    @Resource
    private RedissonClient redissonClient;

    @Override
    public Boolean checkBlocksExist(BlockUploader uploader) {
        // 查询块是否已上传至数据库中
        BlockMetadata blockMetadata = blockMetadataMapper.findOne(blockMetadataMapper.query()
                .where.hash().eq(uploader.getHash()).and.isDelete().eq(YesOrNoEnum.NO.getCode()).end());
        if (ObjectUtil.isNull(blockMetadata)) {
            return Boolean.FALSE;
        }

        // 查询块是否存在磁盘上
        File block = FileUtil.file(systemRecognition.generateSystemPath() + AppConstants.BLOCK_UPLOAD_PATH + uploader.getHash());
        if (!FileUtil.exist(block)) {
            // 如果磁盘上没有此文件块，那么就将文件块的元数据设置为已删除状态
            blockMetadataMapper.updateBy(blockMetadataMapper.updater()
                    .set.isDelete().is(YesOrNoEnum.YES.getCode()).end()
                    .where.id().eq(blockMetadata.getId()).end());
            return Boolean.FALSE;
        }
        String redisValue = JSONUtil.toJsonStr(uploader);
        double blockIndex = uploader.getBlockIndex().doubleValue();
        redisTools.zSetAdd(AppConstants.FILE_BLOCK_UPLOAD_PREFIX + uploader.getIdentifier(), redisValue, blockIndex);
        return Boolean.TRUE;
    }

    @Override
    public void uploadFileBlock(BlockUploader upload) throws IOException {
        // 检测当前文件块是否已存到硬盘上。如果没有，则需要存到磁盘上，并入库
        RLock blockUploadLock = redissonClient.getLock("upload_file_block_lock:" + upload.getIdentifier());
        try {
            blockUploadLock.lock();
            BlockMetadata block = blockMetadataMapper.findOne(blockMetadataMapper.query()
                    .where.hash().eq(upload.getHash()).and.isDelete().eq(YesOrNoEnum.NO.getCode()).end());

            if (ObjUtil.isNull(block)) {
                String uploadPath = systemRecognition.generateSystemPath() + AppConstants.BLOCK_UPLOAD_PATH;
                if (!FileUtil.exist(uploadPath)) {
                    FileUtil.mkdir(uploadPath);
                }
                upload.getFile().transferTo(new File(systemRecognition.generateSystemPath() + AppConstants.BLOCK_UPLOAD_PATH + upload.getHash()));

                block = new BlockMetadata()
                        .setId(IdUtil.fastSimpleUUID())
                        .setHash(upload.getHash())
                        .setStoragePath(AppConstants.BLOCK_UPLOAD_PATH + upload.getHash())
                        .setBlockSize(upload.getBlockSize());
                blockMetadataMapper.insertWithPk(block);
            }

            // 将文件块数据加入到 redis 集合中，便于入库文件元数据，使用 redisson 分布式锁解决并发
            upload.setFile(null);
            String redisValue = JSONUtil.toJsonStr(upload);
            redisTools.zSetAdd(AppConstants.FILE_BLOCK_UPLOAD_PREFIX + upload.getIdentifier(), redisValue, upload.getBlockIndex().doubleValue());
        } finally {
            blockUploadLock.unlock();
        }
    }

    @Override
    public FileMetadataView mergeFile(String identifier, String hash, User user) throws IOException {
        // 1. 获取 redis 中存着的文件块数据
        // 2. 将数据转为文件块上传参数列表和文件块 hash 列表
        List<String> blockUploaderJsons = redisTools.zSetRange(AppConstants.FILE_BLOCK_UPLOAD_PREFIX + identifier, 0D, Double.MAX_VALUE);
        List<BlockUploader> blockUploaderList = new ArrayList<>();
        List<String> blockHashList = new ArrayList<>();
        blockUploaderJsons.forEach(o -> {
            BlockUploader uploader = JSONUtil.toBean(o, BlockUploader.class);
            blockUploaderList.add(uploader);
            blockHashList.add(uploader.getHash());
        });

        // 1. 从上传的文件块参数中获取第一个个元素
        // 2. 查询上传的目录下所有含有上传文件名相关的文件
        // 3. 计算存储文件的文件名后的后缀数字
        BlockUploader uploader = blockUploaderList.get(0);
        List<FileMetadata> files = queryLikePrefix(uploader.getPid(), uploader.getFileName(), FileTypeEnum.FILE, user);
        int fileNumber = calculateFileNumber(files, uploader.getFileName());

        // 查询并将文件块的元数据转为 map，方便后续生成文件块的存储路径，用以合并文件
        List<BlockMetadata> blocks = blockMetadataMapper.listEntity(blockMetadataMapper.query()
                .where.hash().in(blockHashList).and.isDelete().eq(YesOrNoEnum.NO.getCode()).end());
        Map<String, BlockMetadata> blocksHashMap = blocks.stream().collect(Collectors.toMap(BlockMetadata::getHash, o -> o));

        // 生成文件 id，用以存储文件与文件块之间的关联关系
        String fileId = IdTools.fastSimpleUuid();
        blockUploaderList.sort(Comparator.comparingInt(BlockUploader::getBlockIndex));
        List<FileBlock> fileBlocks = blockUploaderList.stream()
                .map(o -> FileBlock.initial(fileId, o, blocksHashMap.get(o.getHash())))
                .collect(Collectors.toList());
        int updateResult = fileBlockMapper.insertBatchWithPk(fileBlocks);
        if (updateResult != fileBlocks.size()) {
            throw new BusinessException(AppResultCode.FAILURE.getMessage());
        }

        // 将文件块合并成文件，用以生成文件 hash
        List<String> blockPaths = blocks.stream()
                .map(o -> systemRecognition.generateSystemPath() + o.getStoragePath())
                .collect(Collectors.toList());
        String filePath = systemRecognition.generateSystemPath() + AppConstants.FILE_UPLOAD_PATH + uploader.getIdentifier();
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
        FileMetadata parent = fileMetadataMapper.findById(uploader.getPid());
        FileMetadata file = FileMetadata.initial(fileId, fileNumber, fileHash, parent, uploader, user);
        updateResult = fileMetadataMapper.insertWithPk(file);
        if (updateResult == 0) {
            throw new BusinessException(AppResultCode.FAILURE.getMessage());
        }
        return BeanUtil.copyProperties(file, FileMetadataView.class);
    }

    @Override
    public List<FileMetadata> queryLikePrefix(String pid, String fileName, FileTypeEnum type, User user) {
        return fileMetadataMapper.listEntity(fileMetadataMapper.query()
                .where.pid().eq(pid)
                .and.name().like(fileName.trim() + AppConstants.PERCENT)
                .and.type().eq(type.getCode())
                .and.userId().eq(user.getId())
                .and.isDelete().eq(YesOrNoEnum.NO.getCode()).end());
    }

    @Override
    public void initUserFile(String userId) {
        FileMetadata file = FileMetadata.initRoot(userId);
        insertOne(file);
    }

    @Override
    public FileMetadata insertOne(FileMetadata file) {
        int updateResult = fileMetadataMapper.insertWithPk(file);
        if (updateResult == 0) {
            throw new BusinessException("文件添加失败，请重试");
        }
        return file;
    }

    @Override
    public FileMetadataView mkdir(String pid, String fileName, User user) {
        // 查询父级目录的数据，以便获取所有父级目录 id 信息
        FileMetadata parent = fileMetadataMapper.findById(pid);
        if (ObjUtil.isNull(parent) || !parent.getUserId().equals(user.getId())) {
            logger.error("父级目录不存在，操作失败，请重试");
            throw new BusinessException("父级目录不存在，操作失败，请重试");
        }

        // 查询与文件夹名称有关的文件夹
        List<FileMetadata> existDirectories = queryLikePrefix(pid, fileName, FileTypeEnum.DIR, user);

        // 计算存储文件的文件名后的后缀数字
        int fileNumber = calculateFileNumber(existDirectories, fileName);

        // 封装文件元数据并入库
        FileMetadata file = new FileMetadata()
                .setId(IdUtil.fastSimpleUUID())
                .setPid(pid)
                .setName(fileNumber == 0 ? fileName : fileName + AppConstants.LEFT_BRACKET + fileNumber + AppConstants.RIGHT_BRACKET)
                .setExt(CharSequenceUtil.EMPTY)
                .setHash(CharSequenceUtil.EMPTY)
                .setPath(CharSequenceUtil.EMPTY)
                .setType(FileTypeEnum.DIR.getCode())
                .setSize(0L)
                .setAncestors(CharSequenceUtil.isBlank(parent.getAncestors()) ? parent.getId() : parent.getAncestors() + StrUtil.COMMA + parent.getId())
                .setUploadTime(DateUtil.date())
                .setUpdateTime(DateUtil.date())
                .setUserId(user.getId());
        int updateResult = fileMetadataMapper.insertWithPk(file);
        if (updateResult == 0) {
            logger.error("文件夹新建失败，请重试");
            throw new BusinessException("文件夹新建失败，请重试");
        }

        // 返回视图数据
        return BeanUtil.copyProperties(file, FileMetadataView.class);
    }

    @Override
    public ResultBean<?> batchDeleteFile(List<String> fileIds, User user) {
        // 检测存在的文件
        List<FileMetadata> files = fileMetadataMapper.listEntity(fileMetadataMapper.query()
                .where.id().in(fileIds)
                .and.isDelete().eq(YesOrNoEnum.NO.getCode())
                .and.userId().eq(user.getId()).end());
        fileIds = files.stream().map(FileMetadata::getId).collect(Collectors.toList());

        // 删除文件
        int updateResult = fileMetadataMapper.updateBy(fileMetadataMapper.updater()
                .set.isDelete().is(YesOrNoEnum.YES.getCode()).end()
                .where.id().in(fileIds).end());
        if (updateResult != fileIds.size()) {
            logger.error("文件删除失败，请重试");
            throw new BusinessException("文件删除失败，请重试");
        }

        // 查询文件对应的，文件与文件块的关联记录
        List<FileBlock> fileBlocks = fileBlockMapper.listEntity(fileBlockMapper.query().where.fileId().in(fileIds).end());
        List<String> fileBlocksIds = fileBlocks.stream().map(FileBlock::getId).collect(Collectors.toList());

        // 删除文件与文件块的关联关系
        if (fileBlocksIds.size() > 0) {
            updateResult = fileBlockMapper.updateBy(fileBlockMapper.updater()
                    .set.isDelete().is(YesOrNoEnum.YES.getCode()).end()
                    .where.id().in(fileBlocksIds).end());
            if (updateResult != fileBlocksIds.size()) {
                logger.error("文件删除失败，请重试.");
                throw new BusinessException("文件删除失败，请重试.");
            }
        }

        // 查询子级的所有文件与文件夹
        List<FileMetadata> childList = new ArrayList<>();
        for (String fileId : fileIds) {
            childList.addAll(fileMetadataMapper.listEntity(fileMetadataMapper.query()
                    .where.ancestors().like(AppConstants.PERCENT + fileId + AppConstants.PERCENT)
                    .and.isDelete().eq(YesOrNoEnum.NO.getCode())
                    .and.userId().eq(user.getId()).end()));
        }
        if (childList.size() == 0) {
            return ResultBean.success();
        }
        List<String> childIds = childList.stream().map(FileMetadata::getId).collect(Collectors.toList());

        // 删除所有子级文件与文件夹
        updateResult =
                fileMetadataMapper.updateBy(fileMetadataMapper.updater().set.isDelete().is(YesOrNoEnum.YES.getCode()).end().where.id().in(childIds).end());
        if (updateResult != childIds.size()) {
            logger.error("文件删除失败，请重新尝试");
            throw new BusinessException("文件删除失败，请重新尝试");
        }

        // 查询子级文件与文件块的关联
        fileBlocks = fileBlockMapper.listEntity(fileBlockMapper.query().where.fileId().in(childIds).and.isDelete().eq(YesOrNoEnum.NO.getCode()).end());
        fileBlocksIds = fileBlocks.stream().map(FileBlock::getId).collect(Collectors.toList());

        // 删除文件与文件块的关联关系
        if (fileBlocksIds.size() > 0) {
            updateResult = fileBlockMapper.updateBy(fileBlockMapper.updater()
                    .set.isDelete().is(YesOrNoEnum.YES.getCode()).end()
                    .where.id().in(fileBlocksIds).end());
            if (updateResult != fileBlocksIds.size()) {
                logger.error("文件删除失败，请重新尝试.");
                throw new BusinessException("文件删除失败，请重新尝试.");
            }
        }
        return ResultBean.success();
    }

    /**
     * 计算文件名名后的后缀数字
     *
     * @param files    计算文件范围列表
     * @param fileName 文件名
     * @return result
     */
    public static int calculateFileNumber(List<FileMetadata> files, String fileName) {
        int fileNumber = 0;
        if (files.size() > 0) {
            // 1. 将文件夹名称刨除掉，过滤掉名称不同的文件夹。如：test（1）切分为 1
            // 2. 将名称刨除掉后，剩下的名称部分，留下是数字的
            // 3. 将数字（字符串）转为数字，并升序排序
            List<Integer> fileNumbers = files.stream()
                    .map(file -> fileName.equals(file.getName()) ?
                            CharSequenceUtil.EMPTY : file.getName().substring(fileName.length() + 1, file.getName().length() - 1))
                    .filter(nameNumber -> CharSequenceUtil.isBlank(nameNumber) || NumberUtil.isInteger(nameNumber))
                    .map(nameNumber -> CharSequenceUtil.isBlank(nameNumber) ? 0 : Integer.parseInt(nameNumber))
                    .sorted(Comparator.comparingInt(Integer::intValue))
                    .collect(Collectors.toList());
            if (fileNumbers.size() > 0) {
                fileNumber = fileNumbers.get(fileNumbers.size() - 1) + 1;
            }
        }
        return fileNumber;
    }

    @Override
    public ResultBean<?> batchMoveFiles(List<String> sources, String target, User user) {
        // 对待操作文件和文件夹与目标目录进行校验
        FileOperationValidate validate = validateFiles(sources, target, user);
        FileMetadata targetFile = validate.getTargetFile();
        List<FileMetadata> sourceFiles = validate.getSourceFiles();

        // 修改待移动文件的父级文件 id 和所有祖级 id
        String ancestor = targetFile.getId();
        if (CharSequenceUtil.isNotBlank(targetFile.getAncestors())) {
            ancestor += StrUtil.COMMA + targetFile.getAncestors();
        }
        int updateResult = fileMetadataMapper.updateBy(fileMetadataMapper.updater()
                .set.pid().is(target).ancestors().is(ancestor).end()
                .where.id().in(sources).end());
        if (updateResult != sourceFiles.size()) {
            logger.error("文件移动失败，请重试");
            throw new BusinessException("文件移动失败，请重试");
        }

        // 修改所有子级文件的祖级 id
        List<FileMetadata> dirFiles = sourceFiles.stream().filter(file -> YesOrNoEnum.YES.is(file.getType())).collect(Collectors.toList());
        for (FileMetadata dir : dirFiles) {
            List<FileMetadata> childList = queryChildFiles(dir.getId());
            for (FileMetadata child : childList) {
                String childAncestor = child.getAncestors();
                String sourceId = sourceFiles.get(0).getId();
                childAncestor = childAncestor.substring(0, childAncestor.indexOf(sourceId) + sourceId.length());
                childAncestor = childAncestor + StrUtil.COMMA + ancestor;
                child.setAncestors(childAncestor);
                updateResult = fileMetadataMapper.updateById(child);
                if (updateResult == 0) {
                    logger.error("文件移动失败，请重新尝试");
                    throw new BusinessException("文件移动失败，请重新尝试");
                }
            }
        }
        return ResultBean.success();
    }

    @Override
    public ResultBean<?> batchCopyFiles(List<String> sources, String target, User user) {
        // 对待操作文件和文件夹与目标目录进行校验
        FileOperationValidate validate = validateFiles(sources, target, user);
        FileMetadata targetFile = validate.getTargetFile();
        List<FileMetadata> sourceFiles = validate.getSourceFiles();

        // 修改呗复制文件的父级文件 id 和所有祖级 id
        String ancestor;
        if (CharSequenceUtil.isBlank(targetFile.getAncestors())) {
            ancestor = targetFile.getId();
        } else {
            ancestor = targetFile.getId() + StrUtil.COMMA + targetFile.getAncestors();
        }

        // 构建复制后的文件及文件夹，并入库
        for (FileMetadata file : sourceFiles) {
            String fileId = file.getId();
            // 查询与文件夹名称有关的文件夹
            List<FileMetadata> existDirectories = queryLikePrefix(target, file.getName(), FileTypeEnum.DIR, user);
            // 计算存储文件的文件名后的后缀数字
            int fileNumber = calculateFileNumber(existDirectories, file.getName());
            file.setId(IdUtil.fastSimpleUUID())
                    .setPid(target)
                    .setAncestors(ancestor)
                    .setName(fileNumber == 0 ? file.getName() : file.getName() + AppConstants.LEFT_BRACKET + fileNumber + AppConstants.RIGHT_BRACKET);
            int updateResult = fileMetadataMapper.insertWithPk(file);
            if (updateResult == 0) {
                logger.error("文件复制失败，请重试");
                throw new BusinessException("");
            }
            // 递归复制下级的所有文件与文件夹
            List<FileMetadata> childList = fileMetadataMapper.listEntity(fileMetadataMapper.query()
                    .where.pid().eq(fileId)
                    .and.isDelete().eq(YesOrNoEnum.NO.getCode()).end());
            copyChildFiles(childList, file, user);
        }
        return ResultBean.success();
    }

    @Override
    public FileMetadata rename(FileRenameRequest renameRequest, User user) {
        // 检测文件是否存在
        FileMetadata file = fileMetadataMapper.findById(renameRequest.getId());
        if (ObjUtil.isNull(file) || YesOrNoEnum.YES.is(file.getId())) {
            logger.error("文件不存在，请重试");
            throw new BusinessException("文件不存在，请重试");
        }

        // 检测是否有权限操作此文件
        if (!user.getId().equals(file.getUserId())) {
            logger.error("您没有权限操作此文件");
            throw new BusinessException("您没有权限操作此文件");
        }

        // 检测是否需要修改数据库中的文件名
        boolean updateFlag = Boolean.FALSE;
        if (CharSequenceUtil.isNotBlank(renameRequest.getName())) {
            if (!file.getName().equals(renameRequest.getName())) {
                file.setName(renameRequest.getName());
                updateFlag = Boolean.TRUE;
                file.setUpdateTime(DateUtil.date());
            }
        }
        if (!updateFlag) {
            return file;
        }

        // 重命名
        int updateResult = fileMetadataMapper.updateById(file);
        if (updateResult == 0) {
            logger.error("文件重命名失败，请重试");
            throw new BusinessException("文件重命名失败，请重试");
        }
        return file;
    }

    @Override
    public FilePlayView findPlayUrl(String fileId) throws EncoderException {
        // 校验文件是否存在
        FileMetadata file = fileMetadataMapper.findById(fileId);
        if (ObjUtil.isNull(file)) {
            logger.error("文件不存在，请重试");
            throw new BusinessException("文件不存在，请重试");
        }

        // 查询文件的所有文件块，以便于合并
        List<FileBlock> fileBlocks = fileBlockMapper.listEntity(fileBlockMapper.query().where.fileId().eq(fileId).end().orderBy.blockIndex().asc().end());
        List<String> blockIds = fileBlocks.stream().map(FileBlock::getBlockId).collect(Collectors.toList());
        List<BlockMetadata> blocks = blockMetadataMapper.listByIds(blockIds);
        Map<String, BlockMetadata> blockMap = blocks.stream().collect(Collectors.toMap(BlockMetadata::getId, block -> block));

        // 合并文件
        fileBlocks.sort(Comparator.comparingInt(FileBlock::getBlockIndex));
        List<String> blockPaths = fileBlocks.stream()
                .map(fileBlock -> systemRecognition.generateSystemPath() + blockMap.get(fileBlock.getBlockId()).getStoragePath())
                .collect(Collectors.toList());
        FilePlayView playView = FilePlayView.packageData(AppConstants.TMP_PATH + file.getHash() + StrUtil.DOT + file.getExt(), file.getExt());
        FileTools.combineFile(systemRecognition.generateSystemPath() + playView.getPath(), blockPaths);

        if (FileUtil.exist(systemRecognition.generateSystemPath() + playView.getPath())) {
            // 如果文件是视频的话，那么就获取到视频的宽高，并生成播放地址
            if (FileTools.determineFileType(FileCategoryEnum.VIDEO, file.getExt())) {
                MultimediaInfo media = new MultimediaObject(FileUtil.file(systemRecognition.generateSystemPath() + playView.getPath())).getInfo();
                JSONObject extendObj = JSONUtil.createObj()
                        .set("width", media.getVideo().getSize().getWidth())
                        .set("height", media.getVideo().getSize().getHeight());
                playView.setExtend(extendObj);
            }
        }
        return playView;
    }

    /**
     * 递归复制文件及文件夹
     *
     * @param sources 待复制文件及文件夹
     * @param target  目标目录
     * @param user    当前登录的用户
     */
    private void copyChildFiles(List<FileMetadata> sources, FileMetadata target, User user) {
        for (FileMetadata source : sources) {
            String fileId = source.getId();
            // 复制文件
            source.setId(IdUtil.fastSimpleUUID())
                    .setPid(target.getId())
                    .setAncestors(target.getId() + StrUtil.COMMA + target.getAncestors());
            int updateResult = fileMetadataMapper.insertWithPk(source);
            if (updateResult == 0) {
                logger.error("文件复制失败，请重新尝试");
                throw new BusinessException("文件复制失败，请重新尝试");
            }
            // 如果是文件夹的话，需要复制文件夹下的所有的文件及文件夹
            if (YesOrNoEnum.YES.is(source.getType())) {
                List<FileMetadata> childList = fileMetadataMapper.listEntity(fileMetadataMapper.query()
                        .where.pid().eq(fileId)
                        .and.userId().eq(user.getId())
                        .and.isDelete().eq(YesOrNoEnum.NO.getCode()).end());
                copyChildFiles(childList, source, user);
            }
        }
    }

    /**
     * 移动、复制时的文件校验
     *
     * @param sources 待操作文件
     * @param target  目标目录
     * @return result
     */
    private FileOperationValidate validateFiles(List<String> sources, String target, User user) {
        // 校验目标目录是否存在
        FileMetadata targetFile = fileMetadataMapper.findById(target);
        if (ObjUtil.isNull(targetFile) || YesOrNoEnum.YES.is(targetFile.getIsDelete()) || !targetFile.getUserId().equals(user.getId())) {
            logger.error("目标目录不存在，请刷新后重试");
            throw new BusinessException("目标目录不存在，请刷新后重试");
        }

        // 检测目标文件是否是目录
        if (YesOrNoEnum.NO.is(targetFile.getType())) {
            logger.error("请选择目录");
            throw new BusinessException("请选择目录");
        }

        // 校验待移动文件是否都存在
        List<FileMetadata> sourceFiles = fileMetadataMapper.listEntity(fileMetadataMapper.query()
                .where.id().in(sources)
                .and.isDelete().eq(YesOrNoEnum.NO.getCode())
                .and.userId().eq(user.getId()).end());
        if (sourceFiles.size() != sources.size()) {
            logger.error("待操作文件非全部存在，请刷新后重试");
            throw new BusinessException("待操作文件非全部存在，请刷新后重试");
        }

        // 检测目标目录是否为待移动文件或文件夹的子级目录
        String targetAncestors = targetFile.getAncestors();
        List<String> targetIdList = CharSequenceUtil.split(targetAncestors, StrUtil.COMMA);
        targetIdList.add(targetFile.getId());
        for (FileMetadata source : sourceFiles) {
            if (YesOrNoEnum.YES.is(source.getType())) {
                if (targetIdList.contains(source.getId())) {
                    logger.error("无法操作至下级目录，请选择其他目录");
                    throw new BusinessException("无法操作至下级目录，请选择其他目录");
                }
            }
        }
        return new FileOperationValidate()
                .setTargetFile(targetFile)
                .setSourceFiles(sourceFiles);
    }

    @Override
    public List<FileBreadView> queryFileBreads(String id, User user) {
        // 查询顶级目录
        if (CharSequenceUtil.isBlank(id)) {
            FileMetadata rootFile = fileMetadataMapper.findOne(fileMetadataMapper.query().where.pid().eq(id).and.userId().eq(user.getId()).end());
            return Collections.singletonList(FileBreadView.packageData(rootFile.getId(), rootFile.getName()));
        }

        // 校验目录是否存在
        FileMetadata file = fileMetadataMapper.findById(id);
        if (ObjUtil.isNull(file) || YesOrNoEnum.YES.is(file.getIsDelete()) || !user.getId().equals(file.getUserId())) {
            logger.error("文件夹不存在，操作失败，请重试");
            throw new BusinessException("文件夹不存在，操作失败，请重试");
        }

        if (CharSequenceUtil.isBlank(file.getAncestors())) {
            // 自己就是最顶级的目录，没有父级目录
            return Collections.singletonList(FileBreadView.packageData(file.getId(), file.getName()));
        }

        // 获取所有的父级目录，并查询父级目录信息
        List<String> parentIds = CharSequenceUtil.split(file.getAncestors(), StrUtil.COMMA);
        List<FileMetadata> parents = fileMetadataMapper.listByIds(parentIds);
        parents.sort((o1, o2) -> DateUtil.compare(o1.getUploadTime(), o2.getUploadTime()));
        List<FileBreadView> breadsViews =
                parents.stream().map(parent -> FileBreadView.packageData(parent.getId(), parent.getName())).collect(Collectors.toList());
        breadsViews.add(FileBreadView.packageData(file.getId(), file.getName()));
        return breadsViews;
    }

    @Override
    public FileMetadata queryById(String id) {
        return fileMetadataMapper.findById(id);
    }

    @Override
    public List<FileMetadataView> queryFiles(FileSearchRequest searchRequest, User user) {
        // 查询起始点文件元数据
        FileMetadata beginFile;
        if (CharSequenceUtil.isBlank(searchRequest.getFileId())) {
            beginFile = fileMetadataMapper.findOne(fileMetadataMapper.query()
                    .where.pid().eq(CharSequenceUtil.EMPTY)
                    .and.userId().eq(user.getId()).end());
        } else {
            beginFile = fileMetadataMapper.findById(searchRequest.getFileId());
            if (!user.getId().equals(beginFile.getUserId())) {
                beginFile = null;
            }
        }
        if (ObjUtil.isNull(beginFile) || YesOrNoEnum.YES.is(beginFile.getIsDelete())) {
            logger.error(AppResultCode.FAILURE.getMessage());
            throw new BusinessException(AppResultCode.FAILURE.getMessage());
        }

        // 构建查询条件
        FileMetadataQuery query = fileMetadataMapper.query()
                .where.id().ne(beginFile.getId())
                .and.uploadTime().ge(beginFile.getUploadTime())
                .and.userId().eq(user.getId())
                .and.isDelete().eq(YesOrNoEnum.NO.getCode()).end()
                .orderBy.uploadTime().asc().end()
                .limit(0, searchRequest.getSize());
        if (CharSequenceUtil.isNotBlank(searchRequest.getPid())) {
            query.where.pid().eq(searchRequest.getPid());
        }
        if (CharSequenceUtil.isNotBlank(searchRequest.getFileName())) {
            query.where.name().like(searchRequest.getFileName().replaceAll(CharSequenceUtil.EMPTY, AppConstants.PERCENT));
        }
        List<FileMetadata> files = fileMetadataMapper.listEntity(query);
        return files.stream()
                .map(file -> BeanUtil.copyProperties(file, FileMetadataView.class))
                .sorted((o1, o2) -> DateUtil.compare(o1.getUploadTime(), o2.getUploadTime())).collect(Collectors.toList());
    }

    @Override
    public List<FileMetadata> queryChildFiles(String fileId) {
        return fileMetadataMapper.listEntity(fileMetadataMapper.query()
                .where.ancestors().like(AppConstants.PERCENT + fileId.trim() + AppConstants.PERCENT)
                .and.isDelete().eq(YesOrNoEnum.NO.getCode()).end());
    }

    @Override
    public List<FileBreadView> queryDirBreads(String parentId) {
        if (CharSequenceUtil.isBlank(parentId)) {
            FileMetadata parent = fileMetadataMapper.findOne(fileMetadataMapper.query().where.pid().eq(parentId).end());
            return Collections.singletonList(BeanUtil.copyProperties(parent, FileBreadView.class));
        }

        // 查询父级目录本身
        FileMetadata parent = fileMetadataMapper.findById(parentId);

        // 查询祖级目录
        List<String> parentIds = CharSequenceUtil.split(parent.getAncestors(), StrUtil.COMMA);
        List<FileMetadata> parentsList = fileMetadataMapper.listEntity(fileMetadataMapper.query()
                .where.id().in(parentIds)
                .and.isDelete().eq(YesOrNoEnum.NO.getCode()).end());

        // 合并父级目录本身与祖级目录
        parentsList.add(parent);

        // 构建视图数据并返回
        return parentsList.stream().map(file -> BeanUtil.copyProperties(file, FileBreadView.class)).collect(Collectors.toList());
    }

    @Override
    public List<FileMetadata> queryDirs(String parentId, Integer size, User user) {
        // 计算父级目录 id
        String pid = CharSequenceUtil.isBlank(parentId) ?
                fileMetadataMapper.findOne(fileMetadataMapper.query().where.pid().eq(CharSequenceUtil.EMPTY).end()).getId() : parentId;

        // 构建查询语句
        FileMetadataQuery query = fileMetadataMapper.query()
                .where.type().eq(YesOrNoEnum.YES.getCode())
                .and.pid().eq(pid)
                .and.isDelete().eq(YesOrNoEnum.NO.getCode())
                .and.userId().eq(user.getId()).end()
                .orderBy.uploadTime().asc().end()
                .limit(0, size);

        // 查询文件夹并返回
        return fileMetadataMapper.listEntity(query);
    }

    @Override
    public void download(String fileId, HttpServletResponse response) {
        // 查询文件的元数据
        FileMetadata fileMetadata = fileMetadataMapper.findById(fileId);
        if (ObjUtil.isNull(fileMetadata)) {
            throw new BusinessException("文件不存在，请查证。");
        }

        // 查询文件的所有文件块
        List<FileBlock> fileBlocks = fileBlockMapper.listEntity(fileBlockMapper.query().where.fileId().eq(fileId).end().orderBy.blockIndex().asc().end());
        List<String> blockIds = fileBlocks.stream().map(FileBlock::getBlockId).collect(Collectors.toList());
        if (blockIds.size() == 0) {
            throw new BusinessException("文件不存在，请查证.");
        }
        List<BlockMetadata> blocks = blockMetadataMapper.listByIds(blockIds);
        Map<String, BlockMetadata> blockMap = blocks.stream().collect(Collectors.toMap(BlockMetadata::getId, block -> block));

        // 获取文件块的所有存储路径
        List<String> paths = fileBlocks.stream()
                .map(block -> systemRecognition.generateSystemPath() + blockMap.get(block.getBlockId()).getStoragePath())
                .collect(Collectors.toList());

        // 合并文件
        String target = systemRecognition.generateSystemPath() + AppConstants.TMP_PATH + fileMetadata.getHash();
        if (FileUtil.exist(target)) {
            if (!FileUtil.exist(systemRecognition.generateSystemPath() + AppConstants.TMP_PATH)) {
                FileUtil.mkdir(systemRecognition.generateSystemPath() + AppConstants.TMP_PATH);
            }
            FileTools.combineFile(target, paths);
        }

        // 下载文件
        FileTools.downloadResponse(target, fileMetadata.getName() + fileMetadata.getExt(), response);
    }

}
