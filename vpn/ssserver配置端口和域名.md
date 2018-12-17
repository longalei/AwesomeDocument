ssserver上如何修改端口号以及密码？

> 主要的流程大概如下：

- [x] 登录ssserver链接远程服务器

  > 重新修改密码命令：
  >
  > ```
  > passwd
  > ```

- [x] 找到ssserver的配置文件

  > 查找配置文件命令：
  >
  > ```
  > vim /etc/shadowsocks.json
  > 其中可以对配置文件进行修改，移动光标按i进行修改
  > ```

- [x] 修改后进行保存

  > 执行保存操作退出命令：
  >
  > ```
  > 按ESC键，输入：":wq"进行输入保存，直接退出：“:q”,强制退出：“:q!”
  > ```
  >
  >

- [x]  查找运行程序的进程号

  > 查找进程号并且结束此运行程序的进程。

       ```
  ps -ef|grep ssserver
  查找到对应的进程号时结束此进程。
  kill -9 pid(进程号)
       ```

- [x] 重新启动ssserver

  > 重新启动
  >
  > ```
  > ssserver -c /etc/shadowsocks.json 在前台启动
  > ssserver -c /etc/shadowsocks.json -d start 在后台启动
  > ```
  >
  >

- [x] 退出

  > 退出命令
  >
  > ```
  > exit
  > ```
  >
  > 可安装Xshell客户端或其它来进行对阿里云的网页端的操作。