package com.zb.backend.controller;

import com.zb.backend.annotation.CurrentAccount;
import com.zb.backend.constants.enums.AccountEnum;
import com.zb.backend.constants.enums.ResultEnum;
import com.zb.backend.model.JwtClaim;
import com.zb.backend.model.Result;
import com.zb.backend.model.request.ChangePasswordRequest;
import com.zb.backend.model.response.AccountDetailResponse;
import com.zb.backend.service.AccountService;
import com.zb.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final AuthService authService;

    @Operation(summary = "修改账号状态 Admin")
    @PostMapping("/toggleAccountStatus")
    public Result<Boolean> toggleAccountStatus(@RequestBody Long accountId) {
        System.out.println(this.getClass().getSimpleName() + "，修改用户状态：" + accountId);

        ResultEnum resultEnum = accountService.toggleAccountStatus(accountId);

        if (!resultEnum.getCode().equals(2001)) {
            return Result.error(resultEnum, false);
        }

        return Result.success(resultEnum, true);
    }

    @Operation(summary = "重置密码 Admin")
    @PostMapping("/resetPassword")
    public Result<Boolean> resetPassword(@RequestBody Long accountId) {
        System.out.println(this.getClass().getSimpleName() + " 重置密码：" + accountId);

        ResultEnum resultEnum = accountService.resetPassword(accountId);

        if (!resultEnum.getCode().equals(2001)) {
            return Result.error(resultEnum, false);
        }

        return Result.success(resultEnum, true);
    }

    @Operation(summary = "修改密码")
    @PostMapping("/changePassword")
    public Result<Boolean> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest, @CurrentAccount JwtClaim jwtClaim) {
        System.out.println(this.getClass().getSimpleName() + " 修改密码：" + jwtClaim.getAccountId());
        ResultEnum resultEnum = accountService.changePassword(jwtClaim.getAccountId(), changePasswordRequest);
        if (!resultEnum.getCode().equals(2001)) {
            return Result.error(resultEnum, false);
        }
        // 修改密码成功，调用退出登录方法
        ResultEnum logout = authService.logout(jwtClaim.getAccountId());
        return Result.success(resultEnum, true);
    }

    @Operation(summary = "获取个人信息详情")
    @PostMapping("/getAccountDetail")
    public Result<AccountDetailResponse> getAccountDetail(@CurrentAccount JwtClaim jwtClaim) {
        System.out.println(this.getClass().getSimpleName() + " 获取个人信息详情：" + jwtClaim.getAccountId());

        AccountDetailResponse accountDetail = accountService.getAccountDetail(jwtClaim);

        return Result.success(AccountEnum.SUC_DETAIL, accountDetail);
    }

}
