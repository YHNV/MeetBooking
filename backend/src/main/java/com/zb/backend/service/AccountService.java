package com.zb.backend.service;

import com.zb.backend.constants.enums.AccountEnum;
import com.zb.backend.constants.enums.ResultEnum;
import com.zb.backend.entity.Account;
import com.zb.backend.mapper.AccountMapper;
import com.zb.backend.model.response.LoginResponse;
import com.zb.backend.util.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;

    // 根据id查询账号信心
    public Account getAccountByAccountId(Long accountId) {
        System.out.println("根据账号查询账号信息：" + accountId);
        return accountMapper.selectAccountByAccountId(accountId);
    }

    // 获取员工登录信息，联表查询
    public LoginResponse getEmpLoginInfo(Long accountId) {
        System.out.println("获取登录信息员工：" + accountId);
        return accountMapper.getEmpLoginInfoByAccountId(accountId);
    }

    // 获取管理员登录信息
    public LoginResponse getAdminLoginInfo(Long accountId) {
        System.out.println("获取登录信息管理员：" + accountId);
        return accountMapper.getAdminLoginInfoByAccountId(accountId);
    }

    // 更新最后登录时间
    public Boolean updateLastLoginTimeByAccountId(Long accountId) {
        System.out.println("更新最后登录时间：" + accountId);
        return accountMapper.updateLastLoginTime(accountId);
    }

    // 通过id判断是否为管理员
    public Boolean isAdminByAccountId(Long accountId) {
        return accountMapper.isAdminByAccountId(accountId);
    }

    // 新增账号
    public Boolean addAccount(String password) {
        return accountMapper.insertAccount(password);
    }

    // 修改账号状态
    public ResultEnum toggleAccountStatus(Long accountId) {
        Boolean updateAccountStatus = accountMapper.updateAccountStatus(accountId);

        if (!updateAccountStatus) {
            return AccountEnum.ERR_UPDATE_STATUS;
        }

        return AccountEnum.SUC_UPDATE_STATUS;
    }

    // 重置密码
    public ResultEnum resetPassword(Long accountId) {
        // 初始密码为123456，进行加密，调用修改密码方法
        String password = "123456";

        password = passwordEncoder.encode(password);

        Boolean resetPassword = accountMapper.updatePasswordByAccountId(accountId, password);
        if (!resetPassword) {
            return AccountEnum.ERR_RESET_PASSWORD;
        }

        return AccountEnum.SUC_RESET_PASSWORD;
    }
}
