package com.zb.backend.service;

import com.zb.backend.constants.enums.EquipmentEnum;
import com.zb.backend.constants.enums.MeetingRoomEnum;
import com.zb.backend.constants.enums.ResultEnum;
import com.zb.backend.entity.Equipment;
import com.zb.backend.entity.MeetingRoom;
import com.zb.backend.mapper.MeetingRoomMapper;
import com.zb.backend.model.PageResult;
import com.zb.backend.model.request.AddMeetingRoomRequest;
import com.zb.backend.model.request.QueryMeetingRoomsRequest;
import com.zb.backend.model.response.QueryEmployeesResponse;
import com.zb.backend.model.response.QueryMeetingRoomsResponse;
import com.zb.backend.util.FileUploadUtil;
import com.zb.backend.util.PaginationValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

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

    // 删除照片
    private void deleteImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            boolean deleteImage = FileUploadUtil.deleteImage(imageUrl);
            System.out.println("插入失败，删除已上传照片：" + deleteImage);
        }
    }

    // 分页查询会议室信息
    public PageResult<MeetingRoom> queryMeetingRooms(@Valid QueryMeetingRoomsRequest queryRequest) {

        // 判断容量参数是否合法
        if (queryRequest.getMinCapacity() != null && queryRequest.getMaxCapacity() != null) {
            if (queryRequest.getMinCapacity() > queryRequest.getMaxCapacity()) {
                throw new RuntimeException(MeetingRoomEnum.ERR_ILLEGAL_CAPACITY.getMessage());
            }
        }

        // 首先判断equipmentIdList中，是否存在不存在的id
        List<Long> nonExistsIds = equipmentService.getNonExistsEquipmentByEquipmentIds(queryRequest.getEquipmentIdList());
        if (nonExistsIds != null && !nonExistsIds.isEmpty()) {
            // 存在不存在的设备id
            System.out.println("该设备不存在：" + nonExistsIds);
            throw new RuntimeException(MeetingRoomEnum.ERR_NOT_EQUIP.getMessage() + ":" + nonExistsIds);
        }

        // 查询总记录数
        Integer total = meetingRoomMapper.countMeetingRoomList(queryRequest);

        // 分页校验工具类
        PageResult<MeetingRoom> pageResult = PaginationValidator.validatePagination(queryRequest, total);
        if (pageResult != null) {
            return pageResult;
        }

        // 记录数不为0，根据筛选条件查询
        List<MeetingRoom> meetingRoomList = meetingRoomMapper.selectMeetingRoomList(queryRequest);

        return new PageResult<>(total, queryRequest.getPageNum(), queryRequest.getPageSize(), meetingRoomList);

    }

    // 根据会议室id获取所添加的设备集合
    public List<Equipment> getRoomEquipmentList(Long roomId) {
        // 判断该会议室是否存在
        MeetingRoom meetingRoom = meetingRoomMapper.selectMeetingRoomByRoomId(roomId);

        if (meetingRoom == null) {
            throw new RuntimeException(MeetingRoomEnum.ERR_EXISTS_ROOM.getMessage());
        }

        // 如果会议室存在，那个根据roomId，通过会议室设备关联表查询到设备ids，返回设备集合
        return meetingRoomMapper.selectEquipmentListByRoomId(roomId);
    }

    // 删除会议室
    // public ResultEnum deleteMeetingRoom(Long roomId) {
    //     // 首先需要判断该会议室是否存在，如果存在才执行删除
    //     Boolean existsRoom = meetingRoomMapper.selectMeetingRoomByRoomId(roomId);
    //
    //     if (!existsRoom) {
    //         // 会议室不存在，不执行删除
    //     }
    //
    //     Boolean deleteRoom = meetingRoomMapper.deleteMeetingRoomByRoomId(roomId);
    // }
}
