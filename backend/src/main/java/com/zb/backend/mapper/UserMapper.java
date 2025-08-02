package com.zb.backend.mapper;

import com.zb.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User findByUser(User user);
}
