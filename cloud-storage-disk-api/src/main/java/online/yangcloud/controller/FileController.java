package online.yangcloud.controller;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import online.yangcloud.annotation.SessionValid;
import online.yangcloud.common.ResultBean;
import online.yangcloud.common.ResultData;
import online.yangcloud.common.resultcode.AppResultCode;
import online.yangcloud.exception.ParamErrorException;
import online.yangcloud.model.FileMetadata;
import online.yangcloud.model.User;
import online.yangcloud.model.ao.file.*;
import online.yangcloud.model.vo.file.FileBreadView;
import online.yangcloud.model.vo.file.FileMetadataView;
import online.yangcloud.model.vo.file.FilePlayView;
import online.yangcloud.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ws.schild.jave.EncoderException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author zhuboyang
 * @since 2023年01月03 12:00:36
 */
@RestController
@RequestMapping("/file")
public class FileController {
    
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Resource
    private FileService fileService;

    /**
     * 检测文件块是否已入库
     *
     * @param uploader 文件块参数
     * @return result
     */
    @SessionValid
    @PostMapping("/check_exist")
    public ResultData checkBlockExist(@RequestBody BlockUploader uploader) {
        Boolean isExist = fileService.checkBlocksExist(uploader);
        return ResultData.success(isExist);
    }

    /**
     * 上传文件
     *
     * @param uploader 上传的文件参数
     * @return result
     */
    @SessionValid
    @PostMapping("/upload")
    public ResultData uploadFile(BlockUploader uploader) throws IOException {
        // 校验父级目录 id 是否正常
        if (CharSequenceUtil.isBlank(uploader.getPid())) {
            logger.error("parent directory id not detected");
            throw new ParamErrorException();
        }
        fileService.uploadFileBlock(uploader);
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
    public ResultData mergeFile(@RequestBody FileMergeRequest mergeRequest, User user) throws IOException {
        FileMetadataView fileView = fileService.mergeFile(mergeRequest.getIdentifier(), mergeRequest.getFileHash(), user);
        return ResultData.success(fileView);
    }

    /**
     * 新建文件夹
     *
     * @param request 请求参数
     * @param user    当前登录的用户
     * @return result
     */
    @SessionValid
    @PostMapping("/mkdir")
    public ResultData mkdir(@RequestBody MkdirRequest request, User user) {
        FileMetadataView fileView = fileService.mkdir(request.getPid(), request.getFileName(), user);
        return ResultData.success(fileView);
    }

    /**
     * 批量删除文件
     *
     * @param deleteRequest 待删除文件 id 列表
     * @param user          当前登录的用户
     * @return result
     */
    @SessionValid
    @PostMapping("/batch_delete")
    public ResultData batchDeleteFiles(@RequestBody BatchDeleteRequest deleteRequest, User user) {
        List<String> fileIds = CharSequenceUtil.split(deleteRequest.getFileString(), StrUtil.COMMA);
        ResultBean<?> resultBean = fileService.batchDeleteFile(fileIds, user);
        if (resultBean.isSuccess()) {
            return ResultData.success(AppResultCode.SUCCESS);
        }
        return ResultData.errorMessage(resultBean.getResultCode());
    }

    /**
     * 批量移动文件
     *
     * @param operationRequest 待移动文件与目标文件夹
     * @param user             当前登录的用户
     * @return result
     */
    @SessionValid
    @PostMapping("/batch_move")
    public ResultData batchMoveFiles(@RequestBody FileBatchOperationRequest operationRequest, User user) {
        ResultBean<?> resultBean = fileService.batchMoveFiles(operationRequest.getSources(), operationRequest.getTarget(), user);
        if (resultBean.isSuccess()) {
            return ResultData.success(resultBean.getResultCode());
        }
        return ResultData.errorMessage(resultBean.getResultCode());
    }

    /**
     * 批量复制文件
     *
     * @param operationRequest 待复制文件与目标文件夹
     * @param user             当前登录的用户
     * @return result
     */
    @SessionValid
    @PostMapping("/batch_copy")
    public ResultData batchCopyFiles(@RequestBody FileBatchOperationRequest operationRequest, User user) {
        ResultBean<?> resultBean = fileService.batchCopyFiles(operationRequest.getSources(), operationRequest.getTarget(), user);
        if (resultBean.isSuccess()) {
            return ResultData.success(resultBean.getResultCode());
        }
        return ResultData.errorMessage(resultBean.getResultCode());
    }

    /**
     * 文件重命名
     *
     * @param renameRequest 请求参数
     * @param user          当前登录的用户
     * @return result
     */
    @SessionValid
    @PostMapping("/rename")
    public ResultData renameFile(@RequestBody FileRenameRequest renameRequest, User user) {
        FileMetadata file = fileService.rename(renameRequest, user);
        return ResultData.success(file);
    }

    /**
     * 获取文件播放地址
     *
     * @param playRequest 文件 id
     * @return result
     */
    @SessionValid
    @PostMapping("/play_url")
    public ResultData getFilePlayUrl(@RequestBody FilePlayRequest playRequest) throws EncoderException {
        FilePlayView playUrl = fileService.findPlayUrl(playRequest.getFileId());
        return ResultData.success(playUrl);
    }

    /**
     * 查询当前所在目录文件的面包屑导航数据
     *
     * @param id   当前所在目录的文件 id
     * @param user 当前登录的用户
     * @return result
     */
    @SessionValid
    @GetMapping("/breads")
    public ResultData queryFileBreads(@RequestParam(defaultValue = "") String id, User user) {
        List<FileBreadView> breads = fileService.queryFileBreads(id, user);
        return ResultData.success(breads);
    }

    /**
     * 分页查询文件列表
     *
     * @param searchRequest 查询请求
     * @param user          当前登录的用户
     * @return result
     */
    @SessionValid
    @GetMapping("/list")
    public ResultData queryFiles(FileSearchRequest searchRequest, User user) {
        List<FileMetadataView> views = fileService.queryFiles(searchRequest, user);
        return ResultData.success(views);
    }

    /**
     * 查询文件夹的面包屑导航数据
     *
     * @param queryRequest 父级目录 id
     * @return result
     */
    @SessionValid
    @GetMapping("/dir_breads")
    public ResultData queryDirBreads(DirBreadsQueryRequest queryRequest) {
        List<FileBreadView> breads = fileService.queryDirBreads(queryRequest.getParentId());
        return ResultData.success(breads);
    }

    /**
     * 查询目录下次一级的所有文件夹
     *
     * @param searchRequest 查询条件
     * @param user          当前登录的用户
     * @return result
     */
    @SessionValid
    @GetMapping("/dirs")
    public ResultData queryDirs(DirsSearchRequest searchRequest, User user) {
        List<FileMetadata> dirs = fileService.queryDirs(searchRequest.getParentId(), searchRequest.getSize(), user);
        return ResultData.success(dirs);
    }

    /**
     * 下载文件
     *
     * @param fileId   文件 id
     * @param response 响应
     */
    @GetMapping("/download/{fileId}")
    public void download(@PathVariable String fileId, HttpServletResponse response) {
        fileService.download(fileId, response);
    }

}
