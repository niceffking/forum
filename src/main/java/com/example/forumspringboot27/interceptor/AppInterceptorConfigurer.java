package com.example.forumspringboot27.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class AppInterceptorConfigurer implements WebMvcConfigurer {
    @Resource
    private LoginInterceptor loginInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注入自定义的登录拦截器， 然后定义拦截接口
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**")
                .excludePathPatterns("/sign-up.html")
                .excludePathPatterns("/sign-in.html")
                .excludePathPatterns("/user/login")
                .excludePathPatterns("/user/logout")
                .excludePathPatterns("/user/register")
                .excludePathPatterns("/swagger*/**")
                .excludePathPatterns("/v3*/**")
                .excludePathPatterns("/dist/**")
                .excludePathPatterns("/image/**")
                .excludePathPatterns("/**.ico")
                .excludePathPatterns("/js/**");
    }
}
