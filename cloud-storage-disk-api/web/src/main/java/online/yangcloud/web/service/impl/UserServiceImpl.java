package online.yangcloud.web.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import online.yangcloud.common.annotation.TimeConsuming;
import online.yangcloud.common.common.AppConstants;
import online.yangcloud.common.common.AppProperties;
import online.yangcloud.common.model.User;
import online.yangcloud.common.model.request.user.PasswordUpdater;
import online.yangcloud.common.model.request.user.UserEnter;
import online.yangcloud.common.model.request.user.UserInitializer;
import online.yangcloud.common.model.request.user.UserUpdater;
import online.yangcloud.common.model.view.user.UserView;
import online.yangcloud.common.tools.*;
import online.yangcloud.web.service.FileService;
import online.yangcloud.web.service.UserService;
import online.yangcloud.web.service.meta.UserMetaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务
 *
 * @author zhuboyang
 * @since 2023年01月18 11:19:37
 */
@Service
@Transactional(rollbackFor = Exception.class)
@TimeConsuming
public class UserServiceImpl implements UserService {

    @Resource
    private UserMetaService userMetaService;

    @Resource
    private FileService fileService;

    @Resource
    private RedisTools redisTools;

    @Override
    public void initialize(UserInitializer initializer) {
        // 检测是否已完成账户信息初始化
        if (AppProperties.ACCOUNT_HAS_INITIAL) {
            ExceptionTools.businessLogger("已完成账户初始化，请前往登录");
        }

        // 校验邮箱格式是否合法
        if (!ReUtil.isMatch(AppConstants.Regex.EMAIL_REGULAR, initializer.getEmail())) {
            ExceptionTools.businessLogger("邮箱不合法，请查证后重新输入");
        }

        // 完成指定次数对密码的 md5 加密
        int lastCount = AppConstants.Account.ENCRYPT_COUNT - Integer.parseInt(initializer.getPassword().substring(0, 1));
        String password = StrUtil.sub(initializer.getPassword(), 1, initializer.getPassword().length());
        password = ValidateTools.encryptInCount(password, lastCount);

        // 完成指定次数对重复密码的 md5 加密
        lastCount = AppConstants.Account.ENCRYPT_COUNT - Integer.parseInt(initializer.getRepeat().substring(0, 1));
        String repeat = StrUtil.sub(initializer.getRepeat(), 1, initializer.getRepeat().length());
        repeat = ValidateTools.encryptInCount(repeat, lastCount);

        // 对比密码是否一致
        if (!password.equals(repeat)) {
            ExceptionTools.businessLogger("两次输入不一致，请重新输入");
        }

        // 初始化用户账户信息
        User account = User.initial(initializer.getEmail(), password);
        userMetaService.insertUser(account);

        // 创建用户的文件根目录
        fileService.initialUserRoot(account.getId());

        // 修改是否已完成初始化的标志
        AppProperties.ACCOUNT_HAS_INITIAL = Boolean.TRUE;
    }

    @Override
    public String enter(UserEnter enter) throws IOException {
        // 查询账户信息，并检测账户是否存在
        User user = userMetaService.queryUserByEmail(enter.getEmail());
        ValidateTools.validObjIsNotFound(user);
        {
            // 这里重新计算下空间使用的总容量、已用容量和可用容量
            // 考虑到磁盘本身就会存在一些文件，而这些文件是不包含在本系统内的，所以在计算时需要排除掉，剩下的才是本系统可用的总空间大小
            Long usableSpace = DiskTools.acquireDiskInfo().getUsableSpace();
            long projectSize = FileTools.calculateDirSpace(SystemTools.systemPath());
            user.setTotalSpaceSize(usableSpace + projectSize);
        }

        // 检测密码是否正确
        int encryptCount = AppConstants.Account.ENCRYPT_COUNT - Integer.parseInt(enter.getPassword().substring(0, 1));
        String password = StrUtil.sub(enter.getPassword(), 1, enter.getPassword().length());
        password = ValidateTools.encryptInCount(password, encryptCount);
        if (!user.getPassword().equals(password)) {
            ExceptionTools.businessLogger("邮箱或密码错误");
        }

        // 将用户信息存入 redis，生成会话 session，并返回
        String sessionId = IdTools.fastSimpleUuid();
        String userInfo = JSONUtil.toJsonStr(UserView.convert(user));
        redisTools.expire(AppConstants.Account.LOGIN_TOKEN + sessionId,
                userInfo,
                AppConstants.Account.LOGIN_SESSION_EXPIRE_TIME,
                TimeUnit.MINUTES
        );
        return sessionId;
    }

    @Override
    public UserView updateUserInfo(UserUpdater updater, User user) {
        String oldAvatar = user.getAvatar();
        boolean flag = Boolean.FALSE;
        if (StrUtil.isNotBlank(updater.getAvatar())) {
            user.setAvatar(updater.getAvatar());
            flag = Boolean.TRUE;
        }
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
            if (StrUtil.isNotBlank(updater.getAvatar())) {
                FileUtil.del(SystemTools.systemPath() + oldAvatar);
            }
        }
        return UserView.convert(user);
    }

    @Override
    public UserView updatePassword(PasswordUpdater updater, User user) {
        // 完成对新密码的指定次数的加密
        int encryptCount = AppConstants.Account.ENCRYPT_COUNT - Integer.parseInt(updater.getPassword().substring(0, 1));
        String password = updater.getPassword().substring(1);
        password = ValidateTools.encryptInCount(password, encryptCount);

        // 完成对再次输入密码的指定次数的加密
        encryptCount = AppConstants.Account.ENCRYPT_COUNT - Integer.parseInt(updater.getRepeat().substring(0, 1));
        String repeat = updater.getRepeat().substring(1);
        repeat = ValidateTools.encryptInCount(repeat, encryptCount);

        // 校验两次密码是否一致
        if (!password.equals(repeat)) {
            ExceptionTools.businessLogger("两次密码输入不一致");
        }

        // 更新账户信息
        user.setPassword(password);
        userMetaService.updateUser(user);
        return UserView.convert(user);
    }

}
