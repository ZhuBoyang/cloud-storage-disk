package online.yangcloud.web.service.meta;

import cn.hutool.core.date.DateUtil;
import online.yangcloud.common.enumration.YesOrNoEnum;
import online.yangcloud.common.mapper.PreviewConvertTaskMapper;
import online.yangcloud.common.model.PreviewConvertTask;
import online.yangcloud.common.tools.ExceptionTools;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author zhuboyang
 * @since 2023年11月02 13:16:09
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PreviewConvertServiceImpl implements PreviewConvertMetaService {

    @Resource
    private PreviewConvertTaskMapper previewConvertTaskMapper;

    @Override
    public void addPreviewTask(PreviewConvertTask task) {
        ExceptionTools.updateError(previewConvertTaskMapper.insertWithPk(task));
    }

    @Override
    public void updatePreviewTask(PreviewConvertTask task) {
        task.setUpdateTime(DateUtil.date().getTime());
        ExceptionTools.updateError(previewConvertTaskMapper.updateById(task));
    }

    @Override
    public PreviewConvertTask queryUnExecutedTask() {
        return previewConvertTaskMapper.findOne(previewConvertTaskMapper.query()
                .where.isOver().eq(YesOrNoEnum.NO.code()).end()
                .orderBy.createTime().asc().end()
                .limit(0, 1));
    }
}
