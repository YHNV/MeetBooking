package com.zb.backend.config.interceptor;

import com.zb.backend.entity.Token;
import com.zb.backend.mapper.TokenMapper;
import com.zb.backend.model.JwtClaim;
import com.zb.backend.service.TokenService;
import com.zb.backend.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;

    public LoginInterceptor(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 不验证OPTIONS请求，直接放行，带有Authorization
        // 关键：放行OPTIONS预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true; // 直接通过，不验证Token
        }

        // 1. 从请求头获取令牌
        String token = request.getHeader(JwtUtil.AUTH_HEADER_KEY);

        // 2. 验证令牌是否存在
        if (token == null || token.trim().isEmpty()) {
            sendErrorResponse(response, "令牌失效，请重新登录");
            return false;
        }

        // 3. 验证令牌并提取对象
        JwtClaim jwtClaim;
        try {
            // 调用工具类验证令牌，成功则返回对象
            jwtClaim = JwtUtil.verifyToken(token);
        } catch (Exception e) {
            // 验证失败（过期、篡改等）
            sendErrorResponse(response, e.getMessage());
            return false;
        }

        // 判断数据库中，是否存在这个Token
        Token tokenByAccountId = tokenService.selectTokenByAccountId(jwtClaim.getAccountId());
        System.out.println(this.getClass().getSimpleName() + " 获取Token：" + tokenByAccountId);
        if (tokenByAccountId == null) {
            sendErrorResponse(response, "令牌失效，请重新登录");
            return false;
        }

        // 4. 令牌有效，将accountId存入请求属性（供后续接口使用）
        request.setAttribute("TokenParsing", jwtClaim);

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

    // public void setTokenService(TokenService tokenService) {
    //     this.tokenService = tokenService;
    // }
}
