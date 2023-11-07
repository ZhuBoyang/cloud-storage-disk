package online.yangcloud.web.service.meta.impl;

import online.yangcloud.web.service.meta.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zhuboyang
 * @since 2023年10月20 11:19:32
 */
@Service
public class MetaServiceImpl implements MetaService {

    @Resource
    private BlockMetadataService blockMetadataService;

    @Resource
    private FileBlockService fileBlockService;

    @Resource
    private FileMetadataService fileMetadataService;

    @Resource
    private VideoMetadataService videoMetadataService;

    @Resource
    private AudioMetadataService audioMetadataService;

    @Resource
    private DocumentMetadataService documentMetadataService;

    @Resource
    private PreviewConvertMetaService previewConvertMetaService;

    @Resource
    private UserMetaService userMetaService;

    @Override
    public BlockMetadataService acquireBlock() {
        return blockMetadataService;
    }

    @Override
    public FileBlockService acquireFileBlock() {
        return fileBlockService;
    }

    @Override
    public FileMetadataService acquireFile() {
        return fileMetadataService;
    }

    @Override
    public VideoMetadataService acquireVideo() {
        return videoMetadataService;
    }

    @Override
    public AudioMetadataService acquireAudio() {
        return audioMetadataService;
    }

    @Override
    public DocumentMetadataService acquireDocument() {
        return documentMetadataService;
    }

    @Override
    public PreviewConvertMetaService acquireSnapshotConvert() {
        return previewConvertMetaService;
    }

    @Override
    public UserMetaService acquireUser() {
        return userMetaService;
    }

}
