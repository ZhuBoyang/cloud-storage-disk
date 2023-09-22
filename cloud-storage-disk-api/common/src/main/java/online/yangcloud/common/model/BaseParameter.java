package online.yangcloud.common.model;

import cn.hutool.core.date.DateUtil;
import cn.org.atool.fluent.mybatis.base.RichEntity;
import online.yangcloud.common.annotation.DatabaseColumn;
import online.yangcloud.common.annotation.DatabaseIndex;
import online.yangcloud.common.enumration.DatabaseColumnTypeEnum;
import online.yangcloud.common.enumration.YesOrNoEnum;

/**
 * @author zhuboyang
 * @since 2023年06月02 20:59:56
 */
public class BaseParameter extends RichEntity {

    /**
     * 创建时间
     */
    @DatabaseColumn(name = "create_time", type = DatabaseColumnTypeEnum.BIGINT, canNull = false, comment = "创建时间")
    private Long createTime = DateUtil.date().getTime();

    /**
     * 修改时间
     */
    @DatabaseColumn(name = "update_time", type = DatabaseColumnTypeEnum.BIGINT, canNull = false, comment = "修改时间")
    private Long updateTime = DateUtil.date().getTime();

    /**
     * 是否已删除
     */
    @DatabaseColumn(name = "is_delete", type = DatabaseColumnTypeEnum.TINYINT, defaultValue = "0", comment = "是否已删除")
    @DatabaseIndex
    private Integer isDelete = YesOrNoEnum.NO.code();

    public Long getCreateTime() {
        return createTime;
    }

    public BaseParameter setCreateTime(Long createTime) {
        this.createTime = createTime;
        return this;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public BaseParameter setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public BaseParameter setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
        return this;
    }

    @Override
    public String toString() {
        return "BaseParameter["
                + " createTime=" + createTime + ","
                + " updateTime=" + updateTime + ","
                + " isDelete=" + isDelete
                + " ]"
                + " "
                + super.toString();
    }
}
