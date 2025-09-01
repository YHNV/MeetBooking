package com.zb.backend.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddAnnouncementRequest {
    private Long senderId;
    @NotBlank(message = "公告标题不能为空")
    private String title;
    @NotBlank(message = "公告内容不能为空")
    private String content;
}
