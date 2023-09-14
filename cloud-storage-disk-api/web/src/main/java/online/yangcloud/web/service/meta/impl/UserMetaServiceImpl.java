package online.yangcloud.web.service.meta.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import online.yangcloud.common.annotation.TimeConsuming;
import online.yangcloud.common.common.AppConstants;
import online.yangcloud.common.enumration.YesOrNoEnum;
import online.yangcloud.common.mapper.UserMapper;
import online.yangcloud.common.model.User;
import online.yangcloud.common.tools.ExceptionTools;
import online.yangcloud.common.tools.RedisTools;
import online.yangcloud.common.tools.SystemTools;
import online.yangcloud.web.service.meta.UserMetaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author zhuboyang
 * @since 2023年06月12 15:28:21
 */
@Service
@Transactional(rollbackFor = Exception.class)
@TimeConsuming
public class UserMetaServiceImpl implements UserMetaService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisTools redisTools;

    @Override
    public void insertUser(User user) {
        int updateResult = userMapper.insertWithPk(user);
        if (updateResult == 0) {
            ExceptionTools.businessLogger();
        }
    }

    @Override
    public void updateSpaceSize(User user, Long spaceSize) {
        // 增加空间使用量
        long usedSpaceSize = user.getUsedSpaceSize() + spaceSize;
        user.setUsedSpaceSize(usedSpaceSize);
        updateUser(user);

        // 更新 redis 中的登录信息
        redisTools.expire(AppConstants.Account.LOGIN_TOKEN + SystemTools.getHeaders().getAuthorization(),
                JSONUtil.toJsonStr(user),
                AppConstants.Account.LOGIN_SESSION_EXPIRE_TIME,
                TimeUnit.MINUTES
        );
    }

    @Override
    public void updateUser(User user) {
        user.setUpdateTime(DateUtil.date().getTime());
        int updateResult = userMapper.updateById(user);
        if (updateResult == 0) {
            ExceptionTools.businessLogger();
        }
    }

    @Override
    public User queryUserById(String id) {
        return userMapper.findOne(userMapper.query()
                .where.id().eq(id).and.isDelete().eq(YesOrNoEnum.NO.code()).end());
    }

    @Override
    public User queryUserByEmail(String email) {
        return userMapper.findOne(userMapper.query()
                .where.email().eq(email).and.isDelete().eq(YesOrNoEnum.NO.code()).end());
    }

}
