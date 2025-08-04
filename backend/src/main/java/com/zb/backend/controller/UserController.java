package com.zb.backend.controller;

import com.zb.backend.constants.enums.AuthEnum;
import com.zb.backend.entity.User;
import com.zb.backend.model.request.LoginRequest;
import com.zb.backend.model.Result;
import com.zb.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 登录
    @PostMapping("login")
    // public String login(@RequestBody Map<String, String> loginData) {
    public Result login(@RequestBody LoginRequest loginRequest) {
        System.out.println(loginRequest);
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();


        // System.out.println("登录信息" + loginData);
        // String username = loginData.get("username");
        // String password = loginData.get("password");

        System.out.println("username: " + username + " password: " + password);

        User user = userService.login(new User(username, password));
        if (user != null) {
            // return "登录成功，欢迎：" + user.getUsername();
            return Result.success(AuthEnum.SUC.getCode(), AuthEnum.SUC.getMessage(), user);
        } else {
            // return "账号或密码错误";
            return Result.error(AuthEnum.ERR_WRONG_CRED.getCode(), AuthEnum.ERR_WRONG_CRED.getMessage());
        }
    }
}
