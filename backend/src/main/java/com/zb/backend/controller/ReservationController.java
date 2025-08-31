package com.zb.backend.controller;

import com.zb.backend.annotation.CurrentAccount;
import com.zb.backend.constants.enums.DepartmentEnum;
import com.zb.backend.constants.enums.MeetingRoomEnum;
import com.zb.backend.constants.enums.ReservationEnum;
import com.zb.backend.constants.enums.ResultEnum;
import com.zb.backend.entity.MeetingRoom;
import com.zb.backend.entity.Reservation;
import com.zb.backend.entity.RoomAvailability;
import com.zb.backend.model.JwtClaim;
import com.zb.backend.model.PageResult;
import com.zb.backend.model.Result;
import com.zb.backend.model.request.QueryReservationRequest;
import com.zb.backend.model.request.ReservationRequest;
import com.zb.backend.model.request.UpdateReservationRequest;
import com.zb.backend.model.response.SimpleDepartmentResponse;
import com.zb.backend.service.DepartmentService;
import com.zb.backend.service.ReservationService;
import com.zb.backend.service.RoomAvailabilityService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
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
    * 预约逻辑，操作多张表，需要开启事务 1
    * 前置功能已完成：获取同部门员工信息、获取可用日期、获取所有预约时段、获取可预约时段 1
    * 首先是接收前端传过来的预约请求模型，以及通过Token解析的JwtClaim 1
    * 1、通过JwtClaim获取isAdmin，如果是管理员，抛异常：非法预约 1
    * 2、判断会议室是否存在（其实应该弄一个拦截器的，算了）1
    * 3、判断会议室状态是否为：已启用 1
    * 4、通过accountId获取isManager 1
    * 5、两个判断 if(type==Large && !isManager)，还有一个@全体员工，都是非经理不能选 1
    * 以上基础校验基本完成，接下来是预约时间的判断
    * 1、获取请求中的预约时间：日期、开始时间、结束时间 1
    * 2、通过时间段工具类处理得到int类型的状态值 1
    * 3、通过roomId和预约日期，获取原本的状态值 1
    * 4、两个状态值做“与”运算，如果返回结果不是0，那么抛异常：预约时间段已被占用 1
    * 5、如果结果为0，那么进行第一次数据库操作，修改那一天的状态值 1
    * 6、修改没问题，就可以根据accountId获取员工的姓名 1
    * 7、根据请求体内容和员工姓名，新增一条待审核的预约记录 1
    * 以上是预约操作，接下来还有两个表的操作：预约参与表，通知表
    * 1、通过请求中的参会ids集合，批量去预约参与表中创建数据（其实应该先判断员工是否存在的）1
    * 2、给预约人发送一个预约成功的通知，在预约请求通过后，再给参加的人发送通知
    *
    *  */

    @Operation(summary = "会议室预约 Employee")
    @PostMapping("/reservation")
    public Result<Boolean> reservation(
            @Valid @RequestBody ReservationRequest reservationRequest,
            @CurrentAccount JwtClaim jwtClaim
    ) {
        System.out.println(this.getClass().getSimpleName() + " 预约会议室：" + reservationRequest);
        ResultEnum resultEnum = reservationService.reservation(reservationRequest, jwtClaim);
        if (!resultEnum.getCode().equals(2001)) return Result.error(resultEnum, false);
        return Result.success(resultEnum, true);
    }

    /*
    * 查询会议室
    * 判断isAdmin
    * 管理员可以查全部
    * 员工只能查自己的id
    * 可以根据日期和预约状态进行筛选
    * 只查预约表内容，点击详情后再查看有哪些关联人员
    *  */

    @Operation(summary = "分页查询预约信息")
    @PostMapping("/queryReservations")
    public Result<PageResult<Reservation>> queryReservations(
            @Valid @RequestBody QueryReservationRequest request,
            @CurrentAccount JwtClaim jwtClaim) {
        System.out.println(this.getClass().getSimpleName() + "：" + request);
        PageResult<Reservation> pageResult = reservationService.queryReservations(request, jwtClaim);
        return Result.success(ReservationEnum.SUC_QUERY_RES, pageResult);
    }

    /*
    *
    * 修改预约状态逻辑
    * 在默认 待审核 状态下
    * 员工可以进行取消操作，管理员可以进行：同意、拒绝
    *
    * 员工 取消 只可在 待审核 和 已通过 这两种状态下进行
    * 管理员 只可在 待审核 状态下 才能进行修改操作 1
    *
    * 当员工 取消 和 管理员 拒绝 的情况下，回退会议室可用时间段
    * 并给预约人发送通知：预约取消/被拒绝，原因：
    *
    * 当管理员修改状态为 已通过 时，给参与人员发送一条提醒
    * 管理员操作需要更新审核时间，不更新了，没用，直接看最后更新时间
    *
    * 需要开启事务
    *
    *  */

    @Operation(summary = "修改预约状态")
    @PostMapping("/updateReservation")
    public Result<Boolean> updateReservation(
            @Valid @RequestBody UpdateReservationRequest request,
            @CurrentAccount JwtClaim jwtClaim
    ) {
        System.out.println(this.getClass().getSimpleName() + " 修改会议室：" + request);
        ResultEnum resultEnum = reservationService.updateReservation(request, jwtClaim);
        if (!resultEnum.getCode().equals(2001)) return Result.error(resultEnum, false);
        return Result.success(resultEnum, true);
    }
}
