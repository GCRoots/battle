package com.battleonline.demo.pojo;

/**
 * @author shipengfei
 * @data 2020/2/14
 */
public class User {
    private String uuid;        //用户id
    private String username;    //用户名
    private String password;    //密码
    private String headImage;   //头像
    private String sender;      //登录地址，用于建立玩家间通信（预计用Map）

    //后续可能添加：战绩（胜负情况）、等级、好友等等


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    @Override
    public String toString() {
        return "User{" +
                "uuid='" + uuid + '\'' +
                ", password='" + password + '\'' +
                ", headImage='" + headImage + '\'' +
                ", sender='" + sender + '\'' +
                '}';
    }

}
