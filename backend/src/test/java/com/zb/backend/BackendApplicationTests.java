package com.zb.backend;

import com.zb.backend.model.JwtClaim;
import com.zb.backend.util.FileUploadUtil;
import com.zb.backend.util.JwtUtil;
import com.zb.backend.util.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    @Test
    // 测试上传图片
    void FileUploadUtil() throws IOException {
        // 您的文件路径
        String filePath = "/Users/yhcm/Desktop/QQ20241214-134600.png";

        // 读取文件内容到字节数组
        Path path = Paths.get(filePath);
        byte[] fileContent = Files.readAllBytes(path);

        // 创建MockMultipartFile
        MultipartFile file = new MockMultipartFile(
                "image",                    // 参数名
                "QQ20241214-134600.png",    // 原始文件名
                "image/png",                // 内容类型
                fileContent                 // 文件内容
        );

        // 设置测试用的临时目录（如果您重构了FileUploadUtil）
        // FileUploadUtil.setUploadDir("/tmp/test-uploads/");

        // 调用上传方法
        String resultUrl = FileUploadUtil.uploadImage(file);

        // 验证结果
        // assertNotNull(resultUrl);
        // assertTrue(resultUrl.endsWith(".png"));
        System.out.println("上传成功，文件URL: " + resultUrl);
    }


}
