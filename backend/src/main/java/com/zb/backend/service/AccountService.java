package com.zb.backend.service;

import com.zb.backend.entity.Account;
import com.zb.backend.mapper.AccountMapper;
import com.zb.backend.model.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountMapper accountMapper;

    public Account getAccountByAccountId(Long accountId) {
        System.out.println("根据账号查询账号信息：" + accountId);
        return accountMapper.selectAccountByAccountId(accountId);
    }

    public LoginResponse getEmpLoginInfo(Long accountId) {
        System.out.println("获取登录信息员工：" + accountId);
        return accountMapper.getEmpLoginInfoByAccountId(accountId);
    }

    public LoginResponse getAdminLoginInfo(Long accountId) {
        System.out.println("获取登录信息管理员：" + accountId);
        return accountMapper.getAdminLoginInfoByAccountId(accountId);
    }

    public Boolean updateLastLoginTimeByAccountId(Long accountId) {
        System.out.println("更新最后登录时间：" + accountId);
        return accountMapper.updateLastLoginTime(accountId);
    }
}
