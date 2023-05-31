package com.lsykk.caselibrary.common.aop;

import com.alibaba.fastjson.JSON;
import com.lsykk.caselibrary.utils.HttpContextUtils;
import com.lsykk.caselibrary.utils.IpUtils;
import com.lsykk.caselibrary.vo.UserDetail;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Component
@Aspect
//切面 定义了通知和切点的关系
@Slf4j
public class LogAspect{
    // 定义切点（切点这里就是注释加在哪里）
    @Pointcut("@annotation(com.lsykk.caselibrary.common.aop.LogAnnotation)")
    public void pointCut(){}

    // 环绕通知，即在方法前后都可以输出日志
    @Around("pointCut()")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        long beginTime = System.currentTimeMillis();
        // 执行方法
        Object result = joinPoint.proceed();
        // 执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;
        // 保存日志
        recordLog(joinPoint, time);
        return result;
    }

    private void recordLog(ProceedingJoinPoint joinPoint, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogAnnotation logAnnotation = method.getAnnotation(LogAnnotation.class);
        log.info("===================== log start ================================");
        log.info("module: {}", logAnnotation.module());
        log.info("operation: {}", logAnnotation.operator());
        // 请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        log.info("request method: {}",className + "." + methodName + "()");
        // 请求的参数
        Object[] args = joinPoint.getArgs();
        String params = "";
        // 没有入参
        if (args.length == 0){
            params = "(none)";
        }
        // 文件没法序列化，特判一下
        else if (args[0] instanceof MultipartFile){
            MultipartFile file = (MultipartFile)args[0];
            params = "MultipartFile_" + file.getOriginalFilename();
        }
        else {
            params = JSON.toJSONString(args[0]);
        }
        log.info("params: {}",params);
        // 获取request 设置IP地址
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        log.info("ip: {}", IpUtils.getIpAddr(request));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        // 操作用户信息
        if (principal instanceof UserDetail){
            UserDetail userDetail = (UserDetail)principal;
            log.info("operator: userId: {}", userDetail.getId());
        }
        else {
            log.info("operator: anonymousUser");
        }
        // 执行时间
        log.info("execute time : {} ms",time);
        log.info("===================== log end ================================");
    }
}
