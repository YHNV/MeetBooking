package com.zb.backend.config;

import com.zb.backend.config.interceptor.AdminInterceptor;
import com.zb.backend.config.interceptor.LoginInterceptor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;
    private final AdminInterceptor adminInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 通用拦截器
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/swagger-ui/**",
                        "/v3/**",
                        "/auth/login",
                        "/h2-console/**"
                );

        // 管理员拦截器
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns(
                        "/auth/register"
                );

        WebMvcConfigurer.super.addInterceptors(registry);
    }

    // 配置跨域请求
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")
                // .allowedOrigins("*")
                .allowedOriginPatterns("*")
                // .allowedOriginPatterns("http://localhost:5173")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
