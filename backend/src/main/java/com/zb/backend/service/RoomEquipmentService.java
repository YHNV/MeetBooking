package com.zb.backend.service;

import com.zb.backend.entity.Equipment;
import com.zb.backend.mapper.RoomEquipmentMapper;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomEquipmentService {
    private final RoomEquipmentMapper roomEquipmentMapper;

    // 根据roomId删除关联
    public Boolean deleteByRoomId(Long roomId) {
        return roomEquipmentMapper.deleteByRoomId(roomId);
    }

    // 批量插入关联
    public Integer addRoomEquipmentList(Long roomId, List<Long> equipmentIds) {
        return roomEquipmentMapper.insertRoomEquipmentList(roomId, equipmentIds);
    }

    // 通过会议室id，查找设备ids集合
    public List<Long> getEquipmentIdsByRoomId(@NotNull(message = "会议室id不能为空") Long roomId) {
        return roomEquipmentMapper.selectEquipmentIdsByRoomId(roomId);
    }

    // 通过会议室id，查找设备集合
    public List<Equipment> selectEquipmentListByRoomId(Long roomId) {
        return roomEquipmentMapper.selectEquipmentListByRoomId(roomId);
    }
}
