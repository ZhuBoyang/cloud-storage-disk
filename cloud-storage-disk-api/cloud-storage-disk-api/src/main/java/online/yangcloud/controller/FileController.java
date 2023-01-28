package online.yangcloud.controller;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import online.yangcloud.common.ResultBean;
import online.yangcloud.common.ResultData;
import online.yangcloud.common.resultcode.AppResultCode;
import online.yangcloud.model.ao.file.*;
import online.yangcloud.model.po.FileMetadata;
import online.yangcloud.model.po.User;
import online.yangcloud.model.vo.file.FileBreadView;
import online.yangcloud.model.vo.file.FileMetadataView;
import online.yangcloud.service.FileMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zhuboyang
 * @since 2023年01月03 12:00:36
 */
@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileMetadataService fileMetadataService;

    /**
     * 新建文件夹
     *
     * @param request 请求参数
     * @param user    当前登录的用户
     * @return result
     */
    @PostMapping("/mkdir")
    public ResultData mkdir(@RequestBody MkdirRequest request, User user) {
        FileMetadataView fileView = fileMetadataService.mkdir(request.getPid(), request.getFileName(), user);
        return ResultData.success(fileView);
    }

    /**
     * 批量删除文件
     *
     * @param deleteRequest 待删除文件 id 列表
     * @param user          当前登录的用户
     * @return result
     */
    @PostMapping("/batch_delete")
    public ResultData batchDeleteFiles(@RequestBody BatchDeleteRequest deleteRequest, User user) {
        List<String> fileIds = CharSequenceUtil.split(deleteRequest.getFileString(), StrUtil.COMMA);
        ResultBean<?> resultBean = fileMetadataService.batchDeleteFile(fileIds, user);
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
    @PostMapping("/batch_move")
    public ResultData batchMoveFiles(@RequestBody FileBatchOperationRequest operationRequest, User user) {
        ResultBean<?> resultBean = fileMetadataService.batchMoveFiles(operationRequest.getSources(), operationRequest.getTarget(), user);
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
    @PostMapping("/batch_copy")
    public ResultData batchCopyFiles(@RequestBody FileBatchOperationRequest operationRequest, User user) {
        ResultBean<?> resultBean = fileMetadataService.batchCopyFiles(operationRequest.getSources(), operationRequest.getTarget(), user);
        if (resultBean.isSuccess()) {
            return ResultData.success(resultBean.getResultCode());
        }
        return ResultData.errorMessage(resultBean.getResultCode());
    }

    /**
     * 查询当前所在目录文件的面包屑导航数据
     *
     * @param id   当前所在目录的文件 id
     * @param user 当前登录的用户
     * @return result
     */
    @GetMapping("/breads")
    public ResultData queryFileBreads(@RequestParam(defaultValue = "") String id, User user) {
        List<FileBreadView> breads = fileMetadataService.queryFileBreads(id, user);
        return ResultData.success(breads);
    }

    /**
     * 分页查询文件列表
     *
     * @param searchRequest 查询请求
     * @param user          当前登录的用户
     * @return result
     */
    @GetMapping("/list")
    public ResultData queryFiles(FileSearchRequest searchRequest, User user) {
        List<FileMetadataView> views = fileMetadataService.queryFiles(searchRequest, user);
        return ResultData.success(views);
    }

    /**
     * 查询文件夹的面包屑导航数据
     *
     * @param queryRequest 父级目录 id
     * @return result
     */
    @GetMapping("/dir_breads")
    public ResultData queryDirBreads(DirBreadsQueryRequest queryRequest) {
        List<FileBreadView> breads = fileMetadataService.queryDirBreads(queryRequest.getParentId());
        return ResultData.success(breads);
    }

    /**
     * 查询目录下次一级的所有文件夹
     *
     * @param searchRequest 查询条件
     * @param user          当前登录的用户
     * @return result
     */
    @GetMapping("/dirs")
    public ResultData queryDirs(DirsSearchRequest searchRequest, User user) {
        List<FileMetadata> dirs = fileMetadataService.queryDirs(searchRequest.getParentId(), searchRequest.getSize(), user);
        return ResultData.success(dirs);
    }

}
