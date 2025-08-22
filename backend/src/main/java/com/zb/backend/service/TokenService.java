package com.zb.backend.service;

import com.zb.backend.entity.Token;
import com.zb.backend.mapper.TokenMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class TokenService {
    final TokenMapper tokenMapper;

    // 插入Token
    Boolean insertToken(Long accountId, String accountToken) {
        Token token = new Token();
        token.setAccountId(accountId);
        token.setToken(accountToken);
        token.setExpiryTime(Instant.now().plus(2, ChronoUnit.HOURS));
        return tokenMapper.insertToken(token);
    }

    // 删除过期Token
    int deleteExpiredTokens() {
        return tokenMapper.deleteExpiredTokens(Instant.now());
    }

    // 根据accountId删除Token
    int deleteTokenByAccountId(Long accountId) {
        return tokenMapper.deleteTokenByAccountId(accountId);
    }

    // 通过accountId查询Token
    public Token selectTokenByAccountId(Long accountId) {
        return tokenMapper.selectTokenByAccountId(accountId);
    }

    // 查询Token是否存在
    public Token selectByToken(String token) {
        return tokenMapper.selectByToken(token);
    }
}
