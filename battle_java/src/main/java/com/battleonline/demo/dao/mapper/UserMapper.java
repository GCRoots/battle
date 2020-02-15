package com.battleonline.demo.dao.mapper;

import com.battleonline.demo.pojo.User;
import org.springframework.stereotype.Repository;


@Repository
public interface UserMapper {
    User findByUuid(String uuid);
    void insertUser(User user);
    void updateUser(User user);
    void delUser(String uuid);
}
