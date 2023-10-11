package online.yangcloud.web.task;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import online.yangcloud.common.common.AppConstants;
import online.yangcloud.common.model.User;
import online.yangcloud.common.tools.RedisTools;
import online.yangcloud.web.service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhuboyang
 * @since 2023年10月11 10:53:58
 */
@Component
public class UpdateUserSpaceTask {

    @Resource
    private RedisTools redisTools;

    @Resource
    private UserService userService;

    /**
     * 自项目启动开始，每 10 秒钟更新一次用户空间容量数据
     */
    @Scheduled(fixedDelay = 10000)
    public void updateUserSpace() {
        List<String> loginUsers = redisTools.keys(AppConstants.Account.LOGIN_TOKEN);
        if (loginUsers.isEmpty()) {
            return;
        }

        for (String key : loginUsers) {
            // 更新空间增量
            String sessionId = key.substring(key.lastIndexOf(StrUtil.COLON) + 1);
            userService.updateIncrementSize(sessionId, JSONUtil.toBean(redisTools.get(key), User.class));
        }
    }

}
