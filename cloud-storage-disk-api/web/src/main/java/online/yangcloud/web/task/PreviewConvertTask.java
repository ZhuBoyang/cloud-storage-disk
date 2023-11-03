package online.yangcloud.web.task;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import online.yangcloud.common.common.AppConstants;
import online.yangcloud.common.tools.RedisTools;
import online.yangcloud.web.service.ThumbnailService;
import online.yangcloud.web.service.meta.MetaService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zhuboyang
 * @since 2023年11月02 14:33:02
 */
@Component
public class PreviewConvertTask {

    /**
     * 是否是首次执行
     */
    private static boolean isFirstTime = true;

    @Resource
    private ThumbnailService thumbnailService;
    
    @Resource
    private RedisTools redisTools;

    @Scheduled(fixedDelay = 1000)
    public void previewConvertProcessInLoop() {
        if (isFirstTime) {
            // 第一次不执行，为了不与建表逻辑冲突
            isFirstTime = false;
            ThreadUtil.safeSleep(1000);
            return;
        }
        String fileId = redisTools.get(AppConstants.PreviewConverter.CONVERT_TASK);
        if (StrUtil.isBlank(fileId)) {
            // 没有任务要执行的情况下，不执行后续逻辑
            return;
        }
        thumbnailService.thumbnail();
    }

}
