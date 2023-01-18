package online.yangcloud.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import online.yangcloud.common.SystemRecognition;
import online.yangcloud.common.constants.AppConstants;
import online.yangcloud.enumration.FileTypeEnum;
import online.yangcloud.enumration.YesOrNoEnum;
import online.yangcloud.exception.BusinessException;
import online.yangcloud.model.ao.file.BlockUpload;
import online.yangcloud.model.mapper.FileBlockMapper;
import online.yangcloud.model.po.BlockMetadata;
import online.yangcloud.model.po.FileBlock;
import online.yangcloud.model.po.FileMetadata;
import online.yangcloud.model.vo.file.FileMetadataView;
import online.yangcloud.service.BlockMetadataService;
import online.yangcloud.service.FileBlockService;
import online.yangcloud.service.FileMetadataService;
import online.yangcloud.utils.RedisUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static online.yangcloud.service.impl.FileMetadataServiceImpl.calculateFileNumber;

/**
 * @author zhuboyang
 * @since 2022年12月31 21:15:35
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FileBlockServiceImpl implements FileBlockService {

    private static final Logger logger = LoggerFactory.getLogger(FileBlockServiceImpl.class);

    @Autowired
    private FileMetadataService fileMetadataService;

    @Autowired
    private BlockMetadataService blockMetadataService;

    @Autowired
    private FileBlockMapper fileBlockMapper;

    @Autowired
    private SystemRecognition systemRecognition;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public void recordBlockMetadata(BlockUpload upload) {
        // 将文件块数据入库
        BlockMetadata block = new BlockMetadata()
                .setId(IdUtil.fastSimpleUUID())
                .setHash(upload.getHash())
                .setStoragePath(systemRecognition.generateBlockStoragePath() + File.separator + upload.getHash())
                .setBlockSize(upload.getBlockSize());
        block = blockMetadataService.insertOne(block);

        // 将文件块数据加入到 redis 集合中
        upload.setFile(null);
        String redisValue = JSONUtil.toJsonStr(upload);
        redisUtil.zSetAdd(AppConstants.FILE_BLOCK_UPLOAD_PREFIX + upload.getIdentifier(), redisValue, upload.getBlockIndex().doubleValue());
    }

    @Override
    public void uploadFileBlock(BlockUpload upload) throws IOException {
        // 检测当前文件块是否已存到硬盘上。如果没有，则需要存到磁盘上，并入库
        RLock blockUploadLock = redissonClient.getLock("upload_file_block_lock:" + upload.getIdentifier());
        try {
            blockUploadLock.lock();
            BlockMetadata block = blockMetadataService.queryByHash(upload.getHash());
            if (ObjUtil.isNull(block)) {
                upload.getFile().transferTo(new File(systemRecognition.generateBlockStoragePath() + File.separator + upload.getHash()));
            }
            block = new BlockMetadata()
                    .setId(IdUtil.fastSimpleUUID())
                    .setHash(upload.getHash())
                    .setStoragePath(systemRecognition.generateBlockStoragePath() + File.separator + upload.getHash())
                    .setBlockSize(upload.getBlockSize());
            block = blockMetadataService.insertOne(block);

            // 将文件块数据加入到 redis 集合中，便于入库文件元数据，使用 redisson 分布式锁解决并发
            upload.setFile(null);
            String redisValue = JSONUtil.toJsonStr(upload);
            redisUtil.zSetAdd(AppConstants.FILE_BLOCK_UPLOAD_PREFIX + upload.getIdentifier(), redisValue, upload.getBlockIndex().doubleValue());
        } finally {
            blockUploadLock.unlock();
        }
    }

    @Override
    public FileMetadataView mergeFile(String identifier, String hash) {
        // 获取已经上传的文件块
        List<String> uploadedFileBlock = redisUtil.zSetRange(AppConstants.FILE_BLOCK_UPLOAD_PREFIX + identifier, 0D, Double.MAX_VALUE);

        // 解析已经上传的文件块
        List<BlockUpload> uploadedBlocks
                = uploadedFileBlock.stream().map(block -> JSONUtil.toBean(block, BlockUpload.class)).collect(Collectors.toList());

        // 查询父级目录元数据
        BlockUpload blockUpload = uploadedBlocks.get(0);
        FileMetadata parent = fileMetadataService.queryById(blockUpload.getPid());

        // 查询与文件夹名称有关的文件夹
        String fileName = blockUpload.getFileName().substring(0, blockUpload.getFileName().lastIndexOf(StrUtil.DOT));
        List<FileMetadata> files = fileMetadataService.queryLikePrefix(blockUpload.getPid(), fileName, FileTypeEnum.FILE);

        // 计算存储文件的文件名后的后缀数字
        int fileNumber = calculateFileNumber(files, fileName);

        // 封装文件元数据并入库
        String fileExt = FileUtil.extName(blockUpload.getFileName());
        FileMetadata file = new FileMetadata()
                .setId(IdUtil.fastSimpleUUID())
                .setPid(blockUpload.getPid())
                .setName(fileNumber == 0 ? fileName : fileName + AppConstants.LEFT_BRACKET + fileNumber + AppConstants.RIGHT_BRACKET)
                .setHash(hash)
                .setExt(fileExt)
                .setPath(systemRecognition.generateFileStoragePath() + hash)
                .setType(FileTypeEnum.FILE.getCode())
                .setSize(blockUpload.getFileSize())
                .setAncestors(CharSequenceUtil.isBlank(parent.getAncestors()) ? parent.getId() : parent.getAncestors() + StrUtil.COMMA + parent.getId())
                .setUploadTime(DateUtil.date())
                .setUpdateTime(DateUtil.date());
        file = fileMetadataService.insertOne(file);

        // 查询文件块元数据
        List<String> blockHashList = uploadedBlocks.stream().map(BlockUpload::getHash).collect(Collectors.toList());
        List<BlockMetadata> blocks = blockMetadataService.queryByHashList(blockHashList);
        Map<String, String> blockHashIdMap = blocks.stream().collect(Collectors.toMap(BlockMetadata::getHash, BlockMetadata::getId));

        // 记录文件元数据与文件块元数据的关联
        List<FileBlock> fileBlocks = new ArrayList<>();
        for (BlockUpload block : uploadedBlocks) {
            fileBlocks.add(new FileBlock()
                    .setId(IdUtil.fastSimpleUUID())
                    .setFileId(file.getId())
                    .setBlockId(blockHashIdMap.get(block.getHash()))
                    .setFileSize(block.getFileSize())
                    .setBlockCount(block.getBlockCount())
                    .setShardingSize(block.getShardingSize())
                    .setShard(block.getShard() ? YesOrNoEnum.YES.getCode() : YesOrNoEnum.NO.getCode()));
        }
        int updateResult = fileBlockMapper.insertBatchWithPk(fileBlocks);
        if (updateResult == 0) {
            logger.error("文件[{}]合并失败", blockUpload.getFileName());
            throw new BusinessException("文件合并失败");
        }

        return BeanUtil.copyProperties(file, FileMetadataView.class);

//        // 文件合并
//        List<String> hashStoragePaths =
//                blocks.stream().map(block -> systemRecognition.generateBlockStoragePath() + block.getStoragePath()).collect(Collectors.toList());
//        FileUtils.merge(file.getPath(), hashStoragePaths);
    }

}
