package online.yangcloud.service.meta.impl;

import online.yangcloud.enumration.YesOrNoEnum;
import online.yangcloud.mapper.UserMapper;
import online.yangcloud.model.User;
import online.yangcloud.service.meta.UserMetaService;
import online.yangcloud.utils.ExceptionTools;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author zhuboyang
 * @since 2023年06月12 15:28:21
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserMetaServiceImpl implements UserMetaService {

    @Resource
    private UserMapper userMapper;

    @Override
    public void insertUser(User user) {
        int updateResult = userMapper.insertWithPk(user);
        if (updateResult == 0) {
            ExceptionTools.businessLogger();
        }
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
