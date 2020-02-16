package com.battleonline.demo;

import com.battleonline.demo.pojo.User;
import com.battleonline.demo.service.UserService;
import com.battleonline.demo.service.imp.UserServiceImp;

/**
 * @author shipengfei
 * @data 2020/2/16
 */
public class Test {

    public static void main(String[] args) {
        UserService userService=new UserServiceImp();

        User user=new User();
        user.setUuid("123345");
        user.setPassword("123789456");
        user.setUsername("aaa");
        user.setHeadImage("aa");
        user.setSender("192.168.1.1");

        userService.insertUser(user);
    }
}
