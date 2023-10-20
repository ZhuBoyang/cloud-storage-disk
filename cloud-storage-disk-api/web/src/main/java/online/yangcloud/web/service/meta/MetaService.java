package online.yangcloud.web.service.meta;

/**
 * @author zhuboyang
 * @since 2023年10月20 11:17:53
 */
public interface MetaService {

    AudioMetadataService acquireAudio();

    BlockMetadataService acquireBlock();

    FileBlockService acquireFileBlock();

    FileMetadataService acquireFile();

    OfficeMetadataService acquireOffice();

    UserMetaService acquireUser();

    VideoMetadataService acquireVideo();

}
