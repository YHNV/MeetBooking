package com.zb.backend.config.interceptor;

import com.zb.backend.model.JwtClaim;
import com.zb.backend.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class AdminInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 在AdminInterceptor的preHandle中简化代码
        JwtClaim jwtClaim = (JwtClaim) request.getAttribute("TokenParsing");
        if (jwtClaim == null || !jwtClaim.getIsAdmin()) {
            sendErrorResponse(response, "没有管理员权限");
            return false;
        }

        // 5. 放行请求
        return true;
    }

    // 统一返回错误响应
    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        // 返回JSON格式的错误信息（与前端约定格式）
        response.getWriter().write("{\"code\":403,\"msg\":\"" + message + "\"}");
    }
}
