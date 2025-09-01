package com.zb.backend.mapper;

import com.zb.backend.entity.Equipment;
import com.zb.backend.model.request.AddEquipmentRequest;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public interface EquipmentMapper {
    // 根据id集合查询存在的id有哪些
    List<Long> selectExistsByIds(List<Long> equipmentIds);

    // 获取简单设备集合
    List<Equipment> selectSimpleEquipmentList();

    // 根据设备id集合，返回不存在的设备id
    List<Long> selectNonExistsEquipmentByEquipmentIds(List<Long> equipmentIdList);

    // 通过设备名查询设备是否存在
    Equipment selectEquipByName(@NotBlank(message = "设备名称不能为空") String equipmentName);

    // 新增设备
    Boolean insertEquip(AddEquipmentRequest request);

    // 根据id查询设备是否存在
    Equipment selectEquipById(Long equipmentId);

    // 修改设备
    Boolean updateEquip(Equipment equipment);

    // 删除设备
    Boolean deleteById(Long equipmentId);
}
