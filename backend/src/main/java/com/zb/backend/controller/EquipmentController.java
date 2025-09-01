package com.zb.backend.controller;

import com.zb.backend.constants.enums.EquipmentEnum;
import com.zb.backend.constants.enums.ResultEnum;
import com.zb.backend.entity.Equipment;
import com.zb.backend.model.Result;
import com.zb.backend.model.request.AddEquipmentRequest;
import com.zb.backend.service.EquipmentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/equip")
@RequiredArgsConstructor
public class EquipmentController {
    private final EquipmentService equipmentService;

    @Operation(summary = "获取设备集合")
    @PostMapping("/getSimpleEquipmentList")
    public Result<List<Equipment>> getSimpleEquipmentList() {
        List<Equipment> equipmentList = equipmentService.getSimpleEquipmentList();

        return Result.success(EquipmentEnum.SUC_GET_EQUIP, equipmentList);
    }

    // 新增设备
    @Operation(summary = "新增设备 Admin")
    @PostMapping("/addEquipment")
    public Result<Boolean> addEquipment(@RequestBody AddEquipmentRequest request) {
        ResultEnum resultEnum = equipmentService.addEquipment(request);
        if (!resultEnum.getCode().equals(2001)) return Result.error(resultEnum, false);
        return Result.success(resultEnum, true);
    }

    // 修改设备
    @Operation(summary = "修改设备 Admin")
    @PostMapping("/updateEquipment")
    public Result<Boolean> updateEquipment(@RequestBody Equipment equipment) {
        ResultEnum resultEnum = equipmentService.updateEquipment(equipment);
        if (!resultEnum.getCode().equals(2001)) return Result.error(resultEnum, false);
        return Result.success(resultEnum, true);
    }

    // 删除设备
    @Operation(summary = "删除设备 Admin")
    @PostMapping("/deleteEquipment")
    public Result<Boolean> deleteEquipment(@RequestBody Long equipmentId) {
        ResultEnum resultEnum = equipmentService.deleteEquipment(equipmentId);
        if (!resultEnum.getCode().equals(2001)) return Result.error(resultEnum, false);
        return Result.success(resultEnum, true);
    }
}
