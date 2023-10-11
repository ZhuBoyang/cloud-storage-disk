package online.yangcloud.common.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import online.yangcloud.common.common.AppConstants;
import online.yangcloud.common.model.AudioMetadata;
import online.yangcloud.common.model.VideoMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author zhuboyang
 * @since 2023年07月29 12:06:34
 */
public class FfmpegTools {

    private static final Logger logger = LoggerFactory.getLogger(FfmpegTools.class);

    /**
     * 获取视频详细信息
     *
     * @param path 视频全路径
     * @return 视频元数据
     */
    public static VideoMetadata acquireVideoInfo(String path) {
        VideoMetadata video = VideoMetadata.initial();

        // 执行指令获取有用的视频信息
        List<String> contents = acquireMediaUsableInfo(path);

        // 解析视频信息
        for (String v : contents) {
            // 这一行有 ‘总时长’、‘比特率’
            if (v.contains("Duration") && v.contains("bitrate")) {
                List<String> parts = StrUtil.split(v, StrUtil.COMMA);
                for (String part : parts) {
                    String label = part.substring(0, part.indexOf(StrUtil.COLON));
                    String content = part.substring(part.indexOf(StrUtil.COLON) + 1);
                    if (label.contains("Duration")) {
                        video.setDuration(TimeTools.convert(content));
                    }
                    if (label.contains("bitrate")) {
                        video.setBitrate(content.trim());
                    }
                }
            }
            // 这一行有 ‘视频编码’、‘视频分辨率’，或许也会有 ’音频编码‘
            if (v.contains("Stream") && (v.contains("Video") || v.contains("Audio"))) {
                v = v.substring(v.indexOf(StrUtil.COLON) + 1);
                List<String> parts = StrUtil.split(v, StrUtil.COMMA);
                for (String part : parts) {
                    // 分辨率
                    if (part.contains("SAR") && part.contains("DAR")) {
                        String content = part.substring(0, part.indexOf(StrUtil.BRACKET_START));
                        List<String> resolutions = StrUtil.split(content, "x");
                        video.setWidth(Integer.parseInt(resolutions.get(0).trim()));
                        video.setHeight(Integer.parseInt(resolutions.get(1).trim()));
                    }
                    // 视频编码
                    if (part.contains("Video")) {
                        part = part.substring(part.lastIndexOf(StrUtil.COLON));
                        video.setVideoCode(part.substring(1, part.indexOf(AppConstants.Special.BRACKET_LEFT)).trim());
                    }
                    // 音频编码
                    if (part.contains("Audio")) {
                        part = part.substring(part.lastIndexOf(StrUtil.COLON));
                        video.setAudioCode(part.substring(part.indexOf(StrUtil.COLON) + 1, part.indexOf(AppConstants.Special.BRACKET_LEFT)).trim());
                    }
                }
            }
        }
        return video;
    }

    /**
     * 获取音频详细信息
     *
     * @param path 视频全路径
     */
    public static AudioMetadata acquireAudioMetadata(String path) {
        AudioMetadata audio = AudioMetadata.initial();

        // 执行指令获取有用的视频信息
        List<String> contents = acquireMediaUsableInfo(path);

        // 解析音频信息
        for (String content : contents) {
            // 这一行包含有标题、专辑或艺术家的信息
            if (content.contains("TITLE") || content.contains("ALBUM") || content.contains("ARTIST")
                    || content.contains("title") || content.contains("album") || content.contains("artist")) {
                List<String> arr = StrUtil.split(content, StrUtil.COLON);
                // 标题/名称
                if ("TITLE".equals(arr.get(0).trim()) || "title".equals(arr.get(0).trim())) {
                    audio.setTitle(arr.get(1).trim());
                }
                // 所属专辑
                if ("ALBUM".equals(arr.get(0).trim()) || "album".equals(arr.get(0).trim())) {
                    audio.setAlbum(arr.get(1).trim());
                }
                // 艺术家
                if ("ARTIST".equals(arr.get(0).trim()) || "artist".equals(arr.get(0).trim())) {
                    audio.setArtist(arr.get(1).trim());
                }
            }
            // 这一行包含了时长和比特率
            if (content.contains("Duration") && content.contains("bitrate")) {
                List<String> parts = StrUtil.split(content, StrUtil.COMMA);
                for (String part : parts) {
                    // 时长
                    if (part.contains("Duration")) {
                        audio.setDuration(TimeTools.convert(part.substring(part.indexOf(StrUtil.COLON) + 1).trim()));
                    }
                    // 比特率
                    if (part.contains("bitrate")) {
                        audio.setBitrate(StrUtil.split(part, StrUtil.COLON).get(1).trim());
                    }
                }
            }
            // 这一行包含音频格式
            if (content.contains("Stream") && content.contains("Audio")) {
                List<String> arr = StrUtil.split(StrUtil.split(content, StrUtil.COMMA).get(0), StrUtil.COLON);
                audio.setEncoderCode(arr.get(arr.size() - 1));
            }
        }
        return audio;
    }

    /**
     * 获取媒体文件有用的信息
     *
     * @param path 媒体文件全路径
     */
    public static List<String> acquireMediaUsableInfo(String path) {
        // 执行指令获取视频信息
        Process exec = RuntimeUtil.exec(StrBuilder.create().append("ffmpeg").append(" -i ").append(path).toString());
        List<String> contents = StrUtil.split(IoUtil.read(exec.getInputStream(), StandardCharsets.UTF_8), StrUtil.LF);

        // 删除 ffmpeg 打印的无用的信息
        boolean flag = Boolean.FALSE;
        for (int i = 0; i < contents.size(); i++) {
            if (contents.get(i).startsWith("Input")) {
                flag = Boolean.TRUE;
            }
            if (!flag) {
                contents.remove(i);
                i--;
            }
        }
        return contents;
    }

    /**
     * 截取视频的第一帧作为缩略图
     *
     * @param videoPath  视频存储路径
     * @param screenshot 截图存储路径
     */
    public static void splitFirstPicture(String videoPath, String screenshot) {
        // 执行 ffmpeg 命令截取指定视频的第一帧为缩略图
        StrBuilder command = StrBuilder.create()
                .append("ffmpeg ")
                .append("-i ")
                .append(videoPath)
                .append(" -vframes")
                .append(" 1")
                .append(" -threads")
                .append(" 1 ")
                .append(screenshot);
        RuntimeUtil.exec(command.toString());

        // 检测缩略图是否存在
        while (!FileUtil.exist(screenshot)) {
            logger.info("screenshot is not exist");
            ThreadUtil.safeSleep(100);
        }
        logger.info("screenshot has exist");
    }

}
