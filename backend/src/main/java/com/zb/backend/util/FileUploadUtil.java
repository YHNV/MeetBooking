// File: backend/src/main/java/com/zb/backend/util/FileUploadUtil.java
package com.zb.backend.util;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class FileUploadUtil {

    // 配置上传路径 - 根据实际部署环境调整
    private static final String UPLOAD_DIR = "/Users/yhcm/Documents/Code/Project/Resources/MeetBooking/images/";

    /**
     * 上传图片文件
     * @param file 上传的文件对象
     * @return 相对URL路径，用于前端访问
     * @throws IOException 当文件操作失败时抛出
     */
    public static String uploadImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("上传文件为空");
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IOException("只允许上传图片文件");
        }

        // 生成唯一文件名，避免冲突
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        // 创建目标目录（如果不存在）
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 保存文件
        Path filePath = uploadPath.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), filePath);

        // 返回前端可访问的相对路径
        return "/resources/" + uniqueFileName;
    }

    /**
     * 删除图片文件
     * @param fileUrl 文件的URL路径
     * @return 是否成功删除
     */
    public static boolean deleteImage(String fileUrl) {
        try {
            // 从URL中提取文件名
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get(UPLOAD_DIR + fileName);

            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 获取文件大小限制（可根据需要调整）
     */
    public static long getMaxFileSize() {
        return 5 * 1024 * 1024; // 5MB
    }
}