package online.yangcloud.common.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import online.yangcloud.common.common.AppConstants;
import online.yangcloud.common.model.VideoMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
    public static VideoMetadata getVideoInfo(String path) {
        VideoMetadata video = VideoMetadata.initial();

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

    /**
     * ffmpeg 命令
     */
    private static final String FFMPEG = "ffmpeg";

    /**
     * 命令之间的空格
     */
    private static final String COMMAND_SPACE = StrUtil.SPACE;

    /**
     * 命令集
     */
    private static final List<String> COMMANDS = new ArrayList<>();

    private FfmpegTools() {
    }

    public static FfmpegTools builder() {
        return new FfmpegTools();
    }

    public static class Primary {

        /**
         * 查询 ffmpeg 版本号的命令
         */
        public static String version() {
            return FFMPEG + " -version";
        }

        /**
         * 查询编译配置
         */
        public static String buildConf() {
            return FFMPEG + " -buildconf";
        }

        /**
         * 显示可用格式 (muxers + demuxers)
         */
        public static String formats() {
            return FFMPEG + " -formats";
        }

        /**
         * 显示可用复用器
         */
        public static String muxers() {
            return FFMPEG + " -muxers";
        }

        /**
         * 显示可用解复用器
         */
        public static String deMuxers() {
            return FFMPEG + " -demuxers";
        }

        /**
         * 显示可用编解码器 (decoders + encoders)
         */
        public static String codecs() {
            return FFMPEG + " -codecs";
        }

        /**
         * 显示可用解码器
         */
        public static String decoders() {
            return FFMPEG + " -decoders";
        }

        /**
         * 显示可用编码器
         */
        public static String encoders() {
            return FFMPEG + " -encoders";
        }

        /**
         * 显示可用比特流 filter
         */
        public static String byteStream() {
            return FFMPEG + " -bsfs";
        }

        /**
         * 显示可用的协议
         */
        public static String protocols() {
            return FFMPEG + " -protocols";
        }

        /**
         * 显示可用的过滤器
         */
        public static String filters() {
            return FFMPEG + " -filters";
        }

        /**
         * 显示可用的像素格式
         */
        public static String pixelFormats() {
            return FFMPEG + " -pix_fmts";
        }

        /**
         * 显示标准声道名称
         */
        public static String layouts() {
            return FFMPEG + " -layouts";
        }

        /**
         * 显示可用的音频采样格式
         */
        public static String audioSampleFormats() {
            return FFMPEG + " -sample_fmts";
        }

        /**
         * 显示可用的颜色名称
         */
        public static String colorName() {
            return FFMPEG + " -colors";
        }

    }

    public Parameters parameters() {
        return new Parameters();
    }

    public static class Parameters {

        private static final String INPUT_PARAMETERS = "-i";
        private static final String START_PARAMETERS = "-ss";
        private static final String TIME_LENGTH_PARAMETERS = "-t";
        private static final String AUDIO_FRAME_PARAMETERS = "-aframes";
        private static final String AUDIO_BITRATE_PARAMETERS = "-b:a";
        private static final String AUDIO_SAMPLING_PARAMETERS = "-ar";
        private static final String AUDIO_CHANNEL_PARAMETERS = "-ac";
        private static final String AUDIO_CODEC_PARAMETERS = "-acodec";
        private static final String AUDIO_NO_HANDLE_PARAMETERS = "-an";
        private static final String AUDIO_FILTER_PARAMETERS = "-af";
        private static final String VIDEO_FRAME_PARAMETERS = "-vframes";
        private static final String VIDEO_BITRATE_PARAMETERS = "-b:v";
        private static final String VIDEO_FRAME_RATE_PARAMETERS = "-r";
        private static final String VIDEO_SIZE_PARAMETERS = "-b";
        private static final String VIDEO_NO_HANDLE_PARAMETERS = "-vn";
        private static final String VIDEO_ENCODER_PARAMETERS = "-vcodec";
        private static final String VIDEO_FILTER_PARAMETERS = "-vf";

        /**
         * 设置输入流
         *
         * @param filepath 输入流地址
         * @return FfmpegTools
         */
        public Parameters input(String filepath) {
            setParameter(INPUT_PARAMETERS, filepath);
            return this;
        }

        /**
         * 设置开始时间
         *
         * @param startTime 开始时间
         * @return FfmpegTools
         */
        public Parameters start(String startTime) {
            setParameter(START_PARAMETERS, startTime);
            return this;
        }

        /**
         * 设置时间长度
         *
         * @param timeLength 时间长度
         * @return FfmpegTools
         */
        public Parameters time(String timeLength) {
            setParameter(TIME_LENGTH_PARAMETERS, timeLength);
            return this;
        }

        /**
         * 设置要输出的音频帧数
         *
         * @param frame 音频帧数
         * @return FfmpegTools
         */
        public Parameters audioFrame(Integer frame) {
            setParameter(AUDIO_FRAME_PARAMETERS, String.valueOf(frame));
            return this;
        }

        /**
         * 设置音频码率
         *
         * @param bitrate 音频码率
         * @return FfmpegTools
         */
        public Parameters audioBitrate(Integer bitrate) {
            setParameter(AUDIO_BITRATE_PARAMETERS, String.valueOf(bitrate));
            return this;
        }

        /**
         * 设置音频采样率
         *
         * @param sampling 音频采样率
         * @return FfmpegTools
         */
        public Parameters audioSampling(Integer sampling) {
            setParameter(AUDIO_SAMPLING_PARAMETERS, String.valueOf(sampling));
            return this;
        }

        /**
         * 设置声道数
         *
         * @param channel 声道数
         * @return FfmpegTools
         */
        public Parameters audioChannel(Integer channel) {
            setParameter(AUDIO_CHANNEL_PARAMETERS, String.valueOf(channel));
            return this;
        }

        /**
         * 设置声音编解码器
         *
         * @param codec 声音编解码器
         * @return FfmpegTools
         */
        public Parameters audioCodec(String codec) {
            setParameter(AUDIO_CODEC_PARAMETERS, codec);
            return this;
        }

        /**
         * 设置不处理音频
         *
         * @return FfmpegTools
         */
        public Parameters audioNoHandle() {
            setParameter(AUDIO_NO_HANDLE_PARAMETERS, StrUtil.EMPTY);
            return this;
        }

        /**
         * 设置音频过滤器
         *
         * @param filter 过滤器
         * @return FfmpegTools
         */
        public Parameters audioFilter(String filter) {
            setParameter(AUDIO_FILTER_PARAMETERS, filter);
            return this;
        }

        /**
         * 设置要输出的视频帧数
         *
         * @param frame 视频帧数
         * @return FfmpegTools
         */
        public Parameters videoFrame(String frame) {
            setParameter(VIDEO_FRAME_PARAMETERS, frame);
            return this;
        }

        /**
         * 设置视频码率
         *
         * @param bitrate 码率
         * @return FfmpegTools
         */
        public Parameters videoBitrate(Integer bitrate) {
            setParameter(VIDEO_BITRATE_PARAMETERS, String.valueOf(bitrate));
            return this;
        }

        /**
         * 设置视频帧速率
         *
         * @param frameRate 帧速率
         * @return FfmpegTools
         */
        public Parameters videoFrameRate(Integer frameRate) {
            setParameter(VIDEO_FRAME_RATE_PARAMETERS, String.valueOf(frameRate));
            return this;
        }

        /**
         * 设置视频画面的宽与高
         *
         * @param width  宽
         * @param height 高
         * @return FfmpegTools
         */
        public Parameters videoSize(Integer width, Integer height) {
            setParameter(VIDEO_SIZE_PARAMETERS, width + "x" + height);
            return this;
        }

        /**
         * 设置视频码率
         *
         * @return FfmpegTools
         */
        public Parameters videoNoHandle() {
            setParameter(VIDEO_NO_HANDLE_PARAMETERS, StrUtil.EMPTY);
            return this;
        }

        /**
         * 设置视频编解码器
         *
         * @param encoder 编码方式
         * @return FfmpegTools
         */
        public Parameters videoEncoder(String encoder) {
            setParameter(VIDEO_ENCODER_PARAMETERS, encoder);
            return this;
        }

        /**
         * 设置视频视频过滤器
         *
         * @param filter 视频过滤器
         * @return FfmpegTools
         */
        public Parameters videoFilter(String filter) {
            setParameter(VIDEO_FILTER_PARAMETERS, filter);
            return this;
        }

        /**
         * 构建 ffmpeg 命令
         *
         * @return 命令
         */
        public String build() {
            StrBuilder sbr = new StrBuilder(FFMPEG).append(COMMAND_SPACE);
            // 获取输入流
            if (COMMANDS.contains("-i")) {
                int i = COMMANDS.indexOf("-i");
                sbr.append(COMMANDS.get(i)).append(COMMAND_SPACE).append(COMMANDS.get(i + 1)).append(COMMAND_SPACE);
            }
            // 获取设置的开始时间
            if (COMMANDS.contains("-ss")) {
                int i = COMMANDS.indexOf("-ss");
                sbr.append(COMMANDS.get(i)).append(COMMAND_SPACE).append(COMMANDS.get(i + 1)).append(COMMAND_SPACE);
            }
            // 获取设置的时间长度
            if (COMMANDS.contains("-t")) {
                int i = COMMANDS.indexOf("-t");
                sbr.append(COMMANDS.get(i)).append(COMMAND_SPACE).append(COMMANDS.get(i + 1)).append(COMMAND_SPACE);
            }
            return StrUtil.EMPTY;
        }

        private void setParameter(String parameter, String content) {
            if (StrUtil.isBlank(content)) {
                COMMANDS.add(parameter);
            } else {
                if (COMMANDS.contains(parameter)) {
                    int i = COMMANDS.indexOf(parameter);
                    COMMANDS.set(i + 1, content);
                } else {
                    COMMANDS.add(parameter);
                    COMMANDS.add(content);
                }
            }
        }

    }

}
