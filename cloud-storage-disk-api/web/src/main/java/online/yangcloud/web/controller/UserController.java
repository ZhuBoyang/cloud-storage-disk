package online.yangcloud.web.controller;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import online.yangcloud.common.annotation.RepeatRequest;
import online.yangcloud.common.annotation.SessionValid;
import online.yangcloud.common.common.AppConstants;
import online.yangcloud.common.common.AppProperties;
import online.yangcloud.common.common.AppResultCode;
import online.yangcloud.common.common.ResultData;
import online.yangcloud.common.model.User;
import online.yangcloud.common.model.request.user.PasswordUpdater;
import online.yangcloud.common.model.request.user.UserEnter;
import online.yangcloud.common.model.request.user.UserInitializer;
import online.yangcloud.common.model.request.user.UserUpdater;
import online.yangcloud.common.model.view.file.FileMetadataView;
import online.yangcloud.common.model.view.user.LoginView;
import online.yangcloud.common.model.view.user.UserView;
import online.yangcloud.common.tools.DiskTools;
import online.yangcloud.common.tools.FileTools;
import online.yangcloud.common.tools.RedisTools;
import online.yangcloud.common.tools.SystemTools;
import online.yangcloud.web.service.FileService;
import online.yangcloud.web.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 用户账户操作接口
 *
 * @author zhuboyang
 * @since 2023年01月18 13:43:14
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private FileService fileService;

    @Resource
    private RedisTools redisTools;

    /**
     * 检测用户账户是否已进行初始化
     *
     * @return result
     */
    @PostMapping("/has_initial")
    public ResultData accountHasInitialed() {
        return ResultData.success(AppProperties.ACCOUNT_HAS_INITIAL);
    }

    /**
     * 初始化用户账户
     *
     * @return result
     */
    @RepeatRequest
    @PostMapping("/initial")
    public ResultData register(@Valid @RequestBody UserInitializer initializer) {
        userService.initialize(initializer);
        return ResultData.success(AppResultCode.SUCCESS.clone("账户初始化完成，请前往登录"), Boolean.TRUE);
    }

    /**
     * 用户登录
     *
     * @return result
     */
    @PostMapping("/login")
    public ResultData login(@Valid @RequestBody UserEnter enter) throws IOException {
        String token = userService.enter(enter);
        UserView view = JSONUtil.toBean(redisTools.get(AppConstants.Account.LOGIN_TOKEN + token), UserView.class);
        FileMetadataView file = fileService.queryById(CharSequenceUtil.EMPTY, view.getId());
        return ResultData.success(LoginView.pack(token, file.getId()));
    }

    /**
     * 退出登录
     *
     * @return result
     */
    @SessionValid
    @PostMapping("/logout")
    public ResultData logout(User user) {
        // 更新账户的空间增量
        userService.updateIncrementSize(SystemTools.getHeaders().getAuthorization(), user);

        // 更新 redis 中的登录信息，设置 1s 后过期
        userService.updateRedisCache(user, 1);
        return ResultData.success(Boolean.TRUE);
    }

    /**
     * 修改账户资料
     *
     * @param updater 要修改的内容
     * @param user    当前登录用户
     * @return 账户资料
     */
    @SessionValid
    @PostMapping("/update")
    public ResultData updateInfo(@Valid @RequestBody UserUpdater updater, User user) {
        UserView view = userService.updateUserInfo(updater, user);
        userService.updateRedisCache(view, AppConstants.Account.LOGIN_SESSION_EXPIRE_TIME);
        return ResultData.success(view);
    }

    /**
     * 修改账户密码
     *
     * @param updater 新的密码
     * @param user    当前登录的账户
     * @return result
     */
    @SessionValid
    @PostMapping("/password_update")
    public ResultData passwordUpdate(@Valid @RequestBody PasswordUpdater updater, User user) {
        UserView view = userService.updatePassword(updater, user);
        // 更新 redis 中的登录信息，设置 1s 后过期
        redisTools.expire(AppConstants.Account.LOGIN_TOKEN + SystemTools.getHeaders().getAuthorization(),
                JSONUtil.toJsonStr(view),
                1,
                TimeUnit.SECONDS
        );
        return ResultData.success(AppResultCode.SUCCESS.clone("密码修改成功，请重新登录"), Boolean.TRUE);
    }

    /**
     * 重置账户密码
     *
     * @return result
     */
    @SessionValid
    @PostMapping("/reset")
    public ResultData resetPwd() {
        return ResultData.success();
    }

    /**
     * 获取当前已登录的用户信息
     *
     * @param user 当前登录的用户的信息
     * @return 用户信息
     */
    @SessionValid
    @GetMapping("/info")
    public ResultData enteredUserInfo(User user) throws IOException {
        // 获取 redis 中的空间增量大小
        List<String> keys = redisTools.keys(AppConstants.Account.INCREMENT + user.getId());
        long increment = 0;
        if (!keys.isEmpty()) {
            for (String key : keys) {
                increment += Long.parseLong(key.substring(key.lastIndexOf(StrUtil.COLON) + 1));
            }
        }

        // 查询账户信息
        Long usableSpace = DiskTools.acquireDiskInfo().getUsableSpace();
        long projectSize = FileTools.calculateDirSpace(SystemTools.systemPath());
        user.setTotalSpaceSize(usableSpace + projectSize);
        if (increment != 0) {
            user.setUsedSpaceSize(user.getUsedSpaceSize() + increment);
        }
        return ResultData.success(UserView.convert(user));
    }

}
