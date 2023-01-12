package online.yangcloud.controller;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import online.yangcloud.common.ResultBean;
import online.yangcloud.common.ResultData;
import online.yangcloud.model.ao.file.BatchDeleteRequest;
import online.yangcloud.model.ao.file.FileSearchRequest;
import online.yangcloud.model.ao.file.MkdirRequest;
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
            return ResultData.success();
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

}
