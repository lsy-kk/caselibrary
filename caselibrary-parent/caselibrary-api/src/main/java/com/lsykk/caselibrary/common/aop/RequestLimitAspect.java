package com.lsykk.caselibrary.common.aop;

import com.lsykk.caselibrary.utils.HttpContextUtils;
import com.lsykk.caselibrary.utils.IpUtils;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.lang.reflect.Method;
import java.time.Duration;
import javax.servlet.http.HttpServletRequest;

public class RequestLimitAspect {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Pointcut("@annotation(com.lsykk.caselibrary.common.aop.RequestLimit)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint pjp) {
        try {
            // 获取请求头
            HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
            // 获取请求ip地址
            String ip = IpUtils.getIpAddr(request);
            // 获取调用的方法
            Method method = this.getMethod(pjp);
            // 获取注解
            RequestLimit annotation = method.getAnnotation(RequestLimit.class);
            // 获取注解上的参数
            long limitTime = annotation.limitTime();
            int times = annotation.times();
            // 拼接redisKey
            String redisKey = "RequestAspect_" + ip + "::" + method.getName();
            // 尝试根据redisKey获取访问记录
            String redisValue = redisTemplate.opsForValue().get(redisKey);
            if (StringUtils.isNotEmpty(redisValue)) {
                // 过期时间（还剩多久过期）
                Long expireTime = redisTemplate.getExpire(redisKey);
                if (expireTime == null){
                    expireTime = 0L;
                }
                int count = Integer.parseInt(redisValue);
                // 当前访问次数已满
                if (expireTime < limitTime && count == times) {
                    return ApiResult.fail(ErrorCode.REQUEST_LIMIT);
                }
                // 不刷新过期时间，只更改值
                redisTemplate.opsForValue().set(redisKey, String.valueOf(count + 1), 0);
            }
            else {
                // 新增记录
                redisTemplate.opsForValue().set(redisKey, "1", Duration.ofMillis(limitTime));
            }
            // 继续执行方法
            return pjp.proceed();
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return ApiResult.fail(ErrorCode.SYSTEM_ERROR);
    }

    /**
     * 获取调用的方法
     */
    private Method getMethod(ProceedingJoinPoint pjp) throws NoSuchMethodException {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        return pjp.getTarget().getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
    }
}
