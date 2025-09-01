package com.zb.backend.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddEquipmentRequest {
    @NotBlank(message = "设备名称不能为空")
    private String equipmentName;
    @NotBlank(message = "设备详情不能为空")
    private String description;
}
