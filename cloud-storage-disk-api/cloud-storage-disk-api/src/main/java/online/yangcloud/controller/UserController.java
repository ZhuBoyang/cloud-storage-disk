package online.yangcloud.controller;

import online.yangcloud.common.ResultBean;
import online.yangcloud.common.ResultData;
import online.yangcloud.common.resultcode.AppResultCode;
import online.yangcloud.model.ao.user.UserLoginRequest;
import online.yangcloud.model.ao.user.UserRegisterRequest;
import online.yangcloud.model.vo.user.UserView;
import online.yangcloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 用户账户操作接口
 *
 * @author zhuboyang
 * @since 2023年01月18 13:43:14
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     *
     * @param registerRequest 请求参数
     * @return result
     */
    @PostMapping("/register")
    public ResultData register(@RequestBody @Valid UserRegisterRequest registerRequest) {
        ResultBean<?> resultBean = userService.addUser(registerRequest.getUserName(), registerRequest.getEmail(), registerRequest.getPassword());
        if (resultBean.isSuccess()) {
            return ResultData.success(AppResultCode.SUCCESS.clone("账户注册成功，请前往登录"));
        }
        return ResultData.errorMessage(resultBean.getResultCode());
    }

    /**
     * 用户登录
     *
     * @param loginRequest 请求参数
     * @return result
     */
    @PostMapping("/login")
    public ResultData login(@RequestBody @Valid UserLoginRequest loginRequest) {
        ResultBean<UserView> resultBean = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
        if (resultBean.isSuccess()) {
            return ResultData.success(resultBean.getBean());
        }
        return ResultData.errorMessage(resultBean.getResultCode());
    }

}
