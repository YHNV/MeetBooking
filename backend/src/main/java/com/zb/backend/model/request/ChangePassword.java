package com.zb.backend.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangePassword {
    // @NotNull(message = "accountId不能为空")
    // private Long accountId;
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
}
