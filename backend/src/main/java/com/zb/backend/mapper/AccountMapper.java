package com.zb.backend.mapper;

import com.zb.backend.entity.Account;
import com.zb.backend.model.response.LoginResponse;

public interface AccountMapper {
    // 通过账号ID获取用户
    Account selectAccountByAccountId(Long accountId);

    LoginResponse getEmpLoginInfoByAccountId(Long accountId);

    LoginResponse getAdminLoginInfoByAccountId(Long accountId);

    Boolean updateLastLoginTime(Long accountId);
}
