package com.zb.backend.service;

import com.zb.backend.entity.Equipment;
import com.zb.backend.mapper.EquipmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentService {
    private final EquipmentMapper equipmentMapper;

    // 根据设备id集合查询存在的id有哪些
    public List<Long> getExistsByIds(List<Long> equipmentIds) {
        return equipmentMapper.selectExistsByIds(equipmentIds);
    }

    // 获取简单设备集合
    public List<Equipment> getSimpleEquipmentList() {
        return equipmentMapper.selectSimpleEquipmentList();
    }

    // 根据设备id集合，返回不存在的设备id
    public List<Long> getNonExistsEquipmentByEquipmentIds(List<Long> equipmentIdList) {
        if (CollectionUtils.isEmpty(equipmentIdList)) {
            return Collections.emptyList();
        }
        return equipmentMapper.selectNonExistsEquipmentByEquipmentIds(equipmentIdList);
    }
}
