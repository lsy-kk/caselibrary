package com.lsykk.caselibrary.common.aop;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestLimit {
    // 限制时间
    long limitTime() default 1000;
    // 在限制时间内允许的最大访问次数
    int times() default 2;
}
