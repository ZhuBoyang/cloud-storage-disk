package online.yangcloud.common.runner;

import cn.hutool.core.util.ObjectUtil;
import online.yangcloud.common.common.AppProperties;
import online.yangcloud.common.mapper.UserMapper;
import online.yangcloud.common.model.User;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zhuboyang
 * @since 2023年08月15 16:32:41
 */
@Component
public class UserInitialRunner implements ApplicationRunner {

    @Resource
    private UserMapper userMapper;

    @Override
    public void run(ApplicationArguments args) {
        User user = userMapper.findOne(userMapper.query().orderBy.createTime().asc().end().limit(0, 1));
        AppProperties.accountHasInitial = ObjectUtil.isNotNull(user);
    }

}
