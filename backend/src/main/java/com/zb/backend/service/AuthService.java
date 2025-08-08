package com.zb.backend.service;

import com.zb.backend.constants.enums.AuthEnum;
import com.zb.backend.constants.enums.ResultEnum;
import com.zb.backend.entity.Account;
import com.zb.backend.mapper.AccountMapper;
import com.zb.backend.model.response.LoginResponse;
import com.zb.backend.util.JwtUtil;
import com.zb.backend.util.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;

    // 登录
    public ResultEnum login(Long accountId, String password) {

        // 通过账号查询用户是否存在
        Account account = accountService.getAccountByAccountId(accountId);

        // 如果不存在
        if (account == null) {
            return AuthEnum.ERR_ACC_NOT_EXIST;   // 账号不存在
        }

        // 如果账号被冻结
        if (!account.getIsActive()) {
            return AuthEnum.ERR_ACC_LOCKED;   // 账号被冻结不可登录
        }

        // 获取密码
        String storePassword = account.getPassword();

        // 将用户输入的密码调用加密算法进行对比
        if (passwordEncoder.matches(password, storePassword)) {
            // 密码校验成功，登录成功
            return AuthEnum.SUC_LOGIN;
        } else {
            // 登录失败
            return AuthEnum.ERR_WRONG_PWD;
        }
    }

    // 登录获取用户信息
    public LoginResponse getLoginInfo(Long accountId) {
        /*
        * 账号表：
        * Boolean isAdmin;
        * Boolean firstLogin;
        * LocalDateTime lastLoginTime;
        *
        * 员工表：
        * Long empId;
        * String empName;
        * Boolean isManager;
        * String position;
        *
        * 部门表：
        * String deptName;
        *
        * 获取Token
        * String token;
        *
        *  */
        // 通过accountId在account表中联表查询，将数据封装进LoginResponse中
        LoginResponse loginResponse = accountService.getLoginInfo(accountId);

        // 查询完成后，获取Token，传入LoginResponse.token
        String token = JwtUtil.createToken(accountId);
        loginResponse.setToken(token);

        // 获取完成后，更新最后登录时间，这样用户获取的就都是上次的登录时间
        Boolean isUpdate = accountService.updateLastLoginTimeByAccountId(accountId);
        System.out.println("最后登录时间是否更新成功：" + isUpdate);

        return loginResponse;
    }
}
