package online.yangcloud.common.exception;

import online.yangcloud.common.common.AppResultCode;
import online.yangcloud.common.common.ResultData;
import online.yangcloud.common.common.UserResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理
 *
 * @author zhuboyang
 * @since 2023/8/14 22:13
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public ResultData errorHandler(Exception e) {
        logger.error(e.getMessage(), e);
        if (e instanceof BusinessException) {
            BusinessException be = (BusinessException) e;
            return ResultData.build(AppResultCode.FAILURE.clone(be.getMessage()));
        }
        if (e instanceof ParamErrorException) {
            ParamErrorException pee = (ParamErrorException) e;
            return ResultData.build(AppResultCode.INVALID_PARAM_VALUE.clone(pee.getMessage()));
        }
        if (e instanceof NoAuthException) {
            return ResultData.build(UserResultCode.NO_AUTH_SESSION);
        }
        return ResultData.errorMessage();
    }

}
