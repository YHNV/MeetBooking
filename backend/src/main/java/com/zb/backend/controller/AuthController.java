package com.zb.backend.controller;

import com.zb.backend.annotation.CurrentAccount;
import com.zb.backend.constants.enums.AuthEnum;
import com.zb.backend.constants.enums.ErrorEnum;
import com.zb.backend.constants.enums.ResultEnum;
import com.zb.backend.model.Result;
import com.zb.backend.model.request.LoginRequest;
import com.zb.backend.model.request.RegisterRequest;
import com.zb.backend.model.response.LoginResponse;
import com.zb.backend.service.AuthService;
import com.zb.backend.util.EnumUtil;
import com.zb.backend.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /*
     * 前端点击登录发送登录请求，参数：账号/工号+密码
     * 后端处理逻辑
     * 首先根据accountId查找用户是否存在
     * 不存在返回ERR
     * 若存在，检查用户是否被冻结
     * 被冻结返回ERR        *
     * 启用状态，继续检查加密后的密码，是否和数据库中相匹配
     * 不匹配返回ERR
     * 如果用户名和密码都匹配，返回SUC
     * Controller判断是SUC后，调用getLoginInfo接口，获取loginResponse
     * 根据accountId查询员工信息，将以下信息返回给前端
     * 登录Response：工号、姓名、职位、部门名称、是否为部门经理、是否为管理员、是否首次登录、最后登录时间以及Token
     *  */

    // 登录
    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        // 打印获取到的登录请求
        System.out.println("登录请求体" + loginRequest);
        // Long accountId = loginRequest.getAccountId();
        // String password = loginRequest.getPassword();

        // 将账号密码传给Service，Service返回是authEnum
        ResultEnum resultEnum = authService.login(loginRequest.getAccountId(), loginRequest.getPassword());

        // 判断resultEnum的Code是否为2001登录成功
        if (!resultEnum.getCode().equals(2001)) {
            // 如果不成功，返回ResultEnum，不带数据体
            return Result.error(resultEnum);
        }

        // 如果是登录成功，则调用getLoginInfo
        LoginResponse loginResponse = authService.getLoginInfo(loginRequest.getAccountId());
        return Result.success(resultEnum, loginResponse);
    }

    // 退出登录
    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Boolean> logout(@CurrentAccount Long accountId) {
        ResultEnum resultEnum = authService.logout(accountId);
        if (!resultEnum.getCode().equals(2001)) {
            return Result.error(resultEnum, false);
        }
        return Result.success(resultEnum, true);
    }

    // 注册
    @Operation(summary = "注册 Admin")
    @PostMapping("/register")
    public Result<Boolean> register(@Valid @RequestBody RegisterRequest registerRequest) {
        /*
        * 注册逻辑
        * 接收到注册请求
        * 传给Service进行处理
        *  */
        System.out.println(this.getClass().getSimpleName() + "：" + registerRequest);

        // try {
            ResultEnum resultEnum = authService.register(registerRequest);

            if (!resultEnum.getCode().equals(2001)) {
                // 注册失败
                return Result.error(resultEnum, false);
            }

            // 注册成功，返回注册成功
            return Result.success(resultEnum, true);

        // } catch (RuntimeException e) {
        //     String errorMsg = e.getMessage();
        //     System.out.println(this.getClass().getSimpleName() + "：" + errorMsg);
        //     // 调用工具类，传入所有实现ResultEnum的枚举类
        //     ResultEnum errorEnum = EnumUtil.findByMessage(errorMsg, AuthEnum.class, ErrorEnum.class);
        //     // 若未找到匹配的枚举类，是用默认错误
        //     if (errorEnum == null) {
        //         errorEnum = ErrorEnum.ERR_SERVER;
        //     }
        //     return Result.error(5001, e.getMessage());
        // }

    }

}
