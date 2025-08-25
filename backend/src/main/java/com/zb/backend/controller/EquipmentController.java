package com.zb.backend.controller;

import com.zb.backend.constants.enums.EquipmentEnum;
import com.zb.backend.entity.Equipment;
import com.zb.backend.model.Result;
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

    @Operation(summary = "获取简单设备集合")
    @PostMapping("/getSimpleEquipmentList")
    public Result<List<Equipment>> getSimpleEquipmentList() {
        List<Equipment> equipmentList = equipmentService.getSimpleEquipmentList();

        return Result.success(EquipmentEnum.SUC_GET_EQUIP, equipmentList);
    }
}
