package online.yangcloud.web.service.meta.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import online.yangcloud.common.common.AppConstants;
import online.yangcloud.common.enumration.YesOrNoEnum;
import online.yangcloud.common.mapper.UserMapper;
import online.yangcloud.common.model.User;
import online.yangcloud.common.utils.ExceptionTools;
import online.yangcloud.common.utils.RedisTools;
import online.yangcloud.common.utils.SystemTools;
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

        // 修改 redis 中已登录的账户信息中的空间使用量，并单独计时。2 分钟内无上传文件操作或退出登录操作，即更新数据库中的账户数据
        String key = AppConstants.Account.SPACE_UPDATE + user.getId() + StrUtil.COLON + usedSpaceSize;
        redisTools.expire(key, StrUtil.EMPTY, 2, TimeUnit.MINUTES);

        // 更新 redis 中的登录信息
        redisTools.expire(AppConstants.Account.LOGIN_TOKEN + SystemTools.getHeaders().getAuthorization(),
                JSONUtil.toJsonStr(user),
                AppConstants.Account.LOGIN_SESSION_EXPIRE_TIME,
                TimeUnit.MINUTES
        );
    }

    @Override
    public void updateUser(User user) {
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
