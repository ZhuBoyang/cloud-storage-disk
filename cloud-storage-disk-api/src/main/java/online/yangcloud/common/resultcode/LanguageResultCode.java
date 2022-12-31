package online.yangcloud.common.resultcode;

/**
 * @author zhuboyang
 * @since 2022年04月03 13:45:29
 */
public interface LanguageResultCode {

    interface LanguageMsg {
        /**
         * 多语言已存在，请勿重复添加
         */
        String LANGUAGE_IS_EXIST = "多语言已存在，请勿重复添加";
        /**
         * 多语言不存在
         */
        String LANGUAGE_IS_NOT_EXIST = "此语言不存在";
        /**
         * 多语言添加失败，请重试
         */
        String ADD_LANGUAGE_ERROR = "多语言添加失败，请重试";
        /**
         * 多语言添加成功
         */
        String ADD_LANGUAGE_SUCCESS = "多语言添加成功";
        /**
         * 多语言名称修改失败，请重试
         */
        String LANGUAGE_NAME_UPDATE_ERROR = "多语言名称修改失败，请重试";
        /**
         * 多语言名称修改成功
         */
        String LANGUAGE_NAME_UPDATE_SUCCESS = "多语言名称修改成功";
        /**
         * 多语言删除失败，请重试
         */
        String DELETE_ERROR = "多语言删除失败，请重试";
        /**
         * 多语言删除成功
         */
        String DELETE_SUCCESS = "多语言删除成功";
        /**
         * 此语言下有已发布的网站内容，无法删除
         */
        String CAN_NOT_DELETE_WITH_RELEASE_CONFIG = "此语言下有已发布的网站内容，无法删除";
        /**
         * 还没有已发布的网站内容，请先发布网站内容
         */
        String PLEASE_RELEASE_CONFIG_INFO = "还没有已发布的网站内容，请先发布网站内容";
        /**
         * 多语言激活失败，请重试
         */
        String ACTIVITY_ERROR = "多语言激活失败，请重试";
        /**
         * 多语言激活成功，请重试
         */
        String ACTIVITY_SUCCESS = "多语言激活成功";
        /**
         * 多语言禁用失败，请重试
         */
        String DISABLE_ERROR = "多语言禁用失败，请重试";
        /**
         * 多语言禁用成功
         */
        String DISABLE_SUCCESS = "多语言禁用成功";
        /**
         * 无法禁用最后一个已激活的语言
         */
        String CANNOT_DISABLE_LAST_ACTIVATE_LANGUAGE = "无法禁用最后一个已激活的语言";
    }

}
