package online.yangcloud.web.service.impl;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import online.yangcloud.common.annotation.TimeConsuming;
import online.yangcloud.common.common.AppConstants;
import online.yangcloud.common.enumration.OfficeTypeEnum;
import online.yangcloud.common.model.AudioMetadata;
import online.yangcloud.common.model.FileMetadata;
import online.yangcloud.common.model.VideoMetadata;
import online.yangcloud.common.tools.*;
import online.yangcloud.web.processor.OfficeProcessor;
import online.yangcloud.web.service.ThumbnailService;
import online.yangcloud.web.service.meta.AudioMetadataService;
import online.yangcloud.web.service.meta.VideoMetadataService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.office.OfficeException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhuboyang
 * @since 2023年09月07 10:16:41
 */
@Service
@Transactional(rollbackFor = Exception.class)
@TimeConsuming
public class ThumbnailServiceImpl implements ThumbnailService {

    /**
     * 待处理文件队列
     */
    public static final Queue<FileMetadata> FILE = new ArrayDeque<>();

    @Resource
    private VideoMetadataService videoMetadataService;

    @Resource
    private AudioMetadataService audioMetadataService;

    @Resource
    private OfficeProcessor officeProcessor;

    @Resource
    private FileTools fileTools;

    @Override
    public void thumbnail(FileMetadata metadata) {
        while (true) {
            FileMetadata metadata1 = FILE.poll();
            if (ObjectUtil.isNull(metadata1)) {
                ThreadUtil.safeSleep(1000);
                continue;
            }
            // 上传的文件是个视频
            if (fileTools.isVideo(metadata.getExt())) {
                videoInformation(metadata);
            }
            // 上传的文件是个音频
            if (fileTools.isAudio(metadata.getExt())) {
                audioInformation(metadata);
            }
            // 上传的文件是个 office 文档
            if (fileTools.isOffice(metadata.getExt())) {
                officeInformation(metadata);
            }
            FileUtil.del(SystemTools.systemPath() + AppConstants.Uploader.FILE + metadata.getId() + metadata.getExt());
        }
    }

    /**
     * 获取视频详细信息，并对视频第一帧进行截帧
     *
     * @param metadata 文件元数据
     */
    private void videoInformation(FileMetadata metadata) {
        String filePath = SystemTools.systemPath() + AppConstants.Uploader.FILE + metadata.getId() + metadata.getExt();

        // 获取视频详细信息
        VideoMetadata video = FfmpegTools.acquireVideoInfo(filePath);

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
        videoMetadataService.addVideoRecord(video);
    }

    /**
     * 获取音频详细信息
     *
     * @param metadata 文件元数据
     */
    private void audioInformation(FileMetadata metadata) {
        String filePath = SystemTools.systemPath() + AppConstants.Uploader.FILE + metadata.getId() + metadata.getExt();

        // 获取音频详细信息
        AudioMetadata audio = FfmpegTools.acquireAudioMetadata(filePath);
        audio.setId(IdTools.fastSimpleUuid()).setFileId(metadata.getId());

        // 记录音频元数据
        audioMetadataService.addAudioRecord(audio);
    }

    /**
     * 获取 office 文档详细信息
     *
     * @param metadata 文件元数据
     */
    private void officeInformation(FileMetadata metadata) {
        try {
            // 将文档转为 pdf
            String pdfSuffix = DefaultDocumentFormatRegistry.PDF.getExtension();
            String source = SystemTools.systemPath() + AppConstants.Uploader.FILE + metadata.getId() + metadata.getExt();
            String target = SystemTools.systemPath() + AppConstants.Uploader.TMP + metadata.getId() + StrUtil.DOT + pdfSuffix;
            OfficeTypeEnum type = fileTools.isWord(metadata.getExt()) ?
                    OfficeTypeEnum.WORD : fileTools.isPpt(metadata.getExt()) ?
                    OfficeTypeEnum.PPT : OfficeTypeEnum.EXCEL;
            officeProcessor.convertToPdf(source, target, type);

            // 在缩略图目录中创建以文件 id 为名的目录，以存放 pdf 每页生成的图片
            String thumbnailDir = AppConstants.Uploader.SNAPSHOT + metadata.getId();
            if (!FileUtil.exist(SystemTools.systemPath() + thumbnailDir)) {
                FileUtil.mkdir(SystemTools.systemPath() + thumbnailDir);
            }

            // 对 pdf 文件进行切分，并将每页转为图片
            PDDocument document = PDDocument.load(FileUtil.file(target));
            PDFRenderer renderer = new PDFRenderer(document);
            for (int i = 0; i < document.getPages().getCount(); i++) {
                BufferedImage image = renderer.renderImage(i);
                ImageIO.write(image, "PNG", FileUtil.file(SystemTools.systemPath() + thumbnailDir + StrUtil.SLASH + i + ".png"));
            }
        } catch (OfficeException | IOException e) {
            ExceptionTools.businessLogger(e.getMessage());
        }
    }

    @Override
    public String queryThumbnail(FileMetadata file) {
        if (fileTools.isVideo(file.getExt())) {
            VideoMetadata video = videoMetadataService.queryVideoByFileId(file.getId());
            return ObjectUtil.isNull(video) ? StrUtil.EMPTY : video.getThumbnail();
        }
        return StrUtil.EMPTY;
    }

    @Override
    public Map<String, Object> queryMetadata(List<FileMetadata> files) {
        Map<String, Object> maps = new HashMap<>(files.size());

        // 查询各类文件元数据并封装映射
        Map<String, List<FileMetadata>> fileMap = files.stream().collect(Collectors.groupingBy(FileMetadata::getExt));

        // 整理视频文件的元数据映射
        for (String ext : fileTools.acquireFileExtProperty().acquireVideoSupports()) {
            List<FileMetadata> videos = fileMap.get(ext);
            if (ObjectUtil.isNotNull(videos) && !videos.isEmpty()) {
                List<String> fileIds = videos.stream().map(FileMetadata::getId).collect(Collectors.toList());
                if (!fileIds.isEmpty()) {
                    List<VideoMetadata> objs = videoMetadataService.queryVideosByFileIds(fileIds);
                    maps.putAll(objs.stream().collect(Collectors.toMap(VideoMetadata::getFileId, o -> o)));
                }
            }
        }

        // 整理音频文件的元数据映射
        for (String ext : fileTools.acquireFileExtProperty().acquireAudioSupports()) {
            List<FileMetadata> videos = fileMap.get(ext);
            if (ObjectUtil.isNotNull(videos) && !videos.isEmpty()) {
                List<String> fileIds = videos.stream().map(FileMetadata::getId).collect(Collectors.toList());
                if (!fileIds.isEmpty()) {
                    List<AudioMetadata> objs = audioMetadataService.queryAudiosByFileIds(fileIds);
                    maps.putAll(objs.stream().collect(Collectors.toMap(AudioMetadata::getFileId, o -> o)));
                }
            }
        }

        return maps;
    }

    @Override
    public Object queryMediaMetadata(String fileId, String ext) {
        // 是视频文件
        if (fileTools.isVideo(ext)) {
            return videoMetadataService.queryVideoByFileId(fileId);
        }
        // 是音频文件
        if (fileTools.isAudio(ext)) {
            return audioMetadataService.queryAudioByFileId(fileId);
        }
        return null;
    }

}
