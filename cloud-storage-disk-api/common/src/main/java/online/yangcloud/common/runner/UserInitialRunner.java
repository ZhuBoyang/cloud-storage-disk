package online.yangcloud.common.runner;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import online.yangcloud.common.common.AppProperties;
import online.yangcloud.common.mapper.FileMetadataMapper;
import online.yangcloud.common.mapper.UserMapper;
import online.yangcloud.common.model.FileMetadata;
import online.yangcloud.common.model.User;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zhuboyang
 * @since 2023年08月15 16:32:41
 */
@Order(4)
@Component
public class UserInitialRunner implements ApplicationRunner {

    @Resource
    private UserMapper userMapper;

    @Resource
    private FileMetadataMapper fileMetadataMapper;

    @Override
    public void run(ApplicationArguments args) {
        User user = userMapper.findOne(userMapper.query().orderBy.createTime().asc().end().limit(0, 1));
        AppProperties.ACCOUNT_HAS_INITIAL = ObjectUtil.isNotNull(user);
        if (ObjectUtil.isNotNull(user)) {
            FileMetadata root = fileMetadataMapper.findOne(fileMetadataMapper.query()
                    .where.pid().eq(StrUtil.EMPTY)
                    .and.userId().eq(user.getId()).end());
            AppProperties.ROOT_DIR_ID = root.getId();
        }
    }

}
