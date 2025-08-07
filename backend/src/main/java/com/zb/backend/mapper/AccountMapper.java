package com.zb.backend.mapper;

import com.zb.backend.entity.Account;
import com.zb.backend.model.response.LoginResponse;

public interface AccountMapper {
    // 通过账号ID获取用户
    Account selectAccountByAccountId(Long accountId);

    LoginResponse getLoginInfoByAccountId(Long accountId);
}
