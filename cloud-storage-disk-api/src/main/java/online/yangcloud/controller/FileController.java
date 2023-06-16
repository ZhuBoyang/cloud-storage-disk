package online.yangcloud.controller;

import online.yangcloud.annotation.SessionValid;
import online.yangcloud.common.ResultData;
import online.yangcloud.common.resultcode.AppResultCode;
import online.yangcloud.model.User;
import online.yangcloud.model.ao.file.FileMerger;
import online.yangcloud.model.ao.file.FileMkdir;
import online.yangcloud.model.ao.file.FileSearcher;
import online.yangcloud.model.ao.file.FileUploader;
import online.yangcloud.model.ao.user.BreadsLooker;
import online.yangcloud.model.vo.PagerView;
import online.yangcloud.model.vo.file.FileMetadataView;
import online.yangcloud.service.FileService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

/**
 * @author zhuboyang
 * @since 2023年01月03 12:00:36
 */
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private FileService fileService;

    /**
     * 检测文件块是否已在库中
     *
     * @param uploader 文件块信息
     * @return 检测结果：true-已存在；false-尚不存在
     */
    @SessionValid
    @PostMapping("/check_exist")
    public ResultData checkBlockExist(@Valid @RequestBody FileUploader uploader) {
        return ResultData.success(fileService.checkBlockExist(uploader));
    }

    /**
     * 上传文件块
     *
     * @param uploader 文件块参数
     * @return 是否上传成功
     */
    @SessionValid
    @PostMapping("/upload")
    public ResultData uploadBlock(FileUploader uploader) {
        return ResultData.success(fileService.blockUpload(uploader));
    }

    /**
     * 合并文件
     *
     * @param merger 文件唯一标识
     * @param user   当前登录的用户
     * @return 上传后的文件元数据
     * @throws IOException IOException
     */
    @SessionValid
    @PostMapping("/merge")
    public ResultData merge(@Valid @RequestBody FileMerger merger, User user) throws IOException {
        return ResultData.success(fileService.fileMerge(merger.getIdentifier(), user));
    }

    /**
     * 新建文件夹
     *
     * @param mkdir 父级文件 id 与文件夹名称
     * @param user  当前登录的用户
     * @return 文件元数据
     */
    @SessionValid
    @PostMapping("/mkdir")
    public ResultData mkdir(@Valid @RequestBody FileMkdir mkdir, User user) {
        FileMetadataView view = fileService.mkdir(mkdir.getId(), mkdir.getName(), user.getId());
        return ResultData.success(AppResultCode.SUCCESS.clone("文件夹创建成功"), view);
    }

    /**
     * 分页查询用户下所有的文件
     *
     * @param searcher searcher
     * @param user     当前登录的用户
     * @return 文件列表
     */
    @SessionValid
    @GetMapping("/pager")
    public ResultData queryFilesInPager(FileSearcher searcher, User user) {
        PagerView<FileMetadataView> view = fileService.queryFiles(searcher, user.getId());
        return ResultData.success(view);
    }

    /**
     * 查询到面包屑导航数据
     *
     * @param looker 当前所在目录的文件 id
     * @param user   当前登录的用户
     * @return 导航项列表
     */
    @SessionValid
    @PostMapping("/breads")
    public ResultData queryBreads(@Valid @RequestBody BreadsLooker looker, User user) {
        return ResultData.success(fileService.queryBreads(looker.getId(), user.getId()));
    }

    /**
     * 文件下载
     *
     * @param id       文件 id
     * @param response 响应
     */
    @GetMapping("/download/{id}")
    public void download(@PathVariable String id, HttpServletResponse response) {
        fileService.download(id, response);
    }

}
