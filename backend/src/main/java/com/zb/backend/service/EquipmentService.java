package com.zb.backend.service;

import com.zb.backend.mapper.EquipmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentService {
    private final EquipmentMapper equipmentMapper;

    // 根据id集合查询存在的id有哪些
    public List<Long> getExistsByIds(List<Long> equipmentIds) {
        return equipmentMapper.selectExistsByIds(equipmentIds);
    }
}
