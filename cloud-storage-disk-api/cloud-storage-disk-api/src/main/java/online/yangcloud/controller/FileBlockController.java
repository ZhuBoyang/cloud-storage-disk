package online.yangcloud.controller;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjUtil;
import online.yangcloud.common.ResultData;
import online.yangcloud.exception.ParamErrorException;
import online.yangcloud.model.ao.BlockUpload;
import online.yangcloud.model.ao.file.BlockCheckExistRequest;
import online.yangcloud.model.ao.file.FileMergeRequest;
import online.yangcloud.model.po.BlockMetadata;
import online.yangcloud.model.vo.file.FileMetadataView;
import online.yangcloud.service.BlockMetadataService;
import online.yangcloud.service.FileBlockService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * @author zhuboyang
 * @since 2022年12月31 12:08:50
 */
@RestController
@RequestMapping("/file_block")
public class FileBlockController {

    private static final Logger logger = LoggerFactory.getLogger(FileBlockController.class);

    @Autowired
    private FileBlockService fileBlockService;

    @Autowired
    private BlockMetadataService blockMetadataService;

    /**
     * 检测文件块是否已入库
     *
     * @param existRequest 文件块参数
     * @return result
     */
    @PostMapping("/check_exist")
    public ResultData checkBlockExist(@RequestBody BlockCheckExistRequest existRequest) {
        List<Boolean> checkResults = blockMetadataService.checkBlocksExist(existRequest);
        return ResultData.success(checkResults);
    }

    /**
     * 上传文件
     *
     * @param upload 上传的文件参数
     * @return result
     */
    @PostMapping("/upload")
    public ResultData uploadFile(BlockUpload upload) throws IOException {
        // 校验父级目录 id 是否正常
        if (CharSequenceUtil.isBlank(upload.getPid())) {
            logger.error("parent directory id not detected");
            throw new ParamErrorException();
        }
        fileBlockService.uploadFileBlock(upload);
        return ResultData.success(Boolean.TRUE);
    }

    /**
     * 文件合并
     *
     * @param mergeRequest 请求参数
     * @return result
     */
    @PostMapping("/merge")
    public ResultData mergeFile(@RequestBody FileMergeRequest mergeRequest) {
        FileMetadataView fileView = fileBlockService.mergeFile(mergeRequest.getIdentifier(), mergeRequest.getFileHash());
        return ResultData.success(fileView);
    }

}
