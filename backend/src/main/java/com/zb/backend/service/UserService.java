package com.zb.backend.service;

import com.zb.backend.entity.User;
import com.zb.backend.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;

    public User login(User user) {
        User userInfo = userMapper.findByUser(user);
        System.out.println(userInfo);
        if (userInfo == null) {
            return null;
        }
        return userInfo;
    }

}
