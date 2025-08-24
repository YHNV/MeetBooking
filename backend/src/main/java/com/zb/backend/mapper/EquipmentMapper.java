package com.zb.backend.mapper;

import com.zb.backend.entity.Equipment;

import java.util.List;

public interface EquipmentMapper {
    // 根据id集合查询存在的id有哪些
    List<Long> selectExistsByIds(List<Long> equipmentIds);

    // 获取简单设备集合
    List<Equipment> selectSimpleEquipmentList();
}
