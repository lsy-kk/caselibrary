package com.lsykk.caselibrary.config;


import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMVCConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //跨域配置，便于前后端访问；本地测试 端口不一致 也算跨域
        // 配置SpringSecurity之后失效了
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://kkysl.top",
                        "https://kkysl.top",
                        "http://47.102.116.87",
                        "http://localhost:5173",
                        "http://172.18.0.6")
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }

    /**
     * fastjson的全局序列化方式
     * @param converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 1、需要先定义一个·convert转换消息的对象；
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        // 2、添加fastjson的配置信息，比如 是否要格式化返回json数据
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
        // 3、在convert中添加配置信息.
        fastConverter.setFastJsonConfig(fastJsonConfig);
        // 4、将convert添加到converters当中.
        converters.add(fastConverter);
    }
}
