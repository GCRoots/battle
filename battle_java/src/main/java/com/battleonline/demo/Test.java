package com.battleonline.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.battleonline.demo.dao.redis.RedisUtil;
import com.battleonline.demo.pojo.User;
import com.battleonline.demo.service.UserService;
import com.battleonline.demo.service.imp.UserServiceImp;

/**
 * @author shipengfei
 * @data 2020/2/16
 */
public class Test {

    public static void main(String[] args) {
        String json="{\"a\":\"a\",\"b\":\"b\"}";
        JSONObject jsonObject= JSON.parseObject(json);
        System.out.println(jsonObject);
        System.out.println(jsonObject.get("a"));

    }
}
