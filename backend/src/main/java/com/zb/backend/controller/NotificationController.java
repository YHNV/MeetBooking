package com.zb.backend.controller;

import com.zb.backend.annotation.CurrentAccount;
import com.zb.backend.constants.enums.AccountEnum;
import com.zb.backend.constants.enums.EmployeeEnum;
import com.zb.backend.constants.enums.NotificationEnum;
import com.zb.backend.constants.enums.ResultEnum;
import com.zb.backend.entity.Notification;
import com.zb.backend.model.JwtClaim;
import com.zb.backend.model.PageRequest;
import com.zb.backend.model.PageResult;
import com.zb.backend.model.Result;
import com.zb.backend.model.request.ChangePasswordRequest;
import com.zb.backend.model.response.AccountDetailResponse;
import com.zb.backend.model.response.QueryEmployeesResponse;
import com.zb.backend.service.AccountService;
import com.zb.backend.service.AuthService;
import com.zb.backend.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notify")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    // 分页查询通知
    @Operation(summary = "分页查询通知 无条件")
    @PostMapping("/getNotifications")
    public Result<PageResult<Notification>> getNotifications(
            @RequestBody PageRequest pageRequest,
            @CurrentAccount JwtClaim jwtClaim) {
        System.out.println(this.getClass().getSimpleName() + "：" + pageRequest + " " + jwtClaim);
        PageResult<Notification> pageResult = notificationService.getNotifications(pageRequest, jwtClaim);
        return Result.success(NotificationEnum.SUC_GET_NOTIFY, pageResult);
    }

    // 员工获取未读通知数量
    @Operation(summary = "获取未读通知数量 Employee")
    @PostMapping("/getNotReadNum")
    public Result<Integer> getNotReadNum(@CurrentAccount JwtClaim jwtClaim) {
        System.out.println(this.getClass().getSimpleName() + "：" + jwtClaim);
        Integer notReadNum = notificationService.getNotReadNum(jwtClaim);
        return Result.success(NotificationEnum.SUC_GET_NOT_READ_NUM, notReadNum);
    }

    // 员工已读通知
    @Operation(summary = "全部已读")
    @PostMapping("/readAllNotifications")
    public Result<Boolean> readAllNotifications(@CurrentAccount JwtClaim jwtClaim) {
        System.out.println(this.getClass().getSimpleName() + "：" + jwtClaim);
        ResultEnum resultEnum = notificationService.readAllNotifications(jwtClaim);
        if (!resultEnum.getCode().equals(2001)) return Result.error(resultEnum);
        return Result.success(resultEnum, true);
    }

}
