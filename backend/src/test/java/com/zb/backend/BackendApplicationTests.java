package com.zb.backend;

import com.zb.backend.model.JwtClaim;
import com.zb.backend.util.JwtUtil;
import com.zb.backend.util.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BackendApplicationTests {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    void contextLoads() {
    }

    @Test
    // 测试密码加密
    void TestBCryptPasswordEncoder() {
        String pwd = "123456";
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode(pwd);
        System.out.println("加密前的密码：" + pwd);
        System.out.println("加密后的密码：" + encode);
    }

    @Test
    // 测试Jwt
    void JwtUtil() {
        JwtClaim jwtClaim = new JwtClaim(10001L, false);
        String token = JwtUtil.createToken(jwtClaim);
        System.out.println("Token：" + token);

        try {
            JwtClaim jwtClaim1 = JwtUtil.verifyToken(token);
            System.out.println("验证Token：" + jwtClaim1.toString());
        } catch (Exception e) {
            System.out.println("错误：" + e.getMessage());
            throw new RuntimeException(e);
        }
    }



}
