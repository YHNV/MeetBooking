package com.zb.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// Jwt声明信息
public class JwtClaim {
    private Long accountId;
    private Boolean isAdmin;
}
