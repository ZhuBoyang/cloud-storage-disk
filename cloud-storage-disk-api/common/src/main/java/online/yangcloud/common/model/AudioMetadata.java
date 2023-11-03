package online.yangcloud.common.model;

import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import cn.org.atool.fluent.mybatis.annotation.TableId;
import online.yangcloud.common.annotation.DatabaseColumn;
import online.yangcloud.common.annotation.DatabaseIndex;
import online.yangcloud.common.enumration.ColumnTypeEnum;

/**
 * @author zhuboyang
 * @since 2023年09月22 19:24:32
 */
@FluentMybatis(table = "audio_metadata", desc = "音频元数据")
public class AudioMetadata extends BaseParameter {

    /**
     * 主键 id
     */
    @TableId
    @DatabaseColumn(primary = true, name = "id", type = ColumnTypeEnum.VARCHAR, length = 32, canNull = false)
    @DatabaseIndex(unique = true)
    private String id;

    /**
     * 文件 id
     */
    @DatabaseColumn(name = "file_id", type = ColumnTypeEnum.VARCHAR, length = 32, canNull = false, comment = "文件 id")
    @DatabaseIndex
    private String fileId;

    /**
     * 歌曲名称
     */
    @DatabaseColumn(name = "title", type = ColumnTypeEnum.VARCHAR, length = 128, canNull = false, comment = "歌曲名称")
    private String title = "未知";

    /**
     * 所属专辑
     */
    @DatabaseColumn(name = "album", type = ColumnTypeEnum.VARCHAR, length = 128, canNull = false, comment = "所属专辑")
    private String album = "未知";

    /**
     * 艺术家
     */
    @DatabaseColumn(name = "artist", type = ColumnTypeEnum.VARCHAR, length = 128, canNull = false, comment = "艺术家")
    private String artist = "未知";

    /**
     * 时长
     */
    @DatabaseColumn(name = "duration", type = ColumnTypeEnum.DOUBLE, canNull = false, comment = "时长")
    private Double duration;

    /**
     * 比特率
     */
    @DatabaseColumn(name = "bitrate", type = ColumnTypeEnum.VARCHAR, length = 20, canNull = false, comment = "比特率")
    private String bitrate;

    /**
     * 编码格式
     */
    @DatabaseColumn(name = "encoder_code", type = ColumnTypeEnum.VARCHAR, length = 10, canNull = false, comment = "编码格式")
    private String encoderCode;

    public static AudioMetadata initial() {
        return new AudioMetadata();
    }

    public String getId() {
        return id;
    }

    public AudioMetadata setId(String id) {
        this.id = id;
        return this;
    }

    public String getFileId() {
        return fileId;
    }

    public AudioMetadata setFileId(String fileId) {
        this.fileId = fileId;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public AudioMetadata setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getAlbum() {
        return album;
    }

    public AudioMetadata setAlbum(String album) {
        this.album = album;
        return this;
    }

    public String getArtist() {
        return artist;
    }

    public AudioMetadata setArtist(String artist) {
        this.artist = artist;
        return this;
    }

    public Double getDuration() {
        return duration;
    }

    public AudioMetadata setDuration(Double duration) {
        this.duration = duration;
        return this;
    }

    public String getBitrate() {
        return bitrate;
    }

    public AudioMetadata setBitrate(String bitrate) {
        this.bitrate = bitrate;
        return this;
    }

    public String getEncoderCode() {
        return encoderCode;
    }

    public AudioMetadata setEncoderCode(String encoderCode) {
        this.encoderCode = encoderCode;
        return this;
    }

    @Override
    public String toString() {
        return "AudioMetadata["
                + " id=" + id + ","
                + " fileId=" + fileId + ","
                + " title=" + title + ","
                + " album=" + album + ","
                + " artist=" + artist + ","
                + " duration=" + duration + ","
                + " bitrate=" + bitrate + ","
                + " encoderCode=" + encoderCode
                + " ]"
                + " "
                + super.toString();
    }
}
