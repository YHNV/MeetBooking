package com.zb.backend.controller;

import com.zb.backend.constants.enums.AuthEnum;
import com.zb.backend.constants.enums.ResultEnum;
import com.zb.backend.model.Result;
import com.zb.backend.model.request.LoginRequest;
import com.zb.backend.model.response.LoginResponse;
import com.zb.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @PostMapping("/login")
    public Result login(@RequestBody LoginRequest loginRequest) {
        // 打印获取到的登录请求
        System.out.println("登录请求体" + loginRequest);
        // Long accountId = loginRequest.getAccountId();
        // String password = loginRequest.getPassword();

        // 将账号密码传给Service，Service返回是authEnum
        ResultEnum resultEnum = authService.login(loginRequest.getAccountId(), loginRequest.getPassword());

        // 判断resultEnum的Code是否为2001登录成功
        if (!resultEnum.getCode().equals(2001)) {
            // 如果不成功，返回ResultEnum，不带数据体
            return Result.error(resultEnum, null);
        }

        // 如果是登录成功，则调用getLoginInfo
        LoginResponse loginResponse = authService.getLoginInfo(loginRequest.getAccountId());
        return Result.success(resultEnum, loginResponse);
    }

    // // 获取前端账号展示信息
    // @PostMapping("/info")
    // public Result info(@RequestBody Long accountId) {
    //     System.out.println("需要获取信息的账号" + accountId);
    //
    //     return Result.success(200, "获取成功", null);
    // }
}
