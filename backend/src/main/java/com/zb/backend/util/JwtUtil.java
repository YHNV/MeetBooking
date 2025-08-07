package com.zb.backend.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
    // 请求头中存储令牌的键名（前端传递令牌时需使用此键）
    public static final String AUTH_HEADER_KEY = "Authorization";

    // 令牌前缀（前端传递时格式为："Bearer 令牌字符串"）
    public static final String TOKEN_PREFIX = "Bearer ";

    // 签名密钥（核心安全参数，需保密且定期更换，建议至少32位）
    // 生产环境应放在配置文件中，避免硬编码
    // public static final String KEY = "q3t6w9z$C&F)J@NcQfTjWnZr4u7x";
    public static final String KEY = "YHNV";

    // 令牌有效期（2小时，单位：毫秒）
    public static final Long EXPIRATION_TIME = 1000L * 60 * 60 * 2;

    /**
     * 生成JWT令牌
     * @param accountId 用户账号ID（作为令牌的核心标识）
     * @return 带前缀的完整令牌字符串（格式："Bearer xxxxxx"）
     */
    public static String createToken(Long accountId) {
        // 将accountId转为字符串存入令牌主题（subject）
        String content = accountId.toString();

        // 当前时间
        Date now = new Date();
        // 计算令牌过期时间（当前时间 + 有效期）
        Date expireDate = new Date(now.getTime() + EXPIRATION_TIME);

        // 构建JWT令牌：
        // 1. 设置主题（存储accountId）
        // 2. 设置过期时间
        // 3. 使用HMAC512算法和密钥签名（防止篡改）
        // 4. 拼接前缀后返回
        return TOKEN_PREFIX + JWT.create()
                .withSubject(content) //将账号存入主题
                .withIssuedAt(now) // 签发时间
                .withExpiresAt(expireDate) // 过期时间
                .sign(Algorithm.HMAC512(KEY)); // 签名密钥
    }

    /**
     * 验证令牌并返回accountId
     * @param token 待验证的令牌（可以是带前缀或不带前缀的格式）
     * @return 验证成功返回账号ID（Long类型）
     * @throws Exception 验证失败时抛出异常（包含具体原因）
     */
    public static Long verifyToken(String token) throws Exception {
        try {
            // 1. 去除令牌前缀（如果存在）
            String pureToken = token.replace(TOKEN_PREFIX, "");

            // 2. 验证令牌：
            // - 签名是否正确（使用相同密钥验证）
            // - 是否在有效期内
            String content = JWT.require(Algorithm.HMAC512(KEY))
                    .build()
                    .verify(pureToken)
                    .getSubject(); // 获取存储的accountId字符串

            // 3. 将字符串转回Long类型并返回
            return Long.parseLong(content);

        } catch (TokenExpiredException e) {
            // 令牌已过期（常见错误，需提示用户重新登录）
            throw new Exception("令牌已失效，请重新登录", e);
        } catch (JWTVerificationException e) {
            // 其他验证失败（如签名错误、令牌被篡改、格式错误等）
            throw new Exception("令牌验证失败，可能已被篡改", e);
        } catch (NumberFormatException e) {
            // accountId转换失败（通常是令牌被恶意修改）
            throw new Exception("令牌内容异常", e);
        }
    }
}
