package com.zb.backend.mapper;

import com.zb.backend.entity.Equipment;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface RoomEquipmentMapper {
    // 根据roomId删除关联
    Boolean deleteByRoomId(Long roomId);

    // 批量插入关联
    Integer insertRoomEquipmentList(Long roomId, List<Long> equipmentIds);

    // 通过会议室id查找设备id集合
    List<Long> selectEquipmentIdsByRoomId(@NotNull(message = "会议室id不能为空") Long roomId);

    // 通过会议室id，查找设备集合
    List<Equipment> selectEquipmentListByRoomId(Long roomId);

    // 根据设备id删除关联
    Integer deleteByEquipId(Long equipmentId);
}
