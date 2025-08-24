package com.zb.backend.mapper;

import java.util.List;

public interface EquipmentMapper {
    // 根据id集合查询存在的id有哪些
    List<Long> selectExistsByIds(List<Long> equipmentIds);
}
