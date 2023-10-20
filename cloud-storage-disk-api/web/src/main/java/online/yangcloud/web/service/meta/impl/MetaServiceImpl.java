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
    private AudioMetadataService audioMetadataService;

    @Resource
    private BlockMetadataService blockMetadataService;

    @Resource
    private FileBlockService fileBlockService;

    @Resource
    private FileMetadataService fileMetadataService;

    @Resource
    private OfficeMetadataService officeMetadataService;

    @Resource
    private UserMetaService userMetaService;

    @Resource
    private VideoMetadataService videoMetadataService;

    @Override
    public AudioMetadataService acquireAudio() {
        return audioMetadataService;
    }

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
    public OfficeMetadataService acquireOffice() {
        return officeMetadataService;
    }

    @Override
    public UserMetaService acquireUser() {
        return userMetaService;
    }

    @Override
    public VideoMetadataService acquireVideo() {
        return videoMetadataService;
    }

}
