package com.zb.backend.service;

import com.zb.backend.mapper.RoomEquipmentMapper;
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
}
