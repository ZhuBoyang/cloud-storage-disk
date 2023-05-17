package online.yangcloud.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;
import online.yangcloud.common.ResultBean;
import online.yangcloud.common.constants.UserConstants;
import online.yangcloud.common.resultcode.AppResultCode;
import online.yangcloud.exception.BusinessException;
import online.yangcloud.mapper.UserMapper;
import online.yangcloud.model.User;
import online.yangcloud.model.ao.user.UserUpdateRequest;
import online.yangcloud.model.vo.user.LoginView;
import online.yangcloud.model.vo.user.UserView;
import online.yangcloud.service.UserService;
import online.yangcloud.utils.RedisTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTools redisTools;

    @Override
    public ResultBean<UserView> addUser(String userName, String email, String password) {
        // 校验邮箱是否已被使用
        User user = userMapper.findOne(userMapper.query().where.email().eq(email).end());
        if (ObjUtil.isNotNull(user)) {
            logger.error("邮箱已被注册，请换个邮箱注册");
            return ResultBean.resultCode(AppResultCode.FAILURE.clone("邮箱已被注册，请换个邮箱注册"));
        }

        // 封装用户账户数据
        user = User.packageData(userName, email, password);

        // 数据入库
        int updateResult = userMapper.insertWithPk(user);
        if (updateResult == 0) {
            return ResultBean.error();
        }
        return ResultBean.success(BeanUtil.copyProperties(user, UserView.class));
    }

    @Override
    public ResultBean<UserView> updateUser(UserUpdateRequest updateRequest) {
        User user = userMapper.findById(updateRequest.getId());
        if (ObjUtil.isNull(user)) {
            if (CharSequenceUtil.isBlank(updateRequest.getEmail())) {
                logger.error("账户不存在，请查证");
                return ResultBean.resultCode(AppResultCode.FAILURE.clone("账户不存在，请查证"));
            } else {
                user = userMapper.findOne(userMapper.query().where.email().eq(updateRequest.getEmail()).end());
                if (ObjUtil.isNull(user)) {
                    logger.error("账户不存在，请查证");
                    return ResultBean.resultCode(AppResultCode.FAILURE.clone("账户不存在，请查证"));
                }
            }
        }
        boolean updateFlag = Boolean.FALSE;
        if (CharSequenceUtil.isNotBlank(updateRequest.getUserName())) {
            if (!updateRequest.getUserName().equals(user.getUserName())) {
                user.setUserName(updateRequest.getUserName());
                updateFlag = Boolean.TRUE;
            }
        }
        if (CharSequenceUtil.isNotBlank(updateRequest.getPassword())) {
            if (!updateRequest.getPassword().equals(user.getPassword())) {
                user.setPassword(SecureUtil.md5(updateRequest.getPassword()));
                updateFlag = Boolean.TRUE;
            }
        }
        if (CharSequenceUtil.isNotBlank(updateRequest.getBirthday())) {
            DateTime birthday = DateUtil.parse(updateRequest.getBirthday());
            if (!birthday.equals(user.getBirthday())) {
                user.setBirthday(birthday);
                updateFlag = Boolean.TRUE;
            }
        }
        if (ObjUtil.isNotNull(updateRequest.getAge())) {
            if (!updateRequest.getAge().equals(user.getAge())) {
                user.setAge(updateRequest.getAge());
                updateFlag = Boolean.TRUE;
            }
        }
        if (ObjUtil.isNotNull(updateRequest.getGender())) {
            if (!updateRequest.getGender().equals(user.getGender())) {
                user.setGender(updateRequest.getGender());
                updateFlag = Boolean.TRUE;
            }
        }
        if (CharSequenceUtil.isNotBlank(updateRequest.getPhone())) {
            if (!updateRequest.getPhone().equals(user.getPhone())) {
                user.setPhone(updateRequest.getPhone());
                updateFlag = Boolean.TRUE;
            }
        }
        if (!updateFlag) {
            return ResultBean.success();
        }
        int updateResult = userMapper.updateById(user);
        if (updateResult == 0) {
            logger.error("用户信息修改失败，请重试");
            throw new BusinessException("用户信息修改失败，请重试");
        }
        return ResultBean.success(BeanUtil.copyProperties(user, UserView.class));
    }

    @Override
    public ResultBean<LoginView> login(String email, String password) {
        User user = userMapper.findOne(userMapper.query().where.email().eq(email).end());

        // 校验用户是否存在，及校验密码是否正确
        if (ObjUtil.isNull(user) || !SecureUtil.md5(password).equals(user.getPassword())) {
            logger.error("邮箱或密码不正确");
            return ResultBean.resultCode(AppResultCode.FAILURE.clone("邮箱或密码不正确"));
        }

        // 将登录信息记录到 redis 中
        LoginView loginView = BeanUtil.copyProperties(user, LoginView.class);
        String sessionId = IdUtil.fastSimpleUUID();
        redisTools.expire(UserConstants.LOGIN_SESSION + sessionId, JSONUtil.toJsonStr(loginView), UserConstants.LOGIN_SESSION_EXPIRE_TIME, TimeUnit.SECONDS);

        // 封装视图数据，并返回
        return ResultBean.resultCode(AppResultCode.SUCCESS.clone("登录成功"), loginView.setSessionId(sessionId));
    }

}
