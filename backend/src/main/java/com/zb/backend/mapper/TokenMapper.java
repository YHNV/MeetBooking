package com.zb.backend.mapper;

import com.zb.backend.entity.Token;

import java.time.Instant;

public interface TokenMapper {
    // 添加Token到黑名单表（登录时存进来）
    Boolean insertToken(Token token);

    // 删除过期Token（小于当前时间-2小时）
    int deleteExpiredTokens(Instant now);

    // 根据accountId删除Token
    int deleteTokenByAccountId(Long accountId);

    // 检查Token是否存在
    Token selectByToken(String token);

}
