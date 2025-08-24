package com.zb.backend.service;

import com.zb.backend.constants.enums.MeetingRoomEnum;
import com.zb.backend.constants.enums.ResultEnum;
import com.zb.backend.entity.MeetingRoom;
import com.zb.backend.mapper.MeetingRoomMapper;
import com.zb.backend.model.request.AddMeetingRoomRequest;
import com.zb.backend.util.FileUploadUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetingRoomService {
    private final MeetingRoomMapper meetingRoomMapper;
    private final RoomEquipmentService roomEquipmentService;
    private final EquipmentService equipmentService;

    // 新增会议室
    @Transactional(rollbackFor = Exception.class)
    public ResultEnum addMeetingRoom(@Valid AddMeetingRoomRequest addMeetingRoomRequest) {
        // 首先根据会议室名字查询，会议室是否存在
        MeetingRoom existsRoom = meetingRoomMapper.selectMeetingRoomByRoomName(addMeetingRoomRequest.getRoomName());

        if (existsRoom != null) {
            // 插入失败，同时删除已上传的图片
            deleteImage(addMeetingRoomRequest.getImageUrl());
            // return MeetingRoomEnum.ERR_EXISTS_NAME;
            throw new RuntimeException(MeetingRoomEnum.ERR_EXISTS_NAME.getMessage());
        }

        // 会议室不存在，执行插入语句
        Boolean insertMeetingRoom = meetingRoomMapper.insertMeetingRoom(addMeetingRoomRequest);

        if (!insertMeetingRoom) {
            // 插入失败，同时删除已上传的图片
            deleteImage(addMeetingRoomRequest.getImageUrl());
            // return MeetingRoomEnum.ERR_ADD_ROOM;
            throw new RuntimeException(MeetingRoomEnum.ERR_ADD_ROOM.getMessage());
        }

        // 通过name去查询会议室，得到对象
        MeetingRoom meetingRoom = meetingRoomMapper.selectMeetingRoomByRoomName(addMeetingRoomRequest.getRoomName());

        List<Long> equipmentIds = addMeetingRoomRequest.getEquipmentIds();

        // 如果插入成功，首先我们判断传过来的equipmentIds，并插入关联关系
        if (equipmentIds != null && !equipmentIds.isEmpty()) {
            // 先删除已存在的设备关联，新表没有可删的，所以是0，false
            Boolean deleteRoomEquip = roomEquipmentService.deleteByRoomId(meetingRoom.getRoomId());
            // if (!deleteRoomEquip) {
                // 删除失败，同时删除已上传的图片
                // if (addMeetingRoomRequest.getImageUrl() != null && !addMeetingRoomRequest.getImageUrl().isEmpty()) {
                //     boolean deleteImage = FileUploadUtil.deleteImage(addMeetingRoomRequest.getImageUrl());
                //     System.out.println("插入失败，删除已上传照片：" + deleteImage);
                // }
                // // return MeetingRoomEnum.ERR_DELETE_EQUIP;
                // throw new RuntimeException(MeetingRoomEnum.ERR_DELETE_EQUIP.getMessage());
            // }

            Integer equipNum = equipmentIds.size();

            System.out.println("新增会议室，设备数量：" + equipNum);

            // 判断一下，每个id在数据库中是否存在
            // 2. 批量查询数据库中存在的设备ID
            List<Long> existingIds = equipmentService.getExistsByIds(equipmentIds);

            // 3. 找出不存在的ID
            List<Long> nonExistingIds = equipmentIds.stream()
                    .filter(id -> !existingIds.contains(id))
                    .collect(Collectors.toList());

            // 4. 如果存在无效ID，抛出异常提示
            if (!nonExistingIds.isEmpty()) {
                // 插入失败，同时删除已上传的图片
                deleteImage(addMeetingRoomRequest.getImageUrl());
                throw new RuntimeException("设备ID不存在：" + nonExistingIds);
            }

            // 创建新的设备关联，批量插入
            Integer insertNum = roomEquipmentService.addRoomEquipmentList(meetingRoom.getRoomId(), equipmentIds);

            System.out.println("新增会议室，插入设备管理数量：" + insertNum);

            if (!equipNum.equals(insertNum)) {
                // 插入失败，同时删除已上传的图片
                deleteImage(addMeetingRoomRequest.getImageUrl());
                throw new RuntimeException(MeetingRoomEnum.ERR_ROOM_EQUIP.getMessage());
            }
        }
        
        return MeetingRoomEnum.SUC_ADD_ROOM;

    }

    private void deleteImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            boolean deleteImage = FileUploadUtil.deleteImage(imageUrl);
            System.out.println("插入失败，删除已上传照片：" + deleteImage);
        }
    }
}
