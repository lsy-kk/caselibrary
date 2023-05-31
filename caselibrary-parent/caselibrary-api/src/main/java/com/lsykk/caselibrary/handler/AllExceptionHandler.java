package com.lsykk.caselibrary.handler;

import com.lsykk.caselibrary.vo.ApiResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

// 对加了@Controller注解的类，进行异常的拦截处理 AOP的实现
@ControllerAdvice
public class AllExceptionHandler {

    // 处理Exception.class
    @ExceptionHandler
    @ResponseBody
    public ApiResult doException(Exception exception){
        exception.printStackTrace();
        return ApiResult.fail("?", "系统异常");
    }

}
