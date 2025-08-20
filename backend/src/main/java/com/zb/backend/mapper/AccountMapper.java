package com.zb.backend.mapper;

import com.zb.backend.entity.Account;
import com.zb.backend.model.response.LoginResponse;

public interface AccountMapper {
    // 通过账号ID获取用户
    Account selectAccountByAccountId(Long accountId);

    // 通过id获取员工信息，Account、Department、Employee三表联表查询
    LoginResponse getEmpLoginInfoByAccountId(Long accountId);

    // 通过id获取管理员信息
    LoginResponse getAdminLoginInfoByAccountId(Long accountId);

    // 更新最后登录时间
    Boolean updateLastLoginTime(Long accountId);

    // 查询是否为管理员
    Boolean isAdminByAccountId(Long accountId);

    // 新增账号
    Boolean insertAccount(String password);

    // 修改账号状态
    Boolean updateAccountStatus(Long accountId);
}
