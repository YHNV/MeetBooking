package com.zb.backend.service;

import com.zb.backend.constants.enums.EquipmentEnum;
import com.zb.backend.constants.enums.ResultEnum;
import com.zb.backend.entity.Equipment;
import com.zb.backend.mapper.EquipmentMapper;
import com.zb.backend.model.request.AddEquipmentRequest;
import com.zb.backend.model.request.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentService {
    private final EquipmentMapper equipmentMapper;
    private final RoomEquipmentService roomEquipmentService;

    // 根据设备id集合查询存在的id有哪些
    public List<Long> getExistsByIds(List<Long> equipmentIds) {
        return equipmentMapper.selectExistsByIds(equipmentIds);
    }

    // 获取设备集合
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

    // 新增设备
    public ResultEnum addEquipment(AddEquipmentRequest request) {
        // 首先判断该设备是否存在
        Equipment existsEquip = equipmentMapper.selectEquipByName(request.getEquipmentName());
        if (existsEquip != null) return EquipmentEnum.ERR_ADD_EXISTS;

        // 如果不存在，执行添加操作
        Boolean addEquip = equipmentMapper.insertEquip(request);
        if (!addEquip) return EquipmentEnum.ERR_ADD_EQUIP;

        return EquipmentEnum.SUC_ADD_EQUIP;
    }

    // 修改设备，只能修改名称和详情
    public ResultEnum updateEquipment(Equipment equipment) {
        // 首先根据id判断设备是否存在
        Equipment existsEquip = equipmentMapper.selectEquipById(equipment.getEquipmentId());
        if (existsEquip == null) return EquipmentEnum.ERR_UPDATE_NOT_ID;

        // 判断修改的设备名称是否和原来一样，如果不一样，判断名字是否存在
        if (!equipment.getEquipmentName().equals(existsEquip.getEquipmentName())) {
            // 当名称不一样，判断数据库中是否有存在的名字
            Equipment equipByName = equipmentMapper.selectEquipByName(equipment.getEquipmentName());
            if (equipByName != null) return EquipmentEnum.ERR_ADD_EXISTS;
        }

        // 没问题后，执行修改操作
        Boolean updateEquip = equipmentMapper.updateEquip(equipment);
        if (!updateEquip) return EquipmentEnum.ERR_UPDATE_EQUIP;

        return EquipmentEnum.SUC_UPDATE_EQUIP;
    }

    // 删除设备
    @Transactional(rollbackFor = Exception.class)
    public ResultEnum deleteEquipment(Long equipmentId) {
        // 首先判断设备是否存在
        Equipment existsEquip = equipmentMapper.selectEquipById(equipmentId);
        if (existsEquip == null) return EquipmentEnum.ERR_UPDATE_NOT_ID;

        // 删除设备关联表中，设备id=关联表中的设备id
        Integer deleteNum = roomEquipmentService.deleteByEquipId(equipmentId);
        System.out.println("删除" + deleteNum + "条设备：" + equipmentId);

        // 接着删除设备
        Boolean deleteEquip = equipmentMapper.deleteById(equipmentId);
        if (!deleteEquip) throw new RuntimeException(EquipmentEnum.ERR_DELETE_EQUIP.getMessage());

        return EquipmentEnum.SUC_DELETE_EQUIP;
    }
}
