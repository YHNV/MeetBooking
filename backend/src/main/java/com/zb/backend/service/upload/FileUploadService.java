package com.zb.backend.service.upload;

import com.zb.backend.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FileUploadService {
    // 定义4MB的大小（字节）
    private static final long MAX_FILE_SIZE = 4 * 1024 * 1024; // 4MB

    public String imageUpload(MultipartFile imageFile) throws IOException {
        // 检查文件是否为空
        if (imageFile.isEmpty()) {
            throw new IOException("上传文件不能为空");
        }

        // 检查文件大小是否超过4MB
        if (imageFile.getSize() > MAX_FILE_SIZE) {
            throw new IOException("文件大小超过4MB限制");
        }

        // 调用工具类上传文件
        String imageUrl = FileUploadUtil.uploadImage(imageFile);
        return imageUrl;
    }
}
