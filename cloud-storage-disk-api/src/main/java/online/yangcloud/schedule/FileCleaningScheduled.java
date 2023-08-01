package online.yangcloud.schedule;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import online.yangcloud.common.constants.AppConstants;
import online.yangcloud.mapper.BlockMetadataMapper;
import online.yangcloud.mapper.FileBlockMapper;
import online.yangcloud.mapper.FileMetadataMapper;
import online.yangcloud.model.BlockMetadata;
import online.yangcloud.model.FileBlock;
import online.yangcloud.model.FileMetadata;
import online.yangcloud.utils.SystemTools;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhuboyang
 * @since 2023年08月01 15:26:07
 */
@Component
public class FileCleaningScheduled {

    @Resource
    private FileMetadataMapper fileMetadataMapper;

    @Resource
    private FileBlockMapper fileBlockMapper;

    @Resource
    private BlockMetadataMapper blockMetadataMapper;

    /**
     * 定时，每天凌晨零点执行。清空数据库中所有文件的记录。并从磁盘上删除文件
     */
    @Scheduled(cron = "0 0 0 * * ? ")
    public void simpleSchedule() {
        // 清空所有文件元数据
        List<FileMetadata> files = fileMetadataMapper.listEntity(fileMetadataMapper.query().selectAll());
        files = files.stream().filter(o -> StrUtil.isNotBlank(o.getPid())).collect(Collectors.toList());
        fileMetadataMapper.deleteByEntityIds(files);

        // 清空所有文件块元数据
        List<BlockMetadata> blocks = blockMetadataMapper.listEntity(blockMetadataMapper.query().selectAll());
        blockMetadataMapper.deleteByEntityIds(blocks);

        // 清空文件与文件块的关联数据
        List<FileBlock> fileBlocks = fileBlockMapper.listEntity(fileBlockMapper.query().selectAll());
        fileBlockMapper.deleteByEntityIds(fileBlocks);

        // 删除磁盘上的文件
        String uploadPath = SystemTools.systemPath() + AppConstants.Uploader.BLOCK_UPLOAD_PATH;
        FileUtil.del(uploadPath);
    }

}
