package com.zb.backend.controller;

import com.zb.backend.annotation.CurrentAccount;
import com.zb.backend.constants.enums.ResultEnum;
import com.zb.backend.model.Result;
import com.zb.backend.model.request.ChangePassword;
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
    public Result<Boolean> changePassword(@Valid @RequestBody ChangePassword changePassword, @CurrentAccount Long accountId) {
        System.out.println(this.getClass().getSimpleName() + " 修改密码：" + accountId);
        ResultEnum resultEnum = accountService.changePassword(accountId, changePassword);
        if (!resultEnum.getCode().equals(2001)) {
            return Result.error(resultEnum, false);
        }
        // 修改密码成功，调用退出登录方法
        ResultEnum logout = authService.logout(accountId);
        return Result.success(resultEnum, true);
    }


}
