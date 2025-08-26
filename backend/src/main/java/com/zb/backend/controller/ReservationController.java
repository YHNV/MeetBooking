package com.zb.backend.controller;

import com.zb.backend.constants.enums.DepartmentEnum;
import com.zb.backend.constants.enums.ReservationEnum;
import com.zb.backend.entity.RoomAvailability;
import com.zb.backend.model.Result;
import com.zb.backend.model.response.SimpleDepartmentResponse;
import com.zb.backend.service.DepartmentService;
import com.zb.backend.service.ReservationService;
import com.zb.backend.service.RoomAvailabilityService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/res")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;
    private final RoomAvailabilityService roomAvailabilityService;

    /*
    *
    * 预约逻辑
    * 用户从会议室详情点击预约，首先需要会议室预约时间表，和同部门员工信息
    * 提交预约申请，默认为待审核状态
    *
    *  */

    // @Operation(summary = "获取会议室可用状态")
    // @PostMapping("/getRoomAvailability")
    // public Result<List<RoomAvailability>> getRoomAvailability(@RequestBody Long roomId) {
    //     List<RoomAvailability> availabilityList = roomAvailabilityService.getRoomAvailability(roomId);
    //     return Result.success(ReservationEnum.SUC_GET_AVAIL, availabilityList);
    // }


    // private final DepartmentService departmentService;
    //
    // @Operation(summary = "获取简单部门集合")
    // @PostMapping("/getSimpleDepartmentList")
    // public Result<List<SimpleDepartmentResponse>> getSimpleDepartmentList() {
    //     List<SimpleDepartmentResponse> simpleDeptList = departmentService.getSimpleDepartmentList();
    //     return Result.success(DepartmentEnum.SUC_SIMPLE_LIST, simpleDeptList);
    // }
}
