package online.yangcloud.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;
import online.yangcloud.common.ResultBean;
import online.yangcloud.common.constants.UserConstants;
import online.yangcloud.common.resultcode.AppResultCode;
import online.yangcloud.model.mapper.UserMapper;
import online.yangcloud.model.po.User;
import online.yangcloud.model.vo.user.UserView;
import online.yangcloud.service.UserService;
import online.yangcloud.utils.RedisUtil;
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
    private RedisUtil redisUtil;

    @Override
    public ResultBean<?> addUser(String userName, String email, String password) {
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
        return ResultBean.success();
    }

    @Override
    public ResultBean<UserView> login(String email, String password) {
        User user = userMapper.findOne(userMapper.query().where.email().eq(email).end());

        // 校验用户是否存在，及校验密码是否正确
        if (ObjUtil.isNull(user) || !SecureUtil.md5(password).equals(user.getPassword())) {
            logger.error("邮箱或密码不正确");
            return ResultBean.resultCode(AppResultCode.FAILURE.clone("邮箱或密码不正确"));
        }

        // 将登录信息记录到 redis 中
        UserView userView = BeanUtil.copyProperties(user, UserView.class);
        redisUtil.expire(UserConstants.LOGIN_SESSION + email, JSONUtil.toJsonStr(userView), UserConstants.LOGIN_SESSION_EXPIRE_TIME, TimeUnit.SECONDS);

        // 封装视图数据，并返回
        return ResultBean.success(userView);
    }

}
