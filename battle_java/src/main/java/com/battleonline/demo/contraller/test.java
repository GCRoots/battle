package com.battleonline.demo.contraller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.battleonline.demo.pojo.User;
import com.battleonline.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

/**
 * @author shipengfei
 * @data 2020/2/16
 */
public class test {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/test/view",method = RequestMethod.POST)
    public void view(@RequestBody JSONObject json) throws IOException {
        System.out.println(json);
        User user=new User();
        user.setUuid("123345");
        user.setPassword("123789456");
        user.setUsername("aaa");
        user.setHeadImage("aa");
        user.setSender("192.168.1.1");

        userService.insertUser(user);
    }
}
