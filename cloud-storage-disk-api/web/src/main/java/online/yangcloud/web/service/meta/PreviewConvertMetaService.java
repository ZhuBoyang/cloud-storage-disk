package online.yangcloud.web.service.meta;

import online.yangcloud.common.model.PreviewConvertTask;

/**
 * @author zhuboyang
 * @since 2023年11月02 13:15:59
 */
public interface PreviewConvertMetaService {

    /**
     * 创建转换任务
     *
     * @param task 转换任务
     */
    void addPreviewTask(PreviewConvertTask task);

    /**
     * 修改转换任务
     * @param task 转换任务
     */
    void updatePreviewTask(PreviewConvertTask task);

    /**
     * 获取一条还未执行的任务
     *
     * @return 转换任务
     */
    PreviewConvertTask queryUnExecutedTask();

}
