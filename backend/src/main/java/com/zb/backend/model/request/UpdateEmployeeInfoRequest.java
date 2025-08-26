package com.zb.backend.model.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateEmployeeInfoRequest {
    @NotNull(message = "员工ID不能为空")
    private Long empId;
    @NotBlank(message = "员工姓名不能为空")
    @Size(max = 31, message = "员工姓名长度不能超过31个字符")
    private String empName;
    @NotNull(message = "部门ID不能为空")
    private Long deptId;
    // @ValidPosition(message = "职位不能包含'经理'或'管理'字样")
    @Size(max = 31, message = "职位长度不能超过31个字符")
    private String position;
    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确") // 简单校验中国手机号
    private String phone;
    @Email(message = "邮箱格式不正确")
    @Size(max = 63, message = "邮箱长度不能超过63个字符")
    private String email;
    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[0-9Xx]$",
            message = "身份证号格式不正确")
    private String idCard;
}
