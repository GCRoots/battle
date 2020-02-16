package com.battleonline.demo.service.imp;

import com.battleonline.demo.dao.mapper.UserMapper;
import com.battleonline.demo.pojo.User;
import com.battleonline.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImp implements UserService {

    @Autowired
    private UserMapper userMapper;


    @Override
    public User findByUuid(String uuid) {
        return userMapper.findByUuid(uuid);
    }

    @Override
    public void insertUser(User user) {
        userMapper.insertUser(user);
    }

    @Override
    public void updateUser(User user) {
        userMapper.updateUser(user);
    }

    @Override
    public void delUser(String uuid) {
        userMapper.delUser(uuid);
    }
}
