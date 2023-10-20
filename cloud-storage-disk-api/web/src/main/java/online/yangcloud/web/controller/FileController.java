package online.yangcloud.web.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import online.yangcloud.common.annotation.SessionValid;
import online.yangcloud.common.common.AppConstants;
import online.yangcloud.common.common.AppResultCode;
import online.yangcloud.common.common.ResultData;
import online.yangcloud.common.enumration.FileExtTypeEnum;
import online.yangcloud.common.model.User;
import online.yangcloud.common.model.request.BatchOperator;
import online.yangcloud.common.model.request.IdRequest;
import online.yangcloud.common.model.request.file.*;
import online.yangcloud.common.model.request.user.BreadsLooker;
import online.yangcloud.common.model.view.PagerView;
import online.yangcloud.common.model.view.file.FileMetadataView;
import online.yangcloud.common.tools.ExceptionTools;
import online.yangcloud.common.tools.FileTools;
import online.yangcloud.common.tools.IdTools;
import online.yangcloud.common.tools.SystemTools;
import online.yangcloud.web.service.FileService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhuboyang
 * @since 2023年01月03 12:00:36
 */
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private FileService fileService;

    @Resource
    private FileTools fileTools;

    /**
     * 获取系统支持的各类文件的后缀格式列表
     *
     * @return 后缀格式列表
     */
    @GetMapping("/type_supports")
    public ResultData acquireSupportsTypes() {
        return ResultData.success(JSONUtil.createObj()
                .set("video", fileTools.acquireFileExtProperty().acquireVideoSupports())
                .set("audio", fileTools.acquireFileExtProperty().acquireAudioSupports())
                .set("office", fileTools.acquireFileExtProperty().acquireOfficeSupports()));
    }

    /**
     * 检测文件大小是否允许上传，用户空间剩余量是否足够
     *
     * @param checker 文件大小
     * @return 结果
     */
    @SessionValid
    @PostMapping("/check_size")
    public ResultData checkFileSize(@Valid @RequestBody FileSizeChecker checker, User user) {
        List<String> sizes = StrUtil.split(checker.getSizes(), StrUtil.COMMA);
        List<Boolean> flags = new ArrayList<>();
        long total = user.getUsedSpaceSize();
        for (String o : sizes) {
            long size = Long.parseLong(o);
            if (total + size <= user.getTotalSpaceSize()) {
                total += size;
                flags.add(Boolean.TRUE);
            } else {
                flags.add(Boolean.FALSE);
            }
        }
        return ResultData.success(flags);
    }

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
     * 上传简单文件
     *
     * @param file 文件
     * @return 文件目录
     * @throws IOException IOException
     */
    @SessionValid
    @PostMapping("/simple_upload")
    public ResultData simpleUpload(@RequestParam("file") MultipartFile file) throws IOException {
        if (ObjectUtil.isNull(file)) {
            ExceptionTools.paramLogger();
        }
        String name = file.getOriginalFilename();
        if (StrUtil.isBlank(name)) {
            ExceptionTools.paramLogger();
        }
        String fileName = IdTools.fastSimpleUuid();
        String suffix = name.substring(name.lastIndexOf(StrUtil.DOT));
        String filePath = AppConstants.Uploader.UPLOAD + fileName + suffix;
        file.transferTo(new File(SystemTools.systemPath() + filePath));
        return ResultData.success(filePath);
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
     * 批量删除文件及文件夹
     *
     * @param operator 待删除文件与文件夹 id 列表
     * @param user     当前登录用户
     * @return 删除结果
     */
    @SessionValid
    @PostMapping("/remove")
    public ResultData batchRemove(@Valid @RequestBody BatchOperator operator, User user) {
        fileService.batchDeleteFile(operator.getIdsList(), user);
        return ResultData.success(AppResultCode.SUCCESS);
    }

    /**
     * 批量复制文件及文件夹
     *
     * @param operator 待操作文件及目标文件
     * @param user     当前登录用户
     * @return 复制结果
     */
    @SessionValid
    @PostMapping("/copy")
    public ResultData copyFiles(@Valid @RequestBody FileBatchOperator operator, User user) {
        fileService.batchCopy(operator.getSources(), operator.getTarget(), user);
        return ResultData.success(AppResultCode.SUCCESS, Boolean.TRUE);
    }

    /**
     * 批量移动文件及文件夹
     *
     * @param operator 待操作文件及目标文件
     * @param user     当前登录用户
     * @return 复制结果
     */
    @SessionValid
    @PostMapping("/move")
    public ResultData moveFiles(@Valid @RequestBody FileBatchOperator operator, User user) {
        fileService.batchMove(operator.getSources(), operator.getTarget(), user.getId());
        return ResultData.success(AppResultCode.SUCCESS, Boolean.TRUE);
    }

    /**
     * 批量恢复已删除的文件及文件夹
     *
     * @param operator 要恢复的文件 id 列表
     * @param user     当前登录的用户
     * @return 恢复结果
     */
    @SessionValid
    @PostMapping("/rollback")
    public ResultData rollbackRemoved(@Valid @RequestBody BatchOperator operator, User user) {
        fileService.rollbackTrash(operator.getIdsList(), user);
        return ResultData.success(AppResultCode.SUCCESS, Boolean.TRUE);
    }

    /**
     * 修改文件名
     *
     * @param renamer 文件 id 与修改的名称
     * @return 修改结果
     */
    @SessionValid
    @PostMapping("/rename")
    public ResultData rename(@Valid @RequestBody FileRenamer renamer) {
        fileService.rename(renamer.getId(), renamer.getName());
        return ResultData.success(AppResultCode.SUCCESS, Boolean.TRUE);
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
     * 查询目录下所有的文件夹
     *
     * @param looker 目录 id
     * @param user   当前登录的用户
     * @return 文件夹列表
     */
    @SessionValid
    @PostMapping("/dirs")
    public ResultData queryDirs(@Valid @RequestBody DirLooker looker, User user) {
        return ResultData.success(fileService.queryDirs(looker, user.getId()));
    }

    /**
     * 查询已删除的文件列表
     *
     * @param query 起始文件 id 与每次请求的数据量
     * @param user  当前登录的用户
     * @return 文件列表
     */
    @SessionValid
    @PostMapping("/trash")
    public ResultData queryTrashFiles(@Valid @RequestBody TrashQuery query, User user) {
        return ResultData.success(fileService.queryDeletedFiles(query, user.getId()));
    }

    /**
     * 获取视频的播放地址，以及所在目录下所有的视频文件元数据
     *
     * @param request 视频 id
     * @param user    当前登录的用户
     * @return 播放地址+视频文件元数据
     */
    @SessionValid
    @PostMapping("/play_url")
    public ResultData queryVideoPlayUrl(@Valid @RequestBody IdRequest request, User user) {
        FileMetadataView video = fileService.combineToTmp(request.getId(), user.getId());
        return ResultData.success(video);
    }

    /**
     * 查询指定目录下所有的视频
     *
     * @param request 目录的文件 id
     * @param user    当前登录的用户
     * @return 视频列表
     */
    @SessionValid
    @PostMapping("/videos")
    public ResultData queryVideos(@Valid @RequestBody IdRequest request, User user) {
        List<FileMetadataView> videos = fileService.queryFilesUnderDir(request.getId(), user.getId(), FileExtTypeEnum.VIDEO);
        return ResultData.success(videos);
    }

    /**
     * 查询指定目录下所有的音频
     *
     * @param request 目录的文件 id
     * @param user    当前登录的用户
     * @return 音频列表
     */
    @SessionValid
    @PostMapping("/audios")
    public ResultData queryAudios(@Valid @RequestBody IdRequest request, User user) {
        List<FileMetadataView> audios = fileService.queryFilesUnderDir(request.getId(), user.getId(), FileExtTypeEnum.AUDIO);
        return ResultData.success(audios);
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
