package com.zb.backend.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DeptRequest {
    // 仅在更新时传值
    private Long deptId;

    @NotBlank(message = "部门名称不能为空")
    @Size(max = 63, message = "部门名称长度不能超过63个字符")
    @Pattern(regexp = "^.+部$", message = "部门名称必须以'部'结尾，且'部'前至少有一个字符")
    private String deptName;

    private Long managerId;

    @NotBlank(message = "部门地址不能为空")
    @Size(max = 255, message = "部门地址长度不能超过255个字符")
    private String deptAddress;

    @NotBlank(message = "部门详情不能为空")
    private String deptDesc;
}
