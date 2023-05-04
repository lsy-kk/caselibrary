package com.lsykk.caselibrary.common.cache;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Duration;

@Aspect
@Component
@Slf4j
public class CacheAspect {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Pointcut("@annotation(com.lsykk.caselibrary.common.cache.Cache)")
    public void pointCut(){}

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint pjp){
        try {
            Signature signature = pjp.getSignature();
            // className 类名
            String className = pjp.getTarget().getClass().getSimpleName();
            // methodName 调用的方法名
            String methodName = signature.getName();
            // parameterTypes 参数类型
            Class[] parameterTypes = new Class[pjp.getArgs().length];
            Object[] args = pjp.getArgs();
            // params 将所有参数拼接得到的字符串
            StringBuilder params = new StringBuilder();
            for (int i=0; i<args.length; i++) {
                if (args[i] != null) {
                    params.append(JSON.toJSONString(args[i]));
                    parameterTypes[i] = args[i].getClass();
                }
                else {
                    parameterTypes[i] = null;
                }
            }
            if (StringUtils.isNotEmpty(params.toString())) {
                //加密 以防出现key过长以及字符转义获取不到的情况
                params = new StringBuilder(DigestUtils.md5Hex(params.toString()));
            }
            // 根据方法名和参数类型获取signature
            Method method = pjp.getSignature().getDeclaringType().getMethod(methodName, parameterTypes);
            // 获取Cache注解
            Cache annotation = method.getAnnotation(Cache.class);
            // 缓存过期时间
            long expire = annotation.expire();
            // 缓存名称
            String name = annotation.name();
            // 拼接redisKey
            String redisKey = name + "::" + className + "::" +methodName + "::" + params;
            // 尝试根据redisKey获取方法执行结果
            String redisValue = redisTemplate.opsForValue().get(redisKey);
            // 若找到缓存，直接返回
            if (StringUtils.isNotEmpty(redisValue)){
                log.info("get method result from redis, className = {}, methodName = {}", className, methodName);
                ApiResult result = JSON.parseObject(redisValue, ApiResult.class);
                return result;
            }
            // 否则执行方法，然后缓存结果
            Object proceed = pjp.proceed();
            redisTemplate.opsForValue().set(redisKey,JSON.toJSONString(proceed), Duration.ofMillis(expire));
            log.info("put method result to redis, className = {}, methodName = {}", className, methodName);
            return proceed;
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return ApiResult.fail(ErrorCode.SYSTEM_ERROR);
    }
}
