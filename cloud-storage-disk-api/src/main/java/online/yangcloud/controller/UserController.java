package online.yangcloud.controller;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import online.yangcloud.annotation.SessionValid;
import online.yangcloud.common.ResultData;
import online.yangcloud.common.constants.AppConstants;
import online.yangcloud.common.resultcode.AppResultCode;
import online.yangcloud.model.User;
import online.yangcloud.model.request.user.UserEnter;
import online.yangcloud.model.request.user.UserRegister;
import online.yangcloud.model.view.file.FileMetadataView;
import online.yangcloud.model.view.user.LoginView;
import online.yangcloud.model.view.user.UserView;
import online.yangcloud.service.FileService;
import online.yangcloud.service.UserService;
import online.yangcloud.utils.ExceptionTools;
import online.yangcloud.utils.RedisTools;
import online.yangcloud.utils.SystemTools;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
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
     * 用户注册
     *
     * @return result
     */
    @PostMapping("/register")
    public ResultData register(@Valid @RequestBody UserRegister register) {
        ExceptionTools.businessLogger("暂未开放注册");
        userService.register(register);
        return ResultData.success(AppResultCode.SUCCESS.clone("注册成功，请前往登录"), Boolean.TRUE);
    }

    /**
     * 用户登录
     *
     * @return result
     */
    @PostMapping("/login")
    public ResultData login(@Valid @RequestBody UserEnter enter) {
        String token = userService.enter(enter);
        UserView view = JSONUtil.toBean(redisTools.get(AppConstants.User.LOGIN_TOKEN + token), UserView.class);
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
        // 更新用户账户空间变更
        List<String> keys = redisTools.keys(AppConstants.User.SPACE_UPDATE + user.getId());
        if (ObjectUtil.isNotNull(keys) && keys.size() == 1) {
            userService.updateUserSpace(keys, user);
        }
        // 更新 redis 中的登录信息，设置 1s 后过期
        redisTools.expire(AppConstants.User.LOGIN_TOKEN + SystemTools.getHeaders().getAuthorization(),
                JSONUtil.toJsonStr(user),
                1,
                TimeUnit.SECONDS
        );
        return ResultData.success(Boolean.TRUE);
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
    public ResultData enteredUserInfo(User user) {
        return ResultData.success(UserView.convert(user));
    }

}
