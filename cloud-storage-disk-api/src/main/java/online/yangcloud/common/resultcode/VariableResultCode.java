package online.yangcloud.common.resultcode;

/**
 * @author zhuboyang
 * @since 2022年04月11 22:42:16
 */
public interface VariableResultCode {

    interface VariableMsg {
        /**
         * 变量保存失败
         */
        String VARIABLE_SAVE_SUCCESS = "网站内容保存成功";
        /**
         * 变量保存成功
         */
        String VARIABLE_SAVE_ERROR = "网站内容保存失败";
        /**
         * 变量修改失败
         */
        String VARIABLE_UPDATE_SUCCESS = "网站内容修改成功";
        /**
         * 变量修改成功
         */
        String VARIABLE_UPDATE_ERROR = "网站内容修改失败";
        /**
         * 还有未设置的内容，无法发布
         */
        String CAN_NOT_RELEASE_WITH_UNSETTING = "还有未设置的内容，无法发布";
        /**
         * 版本发布失败
         */
        String VERSION_RELEASE_ERROR = "版本发布失败";
        /**
         * 版本发布成功
         */
        String VERSION_RELEASE_SUCCESS = "版本发布成功";
    }

}
