package com.lsykk.caselibrary.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMVCConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //跨域配置，便于前后端访问；本地测试 端口不一致 也算跨域
        //允许8080端口访问后端
        registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET","POST","PUT");
    }
}
