package online.yangcloud.web.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;
import online.yangcloud.common.common.constants.AppConstants;
import online.yangcloud.common.model.User;
import online.yangcloud.common.model.business.email.EmailCodeInfo;
import online.yangcloud.common.model.request.user.UserEnter;
import online.yangcloud.common.model.request.user.UserRegister;
import online.yangcloud.common.model.request.user.UserUpdater;
import online.yangcloud.common.model.view.user.UserView;
import online.yangcloud.common.utils.ExceptionTools;
import online.yangcloud.common.utils.IdTools;
import online.yangcloud.common.utils.RedisTools;
import online.yangcloud.common.utils.ValidateTools;
import online.yangcloud.web.service.FileService;
import online.yangcloud.web.service.UserService;
import online.yangcloud.web.service.meta.UserMetaService;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务
 *
 * @author zhuboyang
 * @since 2023年01月18 11:19:37
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {

    @Resource
    private UserMetaService userMetaService;

    @Resource
    private FileService fileService;

    @Resource
    private RedisTools redisTools;

    @Resource
    private RedissonClient redissonClient;

    @Override
    public void register(UserRegister register) {
        // 检测邮箱验证码是否合法
        String coderInfo = redisTools.get(AppConstants.Email.REGISTER_EMAIL_REDIS_KEY + register.getEmail());
        if (CharSequenceUtil.isBlank(coderInfo)) {
            ExceptionTools.businessLogger("验证码已过期，请重新发送");
        }
        EmailCodeInfo coderObj = JSONUtil.toBean(coderInfo, EmailCodeInfo.class);
        if (!coderObj.getEmailCode().equals(register.getCode())) {
            ExceptionTools.businessLogger("验证码错误，请重新输入");
        }

        // 检测邮箱是否已被注册
        User user = userMetaService.queryUserByEmail(register.getEmail());
        if (ObjectUtil.isNotNull(user)) {
            ExceptionTools.businessLogger("邮箱已被注册");
        }

        // 记录用户账户信息至库中
        user = User.initial(register.getEmail(), register.getPassword());
        userMetaService.insertUser(user);

        // 创建用户的文件根目录
        fileService.initialUserRoot(user.getId());

        // 删除 redis 中的邮箱验证码
        redisTools.delete(AppConstants.Email.REGISTER_EMAIL_REDIS_KEY + register.getEmail());
    }

    @Override
    public String enter(UserEnter enter) {
        // 查询账户信息，并检测账户是否存在
        User user = userMetaService.queryUserByEmail(enter.getEmail());
        ValidateTools.validObjIsNotFound(user);

        // 检测密码是否正确
        if (!user.getPassword().equals(SecureUtil.md5(enter.getPassword()))) {
            ExceptionTools.businessLogger("邮箱或密码错误");
        }

        // 将用户信息存入 redis，生成会话 session，并返回
        String sessionId = IdTools.fastSimpleUuid();
        String userInfo = JSONUtil.toJsonStr(UserView.convert(user));
        redisTools.expire(AppConstants.User.LOGIN_TOKEN + sessionId,
                userInfo,
                AppConstants.User.LOGIN_SESSION_EXPIRE_TIME,
                TimeUnit.MINUTES
        );
        return sessionId;
    }

    @Override
    public void updateUserSpace(List<String> keys, User user) {
        // 计算要变动的空间容量
        long total = 0;
        for (String key : keys) {
            redisTools.delete(key);
            total += Long.parseLong(key.substring(key.lastIndexOf(StrUtil.COLON) + 1));
        }

        // 修改用户账户的空间剩余量
        if (ObjectUtil.isNull(user)) {
            String userId = StrUtil.split(keys.get(0), StrUtil.COLON).get(1);
            user = userMetaService.queryUserById(userId);
        }
        user.setUsedSpaceSize(user.getUsedSpaceSize() + total);
        userMetaService.updateUser(user);
    }

    @Override
    public UserView updateUserInfo(UserUpdater updater, User user) {
        boolean flag = Boolean.FALSE;
        if (StrUtil.isNotBlank(updater.getNickName())) {
            user.setNickName(updater.getNickName());
            flag = Boolean.TRUE;
        }
        if (ObjectUtil.isNotNull(updater.getBirthday()) && updater.getBirthday() > 0) {
            user.setBirthday(updater.getBirthday());
            user.setAge(ValidateTools.calculateAge(DateUtil.date(updater.getBirthday())));
            flag = Boolean.TRUE;
        }
        if (ObjectUtil.isNotNull(updater.getGender()) && updater.getGender() > 0) {
            user.setGender(updater.getGender());
            flag = Boolean.TRUE;
        }
        if (StrUtil.isNotBlank(updater.getPhone())) {
            user.setPhone(updater.getPhone());
            flag = Boolean.TRUE;
        }
        if (flag) {
            userMetaService.updateUser(user);
        }
        return UserView.convert(user);
    }

}
