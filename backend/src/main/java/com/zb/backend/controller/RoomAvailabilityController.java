package com.zb.backend.controller;

import com.zb.backend.constants.enums.ReservationEnum;
import com.zb.backend.constants.enums.RoomAvailabilityEnum;
import com.zb.backend.entity.RoomAvailability;
import com.zb.backend.model.Result;
import com.zb.backend.model.TimeSlot;
import com.zb.backend.model.request.GetRoomAvailTimeSlotListRequest;
import com.zb.backend.model.response.GetRoomAvailDateListResponse;
import com.zb.backend.model.response.GetRoomAvailTimeSlotListResponse;
import com.zb.backend.service.ReservationService;
import com.zb.backend.service.RoomAvailabilityService;
import com.zb.backend.util.RoomTimeSlotUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/roomAvail")
@RequiredArgsConstructor
public class RoomAvailabilityController {
    private final ReservationService reservationService;
    private final RoomAvailabilityService roomAvailabilityService;

    /*
    *
    * 预约逻辑
    * 用户从会议室详情点击预约，首先需要会议室预约时间表，和同部门员工信息
    * 提交预约申请，默认为待审核状态
    *
    *  */


    @Operation(summary = "获取会议室可用状态 Obsolete")
    @PostMapping("/getRoomAvailability")
    public Result<List<RoomAvailability>> getRoomAvailability(@RequestBody Long roomId) {
        List<RoomAvailability> availabilityList = roomAvailabilityService.getRoomAvailability(roomId);
        return Result.success(RoomAvailabilityEnum.SUC_GET_AVAIL, availabilityList);
    }

    @Operation(summary = "获取当前会议室可用日期")
    @PostMapping("/getRoomAvailDateList")
    public Result<GetRoomAvailDateListResponse> getRoomAvailDateList(@RequestBody Long roomId) {
        GetRoomAvailDateListResponse roomAvailDateList = roomAvailabilityService.getRoomAvailDateList(roomId);
        return Result.success(RoomAvailabilityEnum.SUC_GET_AVAIL_DATE, roomAvailDateList);
    }

    @Operation(summary = "获取所有预约时间段")
    @PostMapping("/getAllTimeSlotList")
    public Result<List<TimeSlot>> getAllTimeSlotList() {
        List<TimeSlot> allTimeSlotList = RoomTimeSlotUtil.getAllTimeSlots();
        return Result.success(RoomAvailabilityEnum.SUC_GET_ALL_TIME_SLOT, allTimeSlotList);
    }


    @Operation(summary = "获取当前会议室可预约时间段")
    @PostMapping("/getRoomAvailTimeSlotList")
    public Result<GetRoomAvailTimeSlotListResponse>  getRoomAvailTimeSlotList(@RequestBody GetRoomAvailTimeSlotListRequest request) {
        System.out.println(this.getClass().getSimpleName() + "：获取会议室可预约时间段：" + request);
        GetRoomAvailTimeSlotListResponse response = roomAvailabilityService.getRoomAvailTimeSlotList(request);
        return Result.success(RoomAvailabilityEnum.SUC_GET_AVAIL_STATUS, response);
    }

}
