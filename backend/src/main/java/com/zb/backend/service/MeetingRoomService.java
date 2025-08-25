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
import com.zb.backend.model.request.UpdateMeetingRoomRequest;
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
import java.util.Objects;
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
                throw new RuntimeException(MeetingRoomEnum.ERR_ADD_NOT_EQUIP.getMessage() + "：" + nonExistingIds);
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
        return roomEquipmentService.selectEquipmentListByRoomId(roomId);
    }

    /*
    *
    * 修改会议室
    * 和新增的逻辑很像
    * 首先根据id获取到会议室对象，判断是否存在，不存在返回ERR
    * 然后判断请求中的名字和会议室对象中的名字是否相同，不相同，就通过这个名字去查询会议室，能查到返回ERR
    * 同样的操作判断图片URL，这个URL在最一开始判断，如果是一样的，操作失败无需删除，如果不一样，操作失败需要删除
    * 最后是设备列表，也是一样，先判断ids
    *
    *  */

    // 修改会议室
    @Transactional(rollbackFor = Exception.class)
    public ResultEnum updateMeetingRoom(@Valid UpdateMeetingRoomRequest updateMeetingRoomRequest) {
        // 首先通过id查询会议室对象数据
        MeetingRoom meetingRoom = meetingRoomMapper.selectMeetingRoomByRoomId(updateMeetingRoomRequest.getRoomId());

        // 会议室不存在，无法修改
        if (meetingRoom == null) throw new RuntimeException(MeetingRoomEnum.ERR_UPDATE_ID.getMessage());

        String oldUrl = meetingRoom.getImageUrl();
        String newUrl = updateMeetingRoomRequest.getImageUrl();

        // 会议室存在，判断是否需要删除图片
        // 判断两个URL是否相同
        // 标记，是否需要删除，如果两张图片一样，新旧URL都不需要删除，如果两张图不一样，抛异常删除新的，新增的时候删除旧的
        Boolean isDeleteUrl = !(Objects.equals(oldUrl, newUrl));

        // 判断新旧名字是否相同，如果不相同，需要判断名字是否重复
        MeetingRoom existsRoom = meetingRoomMapper.selectMeetingRoomByRoomName(updateMeetingRoomRequest.getRoomName());

        if (existsRoom != null) {
            // 插入失败，同时删除已上传的图片
            if (isDeleteUrl) deleteImage(newUrl);
            throw new RuntimeException(MeetingRoomEnum.ERR_UPDATE_NAME.getMessage());
        }

        // 获取请求中的设备集合 新的
        List<Long> newEquipmentIds = updateMeetingRoomRequest.getEquipmentIds();
        System.out.println("更新会议室，新的设备集合：" + newEquipmentIds);

        // 获取当前会议室id的设备集合 旧的
        List<Long> oldEquipmentIds = roomEquipmentService.getEquipmentIdsByRoomId(updateMeetingRoomRequest.getRoomId());
        System.out.println("更新会议室，旧的设备集合：" + oldEquipmentIds);

        // 判断请求中的设备集合，是否有无效设备ID
        List<Long> existingIds = equipmentService.getExistsByIds(newEquipmentIds);
        // 找出不存在的ID
        List<Long> nonExistingIds = newEquipmentIds.stream()
                .filter(id -> !existingIds.contains(id))
                .collect(Collectors.toList());
        // 如果存在无效ID，抛出异常提示
        if (!nonExistingIds.isEmpty()) {
            // 插入失败，同时删除已上传的图片
            if (isDeleteUrl) deleteImage(newUrl);
            throw new RuntimeException(MeetingRoomEnum.ERR_UPDATE_EXISTS_EQUIP.getMessage() + "：" + nonExistingIds);
        }

        // 判断两个equipIds是否完全相同，如果完全相同，那么不用进行操作，如果不同，删除旧的，增加新的
        Boolean isEquipIdsSame = Objects.equals(oldEquipmentIds, newEquipmentIds);

        // 当新旧设备id相同的时候，不需要对会议室设备关联表进行操作，能进到下面这个判断，说明肯定不一样，一个为空，另一个就不为空
        if (!isEquipIdsSame) {
            // 获取新设备集合的数量
            Integer equipNum = newEquipmentIds.size();
            System.out.println("修改会议室，请求设备数量：" + equipNum);
            System.out.println();
            // 当不相同时，判断，旧的为空，不删除只新增，新的为空，只删除不新增
            if (oldEquipmentIds != null && !oldEquipmentIds.isEmpty()) {
                // 当新的为空时，只删除不新增，删除新的
                Boolean deleteRoomEquip = roomEquipmentService.deleteByRoomId(updateMeetingRoomRequest.getRoomId());
                if (!deleteRoomEquip) throw new RuntimeException(MeetingRoomEnum.ERR_UPDATE_DELETE_EQUIP.getMessage());
            }
            if (newEquipmentIds != null && !newEquipmentIds.isEmpty()) {
                // 当旧的为空时，只新增不删除，新增旧的
                // 创建新的设备关联，批量插入
                Integer insertNum = roomEquipmentService.addRoomEquipmentList(updateMeetingRoomRequest.getRoomId(), newEquipmentIds);
                System.out.println("修改会议室，完成插入设备数量：" + insertNum);
                if (!equipNum.equals(insertNum)) {
                    if (isDeleteUrl) deleteImage(newUrl);
                    throw new RuntimeException(MeetingRoomEnum.ERR_UPDATE_INSERT_EQUIP.getMessage());
                }
            }
        }

        // 解决完所有的问题，可以开始修改了
        // 修改前，判断是否要删除旧照片，
        if (isDeleteUrl) deleteImage(oldUrl);

        Boolean isUpdate = meetingRoomMapper.updateMeetingRoomByRoomId(updateMeetingRoomRequest);

        System.out.println("是否更新成功：" + isUpdate);

        if (!isUpdate) throw new RuntimeException(MeetingRoomEnum.ERR_UPDATE_ROOM.getMessage());

        return MeetingRoomEnum.SUC_UPDATE_ROOM;

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
