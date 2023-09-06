package online.yangcloud.common.common;

import cn.hutool.core.util.StrUtil;

/**
 * 应用变量，可修改
 *
 * @author zhuboyang
 * @since 2023年08月15 16:46:31
 */
public class AppProperties {

    /**
     * 用户账户是否完成初始化
     */
    public static Boolean ACCOUNT_HAS_INITIAL = Boolean.FALSE;

    /**
     * 用户账户绑定的根目录文件 id
     */
    public static String ROOT_DIR_ID = StrUtil.EMPTY;

}
