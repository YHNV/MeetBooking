package com.zb.backend.config.interceptor;

import com.zb.backend.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 从请求头获取令牌
        String token = request.getHeader(JwtUtil.AUTH_HEADER_KEY);

        // 2. 验证令牌是否存在
        if (token == null || token.trim().isEmpty()) {
            sendErrorResponse(response, "未提供令牌，请先登录");
            return false;
        }

        // 3. 验证令牌并提取accountId
        Long accountId;
        try {
            // 调用工具类验证令牌，成功则返回accountId
            accountId = JwtUtil.verifyToken(token);
        } catch (Exception e) {
            // 验证失败（过期、篡改等）
            sendErrorResponse(response, e.getMessage());
            return false;
        }

        // 4. 令牌有效，将accountId存入请求属性（供后续接口使用）
        request.setAttribute("accountId", accountId);

        // 5. 放行请求
        return true;
    }

    // 统一返回错误响应
    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        // 返回JSON格式的错误信息（与前端约定格式）
        response.getWriter().write("{\"code\":401,\"msg\":\"" + message + "\"}");
    }
}
