package online.yangcloud.web.service.meta;

/**
 * @author zhuboyang
 * @since 2023年10月20 11:17:53
 */
public interface MetaService {

    BlockMetadataService acquireBlock();

    FileBlockService acquireFileBlock();

    FileMetadataService acquireFile();

    VideoMetadataService acquireVideo();

    AudioMetadataService acquireAudio();

    DocumentMetadataService acquireDocument();

    PreviewConvertMetaService acquireSnapshotConvert();

    UserMetaService acquireUser();

}
