package online.yangcloud.web.service.impl;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import online.yangcloud.common.annotation.TimeConsuming;
import online.yangcloud.common.common.AppConstants;
import online.yangcloud.common.enumration.OfficeTypeEnum;
import online.yangcloud.common.model.AudioMetadata;
import online.yangcloud.common.model.FileMetadata;
import online.yangcloud.common.model.OfficeMetadata;
import online.yangcloud.common.model.VideoMetadata;
import online.yangcloud.common.tools.*;
import online.yangcloud.web.processor.OfficeProcessor;
import online.yangcloud.web.service.ThumbnailService;
import online.yangcloud.web.service.meta.MetaService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.office.OfficeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
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

    /**
     * 视频缩略图后缀
     */
    private static final String VIDEO_SUFFIX = ".jpg";

    /**
     * 文档缩略图后缀
     */
    private static final String OFFICE_SUFFIX = "png";

    @Resource
    private MetaService metaService;

    @Resource
    private OfficeProcessor officeProcessor;

    @Resource
    private FileTools fileTools;

    @Override
    public void thumbnail(FileMetadata metadata) {
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
        String thumbnail = AppConstants.Uploader.SNAPSHOT + metadata.getId() + VIDEO_SUFFIX;
        if (!FileUtil.exist(FileUtil.file(SystemTools.systemPath() + thumbnail).getParent())) {
            FileUtil.mkdir(FileUtil.file(SystemTools.systemPath() + thumbnail).getParent());
        }
        FfmpegTools.splitFirstPicture(filePath, SystemTools.systemPath() + thumbnail);

        // 压缩图片大小
        float scale = AppConstants.Icon.DEFAULT_WIDTH / Float.parseFloat(String.valueOf(video.getWidth()));
        String output = AppConstants.Uploader.SNAPSHOT + metadata.getId() + "_output" + VIDEO_SUFFIX;
        ImgUtil.scale(FileUtil.file(SystemTools.systemPath() + thumbnail), FileUtil.file(SystemTools.systemPath() + output), scale);
        FileUtil.rename(FileUtil.file(SystemTools.systemPath() + output), SystemTools.systemPath() + thumbnail, Boolean.TRUE);

        // 补充视频详细信息
        video.setId(IdTools.fastSimpleUuid()).setThumbnail(thumbnail).setFileId(metadata.getId());
        metaService.acquireVideo().addVideoRecord(video);
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
        metaService.acquireAudio().addAudioRecord(audio);
    }

    /**
     * 获取 office 文档详细信息
     *
     * @param metadata 文件元数据
     */
    private void officeInformation(FileMetadata metadata) {
        try {
            // 判断是否是 pdf 文件。如果是，则直接切分并转换图片；如果是 word、ppt、excel 文件，则需要先转成 pdf
            OfficeTypeEnum officeType = fileTools.acquireOfficeType(metadata.getExt());
            if (ObjUtil.isNull(officeType)) {
                ExceptionTools.businessLogger("不支持的文件格式");
            }

            // 拼接 pdf 文件存储路径
            String pdfSuffix = DefaultDocumentFormatRegistry.PDF.getExtension();
            String source = SystemTools.systemPath() + AppConstants.Uploader.FILE + metadata.getId() + metadata.getExt();
            String target = SystemTools.systemPath() + AppConstants.Uploader.TMP + metadata.getId() + StrUtil.DOT + pdfSuffix;

            // 将 word、ppt、excel 格式文件转换为 pdf 文件
            if (!OfficeTypeEnum.PDF.equals(officeType)) {
                officeProcessor.convertToPdf(source, target, officeType);
            }

            // 在缩略图目录中创建以文件 id 为名的目录，以存放 pdf 每页生成的图片
            String thumbnailDir = AppConstants.Uploader.SNAPSHOT + metadata.getId() + AppConstants.Special.SEPARATOR;
            if (!FileUtil.exist(SystemTools.systemPath() + thumbnailDir)) {
                FileUtil.mkdir(SystemTools.systemPath() + thumbnailDir);
            }

            // 文档元数据
            OfficeMetadata office = OfficeMetadata.builder()
                    .setId(IdUtil.fastSimpleUUID())
                    .setFileId(metadata.getId());

            // 对 pdf 文件进行切分，并将每页转为图片
            PDDocument document = PDDocument.load(FileUtil.file(target));
            PDFRenderer renderer = new PDFRenderer(document);
            office.setPageTotal(document.getPages().getCount());
            for (int i = 0; i < document.getPages().getCount(); i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, 300, ImageType.RGB);
                String thumbnailPath = SystemTools.systemPath() + thumbnailDir + i + StrUtil.DOT + OFFICE_SUFFIX;
                office.setWidth(image.getWidth()).setHeight(image.getHeight());
                if (StrUtil.isBlank(office.getThumbnail())) {
                    office.setThumbnail(thumbnailPath.replace(SystemTools.systemPath(), StrUtil.EMPTY));
                }
                ImageIO.write(image, OFFICE_SUFFIX, FileUtil.file(thumbnailPath));
            }

            // 记录文档元数据
            metaService.acquireOffice().addOfficeRecord(office);
        } catch (OfficeException | IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public String queryThumbnail(FileMetadata file) {
        if (fileTools.isVideo(file.getExt())) {
            VideoMetadata video = metaService.acquireVideo().queryVideoByFileId(file.getId());
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
                    List<VideoMetadata> objs = metaService.acquireVideo().queryVideosByFileIds(fileIds);
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
                    List<AudioMetadata> objs = metaService.acquireAudio().queryAudiosByFileIds(fileIds);
                    maps.putAll(objs.stream().collect(Collectors.toMap(AudioMetadata::getFileId, o -> o)));
                }
            }
        }

        // 整理 office 文件的元数据映射
        for (String ext : fileTools.acquireFileExtProperty().acquireOfficeSupports()) {
            List<FileMetadata> offices = fileMap.get(ext);
            if (ObjUtil.isNotNull(offices) && !offices.isEmpty()) {
                List<String> fileIds = offices.stream().map(FileMetadata::getId).collect(Collectors.toList());
                if (!fileIds.isEmpty()) {
                    List<OfficeMetadata> objs = metaService.acquireOffice().queryOfficesByFileIds(fileIds);
                    maps.putAll(objs.stream().collect(Collectors.toMap(OfficeMetadata::getFileId, o -> o)));
                }
            }
        }

        return maps;
    }

    @Override
    public Object queryMediaMetadata(String fileId, String ext) {
        // 是视频文件
        if (fileTools.isVideo(ext)) {
            return metaService.acquireVideo().queryVideoByFileId(fileId);
        }
        // 是音频文件
        if (fileTools.isAudio(ext)) {
            return metaService.acquireAudio().queryAudioByFileId(fileId);
        }
        return null;
    }

}
