package online.yangcloud.common.tools;

import online.yangcloud.common.common.AppResultCode;
import online.yangcloud.common.exception.BusinessException;
import online.yangcloud.common.exception.DuplicateSubmissionException;
import online.yangcloud.common.exception.NoAuthException;
import online.yangcloud.common.exception.ParamErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhuboyang
 * @since 2023年03月24 11:27:01
 */
public class ExceptionTools {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionTools.class);

    /**
     * 打印业务异常
     *
     * @param errorMsg 异常信息
     */
    public static void businessLogger(String errorMsg) {
        logger.error(errorMsg);
        throw new BusinessException(errorMsg);
    }

    /**
     * 打印业务异常
     */
    public static void businessLogger() {
        logger.error(AppResultCode.FAILURE.getMessage());
        throw new BusinessException(AppResultCode.FAILURE.getMessage());
    }

    /**
     * 打印参数异常
     */
    public static void paramLogger() {
        throw new ParamErrorException();
    }

    /**
     * 打印无数据异常
     */
    public static void noDataLogger() {
        throw new BusinessException(AppResultCode.DATA_NOT_FOUND.getMessage());
    }

    /**
     * 防刷异常
     */
    public static void duplicateMissionExp() {
        throw new DuplicateSubmissionException();
    }

    /**
     * 无权限
     */
    public static void noAuthExp(String exp) {
        logger.error(exp);
        throw new NoAuthException(exp);
    }

}
