package online.yangcloud.utils;

import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhuboyang
 * @since 2023年07月29 12:06:34
 */
public class FfmpegTools {

    /**
     * ffmpeg 命令
     */
    private static final String FFMPEG = "ffmpeg";

    private FfmpegTools() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        /**
         * 命令集
         */
        private static final List<String> COMMANDS = new ArrayList<>();

        /**
         * 查询版本号命令
         */
        public Builder version() {
            COMMANDS.clear();
            COMMANDS.add("-version");
            return this;
        }

        /**
         * 查询支持的格式
         */
        private Builder formats() {
            COMMANDS.clear();
            COMMANDS.add("-formats");
            return this;
        }

        public String build() {
            return FFMPEG + StrUtil.SPACE + StrUtil.join(StrUtil.SPACE, COMMANDS);
        }

        public List<String> run() {
            return RuntimeUtil.execForLines(build());
        }

    }

}
