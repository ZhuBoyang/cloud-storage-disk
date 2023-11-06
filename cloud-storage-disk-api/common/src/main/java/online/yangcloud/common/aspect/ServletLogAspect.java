package online.yangcloud.common.aspect;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import online.yangcloud.common.annotation.SessionValid;
import online.yangcloud.common.common.AppConstants;
import online.yangcloud.common.tools.ExceptionTools;
import online.yangcloud.common.tools.RedisTools;
import online.yangcloud.common.tools.SystemTools;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * @author zhuboyang
 * @since 2022/12/31 11:40
 */
@Aspect
@Component
public class ServletLogAspect {

    public static final Logger logger = LoggerFactory.getLogger(ServletLogAspect.class);

    /**
     * 记录用户信息的类的全类名
     */
    public static String TOKEN_CLASSPATH = "online.yangcloud.common.model.User";

    @Resource
    private RedisTools redisTools;

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
    @Around("@annotation(online.yangcloud.common.annotation.TimeConsuming)")
    public Object recordTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {
        Class<?> clazz = joinPoint.getTarget().getClass();
        String methodName = joinPoint.getSignature().getName();

        // 获取执行的类名和方法名
        logger.info("====== 开始执行 {}.{} ======", clazz, methodName);

        // 记录开始执行方法的时间
        long begin = System.currentTimeMillis();

        // 执行目标 service 方法
        Object result = joinPoint.proceed();

        // 记录结束时间
        long end = System.currentTimeMillis();
        long takeTime = end - begin;

        if (takeTime >= 0 && takeTime < AppConstants.Consuming.NORMAL_TIMING) {
            logger.info("====== {}.{} 执行结束，耗时：{} 毫秒 =======", clazz, methodName, takeTime);
        } else if (takeTime >= AppConstants.Consuming.NORMAL_TIMING && takeTime < AppConstants.Consuming.WARN_TIMING) {
            logger.warn("====== {}.{} 执行结束，耗时：{} 毫秒 =======", clazz, methodName, takeTime);
        } else {
            logger.error("====== {}.{} 执行结束，耗时：{} 毫秒 =======", clazz, methodName, takeTime);
        }

        return result;
    }

    /**
     * 回写用户信息
     *
     * @param joinPoint 断点
     * @return 请求参数
     * @throws Throwable Throwable
     */
    @Around("@annotation(online.yangcloud.common.annotation.SessionValid)")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // 检测接口是否需要校验登录状态，并对会话进行续期
        validSessionToken(isNeedValid(joinPoint));

        // 根据类名获取类型
        if (StrUtil.isBlank(TOKEN_CLASSPATH)) {
            Class<?> targetClass = joinPoint.getTarget().getClass();
            Class<?>[] parameterTypes = ((MethodSignature) joinPoint.getSignature()).getMethod().getParameterTypes();
            Method targetMethod = targetClass.getMethod(joinPoint.getSignature().getName(), parameterTypes);
            SessionValid annotation = targetMethod.getAnnotation(SessionValid.class);
            if (StrUtil.isBlank(annotation.errorMsg())) {
                ExceptionTools.businessLogger();
            }
            ExceptionTools.businessLogger(annotation.errorMsg());
        }
        Class<?> clazz = Class.forName(TOKEN_CLASSPATH);

        // 从 redis 中获取用户登录状态，并回写到请求参数中
        Object[] args = joinPoint.getArgs();
        // 设置对象实体
        Object obj = null;
        int index = -1;
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].getClass().equals(clazz)) {
                    String sessionId = SystemTools.getHeaders().getAuthorization();
                    if (StrUtil.isBlank(sessionId)) {
                        ExceptionTools.paramLogger();
                    } else {
                        String tokenInfo = redisTools.get(AppConstants.Account.LOGIN_TOKEN + sessionId);
                        if (StrUtil.isBlank(tokenInfo)) {
                            ExceptionTools.businessLogger();
                        }
                        obj = JSONUtil.toBean(tokenInfo, clazz);
                    }
                    index = i;
                    break;
                }
            }
        }
        if (index != -1) {
            args[index] = obj;
        }

        // 根据注解中设置的类名，获取到具体的类
        return joinPoint.proceed(args);
    }

    /**
     * 阻止重复请求
     *
     * @param joinPoint 断点
     */
    @Around("@annotation(online.yangcloud.common.annotation.RepeatRequest)")
    public Object preventRepeatRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        String key = "repeat_request";
        Boolean flag = redisTools.setIfAbsent(key, StrUtil.EMPTY);
        if (!flag) {
            ExceptionTools.businessLogger("操作太快了，系统反应不过来了");
        }
        return joinPoint.proceed();
    }

    /**
     * 对请求进行拦截，并检测 token 是否已过期。已过期则不予通行；未过期则对 token 时效进行续期，并予以通过
     *
     * @param isNeedValid 是否需要检测
     */
    private void validSessionToken(Boolean isNeedValid) {
        if (!isNeedValid) {
            return;
        }
        String sessionId = SystemTools.getHeaders().getAuthorization();
        if (StrUtil.isNotBlank(sessionId)) {
            logger.info("session id [{}]", sessionId);
            String userInfoJson = redisTools.get(AppConstants.Account.LOGIN_TOKEN + sessionId);
            if (StrUtil.isNotBlank(userInfoJson)) {
                long expireTime = redisTools.getExpireTime(AppConstants.Account.LOGIN_TOKEN + sessionId);
                // 当 session 还剩 5 分钟过期的时候，再次请求就会对 session 进行续期
                if (expireTime < AppConstants.Account.LOGIN_SESSION_EXPIRE_TIME
                        && expireTime != AppConstants.Account.ACCOUNT_EXPIRED_STATUS
                        && expireTime != AppConstants.Account.ACCOUNT_NOT_EXIST_STATUS) {
                    logger.info("The login status of account [{}] is about to expire. Now it starts to renew. The renewal period is [{}]s",
                            userInfoJson, AppConstants.Account.LOGIN_SESSION_EXPIRE_TIME);
                    redisTools.expire(AppConstants.Account.LOGIN_TOKEN + sessionId, userInfoJson, AppConstants.Account.LOGIN_SESSION_EXPIRE_TIME);
                }
            } else {
                ExceptionTools.noAuthExp("Login has expired, please go to login");
            }
        } else {
            ExceptionTools.noAuthExp("Login has expired, please go to login");
        }
    }

    /**
     * 校验当前请求的接口是需要验证用户登录状态
     *
     * @param joinPoint 断点
     * @return 是否需要校验
     * @author zhuby
     * @since 2021/4/16 08:25
     */
    private boolean isNeedValid(JoinPoint joinPoint) throws NoSuchMethodException {
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

}
