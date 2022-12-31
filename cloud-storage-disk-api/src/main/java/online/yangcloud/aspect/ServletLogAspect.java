package online.yangcloud.aspect;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import online.yangcloud.annotation.SessionValid;
import online.yangcloud.common.constants.AppConstants;
import online.yangcloud.common.constants.UserConstants;
import online.yangcloud.exception.NoAuthException;
import online.yangcloud.model.po.User;
import online.yangcloud.utils.RedisUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author zhuboyang
 * @since 2022/12/30 17:13
 */
@Aspect
@Component
public class ServletLogAspect {

    public static final Logger logger = LoggerFactory.getLogger(ServletLogAspect.class);

    @Autowired
    private RedisUtil redisUtil;

    /**
     * AOP 通知：
     * 1. 前置通知：在方法调用之前执行
     * 2. 后置通知：在方法正常调用之后执行
     * 3. 环绕通知：在方法调用之前和之后，都分别可以执行的通知
     * 4. 异常通知：如果在方法调用过程中发生异常，则通知
     * 5. 最终通知：在方法调用之后执行
     * 切面表达式：
     * execution 代表所要执行的表达式主体
     * 第一处 * 代表方法返回类型 * 代表所有类型
     * 第二处 包名代表 aop 监控的类所在的包
     * 第三处 .. 代表该包以及其子包下的所有类方法
     * 第四处 * 代表类名，* 代表所有类
     * 第五处 *(..) * 代表类中的方法名 ( 所有方法名 )，() 表示方法的参数，.. 表示可以接受任何参数
     */
    @Around("execution(* online.yangcloud.service.impl..*.*(..))")
    public Object recordTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {

        // 获取执行的类名和方法名
        logger.info("====== execute start {}.{} ======", joinPoint.getTarget().getClass(), joinPoint.getSignature().getName());

        // 记录开始执行方法的时间
        long begin = System.currentTimeMillis();

        // 执行目标 service 方法
        Object result = joinPoint.proceed();

        // 记录结束时间
        long end = System.currentTimeMillis();
        long takeTime = end - begin;

        if (takeTime >= 0 && takeTime < AppConstants.NORMAL_TIMING) {
            logger.info("====== execute end, consuming time: {} ms =======", takeTime);
        } else if (takeTime >= AppConstants.NORMAL_TIMING && takeTime < AppConstants.WARN_TIMING) {
            logger.warn("====== execute end, consuming time: {} ms =======", takeTime);
        } else {
            logger.error("====== execute end, consuming time: {} ms =======", takeTime);
        }

        return result;
    }

    /**
     * 对请求进行拦截，判断接口是否需要登录校验
     *
     * @param joinPoint 断点
     * @throws NoSuchMethodException .
     */
    @Before("@annotation(online.yangcloud.annotation.SessionValid)")
    public void aspectControllerPermission(JoinPoint joinPoint) throws NoSuchMethodException {
        logger.info("==================== execute start {}.{} ====================", joinPoint.getTarget().getClass(), joinPoint.getSignature().getName());
        ServletRequestAttributes servletRequestAttributes
                = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(servletRequestAttributes).getRequest();
        logger.info("current request info : {}?{}", request.getRequestURL(), request.getQueryString());
        if (needMethodValid(joinPoint)) {
            String sessionId = request.getParameter(UserConstants.AUTHORIZATION);
            if (CharSequenceUtil.isNotBlank(sessionId)) {
                logger.info("sessionId [{}]", sessionId);
                String userInfoJson = redisUtil.get(UserConstants.LOGIN_SESSION + sessionId);
                if (CharSequenceUtil.isNotBlank(userInfoJson)) {
                    long expireTime = redisUtil.getExpireTime(UserConstants.LOGIN_SESSION + sessionId);
                    // 当session还剩5分钟过期的时候，再次请求就会对session进行续期
                    if (expireTime < AppConstants.ACCOUNT_RENEWAL_TIME
                            && expireTime != AppConstants.ACCOUNT_EXPIRED_STATUS
                            && expireTime != AppConstants.ACCOUNT_NOT_EXIST_STATUS) {
                        logger.info("The login status of account [{}] is about to expire. The login status is to be renewed for [{}]s",
                                userInfoJson, UserConstants.LOGIN_SESSION_EXPIRE_TIME);
                        redisUtil.expire(UserConstants.LOGIN_SESSION + sessionId, userInfoJson, UserConstants.LOGIN_SESSION_EXPIRE_TIME);
                    }
                } else {
                    logger.info("==================== Method execution is abnormal ====================");
                    logger.info("=============== Login has expired, please go to login ================");
                    throw new NoAuthException("登录已过期，请前往登录");
                }
            } else {
                logger.info("==================== Method execution is abnormal ====================");
                logger.info("=============== Login has expired, please go to login ================");
                throw new NoAuthException("登录已过期，请前往登录");
            }
        }
        logger.info("==================== Method execution end ====================");
    }

    /**
     * 校验当前请求的接口是需要验证用户登录状态
     *
     * @param joinPoint 断点
     * @return 是否需要校验
     * @author zhuby
     * @since 2021/4/16 08:25
     */
    private boolean needMethodValid(JoinPoint joinPoint) throws NoSuchMethodException {
        try {
            Class<?> targetClass = joinPoint.getTarget().getClass();
            Class<?>[] parameterTypes = ((MethodSignature) joinPoint.getSignature()).getMethod().getParameterTypes();
            Method targetMethod = targetClass.getMethod(joinPoint.getSignature().getName(), parameterTypes);
            if (targetMethod.isAnnotationPresent(SessionValid.class)) {
                SessionValid annotation = targetMethod.getAnnotation(SessionValid.class);
                return annotation.needValid();
            }
        } catch (NoSuchMethodException e) {
            logger.error("no such method error [{}]", e.getMessage(), e);
            throw new NoSuchMethodException();
        }
        return false;
    }

    /**
     * 拦截所有请求，如果请求参数中有"User"，那么将从redis中获取用户信息并回写到请求参数中
     *
     * @param joinPoint 断点
     * @return .
     * @throws Throwable .
     */
    @Around(value = "@annotation(online.yangcloud.annotation.SessionValid)")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes servletRequestAttributes
                = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(servletRequestAttributes).getRequest();
        Object[] args = joinPoint.getArgs();
        User transUser = new User();
        int index = -1;
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof User) {
                    String sessionId = request.getParameter(UserConstants.AUTHORIZATION);
                    if (StrUtil.isBlank(sessionId)) {
                        transUser = new User();
                    } else {
                        String userInfoJson = redisUtil.get(UserConstants.LOGIN_SESSION + sessionId);
                        if (CharSequenceUtil.isBlank(userInfoJson)) {
                            transUser = new User();
                        } else {
                            transUser = JSONUtil.toBean(userInfoJson, User.class);
                        }
                    }
                    index = i;
                }
            }
        }
        if (index != -1) {
            args[index] = transUser;
        }
        return joinPoint.proceed(args);
    }

}
