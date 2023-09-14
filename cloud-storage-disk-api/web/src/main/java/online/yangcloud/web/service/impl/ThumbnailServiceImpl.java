package online.yangcloud.web.service.impl;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import online.yangcloud.common.annotation.TimeConsuming;
import online.yangcloud.common.common.AppConstants;
import online.yangcloud.common.model.FileMetadata;
import online.yangcloud.common.model.VideoMetadata;
import online.yangcloud.common.tools.FfmpegTools;
import online.yangcloud.common.tools.FileTools;
import online.yangcloud.common.tools.IdTools;
import online.yangcloud.common.tools.SystemTools;
import online.yangcloud.web.service.ThumbnailService;
import online.yangcloud.web.service.meta.VideoMetadataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhuboyang
 * @since 2023年09月07 10:16:41
 */
@Service
@Transactional(rollbackFor = Exception.class)
@TimeConsuming
public class ThumbnailServiceImpl implements ThumbnailService {
    
    private static final Logger logger = LoggerFactory.getLogger(ThumbnailServiceImpl.class);

    @Resource
    private VideoMetadataService videoMetadataService;

    @Override
    public void thumbnail(FileMetadata metadata) {
        // 上传的文件是个视频
        logger.info("----------------------------------------");
        logger.info("ext -> {}", metadata.getExt());
        logger.info("----------------------------------------");
        if (FileTools.isVideo(metadata.getExt())) {
            videoThumbnail(metadata);
        }
    }

    private void videoThumbnail(FileMetadata metadata) {
        String filePath = SystemTools.systemPath() + AppConstants.Uploader.FILE + metadata.getId() + metadata.getExt();

        // 获取视频详细信息
        VideoMetadata video = FfmpegTools.getVideoInfo(filePath);

        // 截取视频的第一帧，用作缩略图
        String thumbnail = AppConstants.Uploader.SNAPSHOT + metadata.getId() + ".jpg";
        if (!FileUtil.exist(FileUtil.file(SystemTools.systemPath() + thumbnail).getParent())) {
            FileUtil.mkdir(FileUtil.file(SystemTools.systemPath() + thumbnail).getParent());
        }
        FfmpegTools.splitFirstPicture(filePath, SystemTools.systemPath() + thumbnail);

        // 压缩图片大小
        float scale = AppConstants.Icon.DEFAULT_WIDTH / Float.parseFloat(String.valueOf(video.getWidth()));
        String output = AppConstants.Uploader.SNAPSHOT + metadata.getId() + "_output.jpg";
        ImgUtil.scale(FileUtil.file(SystemTools.systemPath() + thumbnail), FileUtil.file(SystemTools.systemPath() + output), scale);
        FileUtil.rename(FileUtil.file(SystemTools.systemPath() + output), SystemTools.systemPath() + thumbnail, Boolean.TRUE);

        // 补充视频详细信息
        video.setId(IdTools.fastSimpleUuid()).setThumbnail(thumbnail).setFileId(metadata.getId());
        logger.info("----------------------------------------");
        logger.info("video metadata -> {}", video);
        logger.info("----------------------------------------");
        videoMetadataService.addVideoRecord(video);

        // 删除整文件
        FileUtil.del(filePath);
    }

    @Override
    public String queryThumbnail(FileMetadata file) {
        if (FileTools.isVideo(file.getExt())) {
            VideoMetadata video = videoMetadataService.queryVideoByFileId(file.getId());
            return ObjectUtil.isNull(video) ? StrUtil.EMPTY : video.getThumbnail();
        }
        return StrUtil.EMPTY;
    }

    @Override
    public Map<String, VideoMetadata> queryThumbnails(List<FileMetadata> files) {
        Map<String, VideoMetadata> maps = new HashMap<>(files.size());

        // 查询各类文件元数据并封装映射
        Map<String, List<FileMetadata>> fileMap = files.stream().collect(Collectors.groupingBy(FileMetadata::getExt));

        // 整理视频文件的元数据映射
        List<String> fileIds = new ArrayList<>();
        for (String ext : FileTools.VIDEO_EXT) {
            List<FileMetadata> videos = fileMap.get(ext);
            if (ObjectUtil.isNotNull(videos) && !videos.isEmpty()) {
                fileIds.addAll(videos.stream().map(FileMetadata::getId).collect(Collectors.toList()));
            }
        }
        if (!fileIds.isEmpty()) {
            List<VideoMetadata> videos = videoMetadataService.queryVideosByFileIds(fileIds);
            maps.putAll(videos.stream().collect(Collectors.toMap(VideoMetadata::getFileId, o -> o)));
        }

        return maps;
    }

}
