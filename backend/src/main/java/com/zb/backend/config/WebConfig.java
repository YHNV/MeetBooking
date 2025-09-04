package com.zb.backend.config;

import com.zb.backend.config.interceptor.AdminInterceptor;
import com.zb.backend.config.interceptor.LoginInterceptor;
import com.zb.backend.service.TokenService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;
    private final AdminInterceptor adminInterceptor;
    private final CurrentAccountMethodArgumentResolver currentAccountMethodArgumentResolver;

    private final TokenService tokenService;

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
                        "/auth/register",
                        "/meetingRoom/uploadImage",
                        "/meetingRoom/updateMeetingRoom",
                        "/meetingRoom/addMeetingRoom",
                        "/equip/updateEquipment",
                        "/equip/deleteEquipment",
                        "/equip/deleteEquipment",
                        "/emp/updateEmployeeInfo",
                        "/emp/queryEmployees",
                        "/emp/getAllSimpleEmp",
                        "/ann/updateAnnouncement",
                        "/ann/deleteAnnouncement",
                        "/ann/addAnnouncement",
                        "/account/toggleAccountStatus",
                        "/account/resetPassword",
                        "/dept/getAllDeptList",
                        "/dept/updateDept",
                        "/dept/addDept"
                );

        WebMvcConfigurer.super.addInterceptors(registry);
    }

    // 通过Token获取accountId
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentAccountMethodArgumentResolver);
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
