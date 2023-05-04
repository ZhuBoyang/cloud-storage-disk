package online.yangcloud.controller;

import cn.hutool.core.text.CharSequenceUtil;
import online.yangcloud.annotation.SessionValid;
import online.yangcloud.common.ResultData;
import online.yangcloud.exception.ParamErrorException;
import online.yangcloud.model.ao.file.BlockCheckExistRequest;
import online.yangcloud.model.ao.file.BlockUpload;
import online.yangcloud.model.ao.file.FileMergeRequest;
import online.yangcloud.model.po.User;
import online.yangcloud.model.vo.file.FileMetadataView;
import online.yangcloud.service.BlockMetadataService;
import online.yangcloud.service.FileBlockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private BlockMetadataService blockMetadataService;

    @Autowired
    private FileBlockService fileBlockService;

    /**
     * 检测文件块是否已入库
     *
     * @param existRequest 文件块参数
     * @return result
     */
    @SessionValid
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
    @SessionValid
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
     * @param user         当前登录的用户
     * @return result
     */
    @SessionValid
    @PostMapping("/merge")
    public ResultData mergeFile(@RequestBody FileMergeRequest mergeRequest, User user) {
        FileMetadataView fileView = fileBlockService.mergeFile(mergeRequest.getIdentifier(), mergeRequest.getFileHash(), user);
        return ResultData.success(fileView);
    }

}
