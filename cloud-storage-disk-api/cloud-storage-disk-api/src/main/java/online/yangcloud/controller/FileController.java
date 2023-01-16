package online.yangcloud.controller;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import online.yangcloud.common.ResultBean;
import online.yangcloud.common.ResultData;
import online.yangcloud.common.resultcode.AppResultCode;
import online.yangcloud.model.ao.file.*;
import online.yangcloud.model.po.FileMetadata;
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
     * @return result
     */
    @PostMapping("/mkdir")
    public ResultData mkdir(@RequestBody MkdirRequest request) {
        FileMetadataView fileView = fileMetadataService.mkdir(request.getPid(), request.getFileName());
        return ResultData.success(fileView);
    }

    /**
     * 批量删除文件
     *
     * @param deleteRequest 待删除文件 id 列表
     * @return result
     */
    @PostMapping("/batch_delete")
    public ResultData batchDeleteFiles(@RequestBody BatchDeleteRequest deleteRequest) {
        List<String> fileIds = CharSequenceUtil.split(deleteRequest.getFileString(), StrUtil.COMMA);
        ResultBean<?> resultBean = fileMetadataService.batchDeleteFile(fileIds);
        if (resultBean.isSuccess()) {
            return ResultData.success(AppResultCode.SUCCESS);
        }
        return ResultData.errorMessage(resultBean.getResultCode());
    }

    /**
     * 批量移动文件
     *
     * @param operationRequest 待移动文件与目标文件夹
     * @return result
     */
    @PostMapping("/batch_move")
    public ResultData batchMoveFiles(@RequestBody FileBatchOperationRequest operationRequest) {
        ResultBean<?> resultBean = fileMetadataService.batchMoveFiles(operationRequest.getSources(), operationRequest.getTarget());
        if (resultBean.isSuccess()) {
            return ResultData.success(resultBean.getResultCode());
        }
        return ResultData.errorMessage(resultBean.getResultCode());
    }

    /**
     * 批量复制文件
     *
     * @param operationRequest 待复制文件与目标文件夹
     * @return result
     */
    @PostMapping("/batch_copy")
    public ResultData batchCopyFiles(@RequestBody FileBatchOperationRequest operationRequest) {
        ResultBean<?> resultBean = fileMetadataService.batchCopyFiles(operationRequest.getSources(), operationRequest.getTarget());
        if (resultBean.isSuccess()) {
            return ResultData.success(resultBean.getResultCode());
        }
        return ResultData.errorMessage(resultBean.getResultCode());
    }

    /**
     * 查询当前所在目录文件的面包屑导航数据
     *
     * @param id 当前所在目录的文件 id
     * @return result
     */
    @GetMapping("/breads")
    public ResultData queryFileBreads(@RequestParam(defaultValue = "") String id) {
        List<FileBreadView> breads = fileMetadataService.queryFileBreads(id);
        return ResultData.success(breads);
    }

    /**
     * 分页查询文件列表
     *
     * @param searchRequest 查询请求
     * @return result
     */
    @GetMapping("/list")
    public ResultData queryFiles(FileSearchRequest searchRequest) {
        List<FileMetadataView> views = fileMetadataService.queryFiles(searchRequest);
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
     * @return result
     */
    @GetMapping("/dirs")
    public ResultData queryDirs(DirsSearchRequest searchRequest) {
        List<FileMetadata> dirs = fileMetadataService.queryDirs(searchRequest.getParentId(), searchRequest.getSize());
        return ResultData.success(dirs);
    }

}
