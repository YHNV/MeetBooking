package com.zb.backend.mapper;

import java.util.List;

public interface RoomEquipmentMapper {
    // 根据roomId删除关联
    Boolean deleteByRoomId(Long roomId);

    // 批量插入关联
    Integer insertRoomEquipmentList(Long roomId, List<Long> equipmentIds);
}
