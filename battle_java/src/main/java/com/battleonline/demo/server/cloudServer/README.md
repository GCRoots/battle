#cloudServer

用来接收localServer推送过来的消息，并将其在处理过后推送给相应的localServer。

##

####CloudUdpServerHandler类：

用来处理本地端发来的不同数据。

- 注册 

  将注册数据写进数据库。
  
  数据格式： "Register;{\"uuid\":\"123\",\"password\":\"aaa\",\"username\":\"aaa\",\"headImage\":\"aaa\"}"

- 登录

  将登录信息与数据库信息相匹配，并在登录成功后将uuid与客户端IP地址：端口写入redis的在线用户表中。
  
  数据格式： "Login;{\"uuid\":\"123\",\"password\":\"aaa\"}"
  
- 退出(退出游戏大厅)

  退出登录，并将用户数据从在线用户中删去。
  
  数据格式： "Exit;{\"uuid\":\"123\"}"
  
- 查看

  查看在线用户，为下一步的游戏匹配做准备。用户列表从redis里拿到。
  
  数据格式： 
  
  ###### 需要做redis的分表查询吗？？？
  
- 匹配  

  ###### 未测试，等匹配完成后同步测试

  从查看到的在线用户列表中选择玩家进行游戏，并把双方地址信息等放入相应库中。
  
  数据格式： "Match;{\"uuid\":\"123\",\"another\":\"123\"}"
  
- 退出(退出游戏)

  退出游戏，将玩家从匹配列表删除。
  
  数据格式： "ExitGame;{\"uuid\":\"123\"}"
  
注：预计后续可能添加功能有：好友功能。

##

####Redis中的数据表：

- onlone_user_sender:在线用户信息表 

  uuid，sender

- onlone_user_username：在线用户信息表 

  uuid，username

- match_user:匹配成功，正在游戏用户信息表(相当于路由表)

  one_player,another_player
  