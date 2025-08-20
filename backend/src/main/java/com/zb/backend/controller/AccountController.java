package com.zb.backend.controller;

import com.zb.backend.model.Result;
import com.zb.backend.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "修改账号状态 Admin")
    @PostMapping("/toggleAccountStatus")
    public Result<Boolean> toggleAccountStatus(@RequestBody Long accountId) {
        System.out.println(this.getClass().getSimpleName() + "，修改用户状态：" + accountId);

        Boolean toggle = accountService.toggleAccountStatus(accountId);

        if (!toggle) {
            return Result.success(4001, "修改失败", false);
        }

        return Result.success(2001, "修改成功", true);
    }
}
