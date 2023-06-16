package online.yangcloud.model.ao;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zhuboyang
 * @since 2023年03月16 09:28:21
 */
public class BatchOperator {

    /**
     * 待删除数据 id 列表
     */
    @NotNull
    private List<String> idsList;

    public List<String> getIdsList() {
        return idsList;
    }

    public void setIdsList(List<String> idsList) {
        this.idsList = idsList;
    }

    @Override
    public String toString() {
        return "BatchRemove["
                + " idsList=" + idsList
                + " ]";
    }
}
