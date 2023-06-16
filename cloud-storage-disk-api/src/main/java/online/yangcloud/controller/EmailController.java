package online.yangcloud.controller;

import online.yangcloud.common.ResultData;
import online.yangcloud.common.resultcode.AppResultCode;
import online.yangcloud.model.ao.email.EmailSender;
import online.yangcloud.service.EmailService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author zhuboyang
 * @since 2023年06月12 16:05:49
 */
@RestController
@RequestMapping("/email")
public class EmailController {

    @Resource
    private EmailService emailService;

    /**
     * 给注册的账号发送邮箱验证码
     *
     * @param sender 注册邮箱
     * @return 发送结果
     */
    @PostMapping("/register")
    public ResultData register(@Valid @RequestBody EmailSender sender) {
        emailService.sendRegisterEmail(sender);
        return ResultData.success(AppResultCode.SUCCESS);
    }

}
