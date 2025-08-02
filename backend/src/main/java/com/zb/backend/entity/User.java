package com.zb.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    private Long id;
    private String username;    // 工号
    private String password;    // 密码

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
