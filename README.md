@@ -1,8 +1,2 @@
# NPush
使用Netty实现的服务端消息推送，Android端消息接收功能

各子项目功能说明如下：

1.IMClient：Android客户端工程

2.IMServer：服务端Web工程，负责监控连接和推送消息

3.PushIA：使用此工程生成jar文件，从而在其他服务端工程中调用IMServer的推送服务

4.PushClient：通过PushIA.jar，调用IMServer推送服务的demo

5.imdb.sql：MySQL数据库文件
