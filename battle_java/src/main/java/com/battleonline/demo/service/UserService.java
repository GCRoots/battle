package com.battleonline.demo.service;

import com.battleonline.demo.pojo.User;

public interface UserService {
    User findByUuid(String uuid);
    void insertUser(User user);
    void updateUser(User user);
    void delUser(String uuid);
}
