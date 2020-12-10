相关文章：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201105201305151.jpeg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqMTg4MjY2,size_16,color_FFFFFF,t_70#pic_center)

## 前言
在上一篇文章[《开源app-从0到1》](https://mp.weixin.qq.com/s?__biz=MzIwNjQ2NTc5OQ==&tempkey=MTA4Nl8zRWIxdXdPRXF4SHhuNUszMXptaW11VnlkLUxLNzRNR3M1ZS1uZjZYUXRYNHhZNGxHcHZlYWFYYjl3QzE4TTljOGUwQ3ptWEpjOHAxTGNNcmM1bkxPNXhUWjdReUo5NHpJM3BHQTR4REpUcG9Tb1ZrZ0o2NExzYWI5c2NBSEJXWVlObk1Mbi1mbFdQVUdFU0h6cU9GNXV1QS1RWm1WNUliN2JSR2tRfn4=&chksm=17207c9f2057f589c8bacb0c4b800ff9ed602818ed09c102a6b6420f236c7db21e8cec168bca&__mpa_temp_link_flag=1&token=1207947825#rd)中，我们预览了全工程所有的效果，本篇文章就详细记录一下如何运行整个项目，使其能够在自己的机器上面跑出效果来。为了能够准确的运行项目，使其顺利的跑起来，少走弯路，务必要紧跟我的步伐，按照我写的步骤一步一步来就能实现上述效果。



## 1. 开发工具
俗话说，工欲善其事，必先利其器，要想先开始，必须现将工具准备好了，下面列出本工程所需要的全部开发工具
### 1.1 MySql

> Mysql：v8.0.20 下载地址：[https://www.mysql.com/](https://www.mysql.com/)

Mysql用于存储我们从网络上爬取的各种数据。由于我们是运行在本地的，暂时不需要将项目部署到服务器，所以先下载Mysql，安装运行即可。
### 1.2 Navicat premium
Navicat 是Mysql的可视化工具，方便进行数据库mysql的各种操作，可以建立数据库，建表等操作，比命令行方便多了。

> Navicat 下载地址：[https://www.navicat.com.cn/download/navicat-for-mysql/](https://www.navicat.com.cn/download/navicat-for-mysql)

### 1.3 Postman
Postman 是一个很强大的API调试、Http请求的工具，用这个我们可以模拟get post请求，查看返回response，非常方便

> Postman 下载地址：[https://www.getpostman.com/](https://www.getpostman.com)

### 1.4 IntelliJ IDEA
IntelliJ IDEA，服务端开发工具，就好像Android开发工具AndroidStudio一样，用IntelliJ IDEA开发服务端非常方便，顺手。

> IntelliJ IDEA 下载地址：[https://www.jetbrains.com/idea/download/#section=mac](https://www.jetbrains.com/idea/download/#section=mac)

### 1.5 PyCharm
PyCharm 是用于编写python脚本非常方便。

> Navicat 下载地址：[https://www.jetbrains.com/](https://www.jetbrains.com/)

### 1.6 Android Studio 
这个不用说，搞Android的都用过。

## 2. 开始
好了，以上所有的工具都安装好了之后，我们接下来开始下面的步骤，下面就是正文了：

### 2.1 安装mysql
安装过程，Google一下，需要注意点就是设置用户名和密码还有端口号（一般3306就可以）
> **注意：安装mysql，需要设置用户名和密码和端口号，记得保存好，后面需要用到**

### 2.2 创建数据库
安装好了mysql，然后启动mysql，创建数据库，可以通过命令行或者navicat都可以，这里我们通过navicat创建数据库，首先需要新建一个connection，这里面需要用到我们安装mysql时设定的用户名，密码还有端口号等信息，我们新建了一个localhost的connection

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201105192402368.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqMTg4MjY2,size_16,color_FFFFFF,t_70#pic_center)

 接着需要在我们刚刚创建的localhost上面创建数据库：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201105192551960.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqMTg4MjY2,size_16,color_FFFFFF,t_70#pic_center)

创建数据库top_university：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201105192728806.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqMTg4MjY2,size_16,color_FFFFFF,t_70#pic_center)
创建好了数据库之后，双击刚刚创建的数据库top_university，然后右击，选择执行sql文件：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201105193524536.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqMTg4MjY2,size_16,color_FFFFFF,t_70#pic_center)

在弹出的对话框中选择sql文件：文件链接下面，注意该文件是压缩文件，由于超过了100M，所以压缩了上传了，记得下载下来解压即可。

>数据库文件路径： [**top_university.sql**](https://github.com/crazyandcoder/awesome-practise/blob/main/python/sql/top_university.sql.zip)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201105193610975.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqMTg4MjY2,size_16,color_FFFFFF,t_70#pic_center)
以上步骤便可创建了本地数据库服务，点击navicat中的数据库top_university，展开即可看见下面的表：
![在这里插入图片描述](https://img-blog.csdnimg.cn/2020110519500188.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqMTg4MjY2,size_16,color_FFFFFF,t_70#pic_center)
好了，到了这一步，我们数据其实就已经准备好了。接下来我们该运行服务端的代码了。


### 2.3 服务端运行

首先需要下载服务端的代码，代码地址为：，下载下来后用IntelliJ IDEA 打开，目录结构为：

> 服务端代码路径：**[Practise_Server_TopUniversity](https://github.com/crazyandcoder/awesome-practise/tree/main/server)**

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201105195403657.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqMTg4MjY2,size_16,color_FFFFFF,t_70#pic_center)
服务端代码只需要改两行代码即可运行，打开上面选中的文件：**[application.properties](https://github.com/crazyandcoder/awesome-practise/blob/main/server/Practise_Server_TopUniversity/src/main/resources/application.properties)**

改动其中的第10行和第11行代码，配置成你自己的数据库用户名和密码即可：

```
#本地
server.port=8080
spring.datasource.username=你的数据库用户名
spring.datasource.password=你的数据库密码
spring.datasource.url=jdbc:mysql://localhost:3306/top_university?useSSL=false&allowPublicKeyRetrieval=true&useTimezone=true&serverTimezone=GMT%2B8&characterEncoding=utf-8

```

接着就可以运行了，运行UniversityApplication这个类即可
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201105195922998.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqMTg4MjY2,size_16,color_FFFFFF,t_70#pic_center)
如果控制台打印了下面代码即可表示运行成功，好了，服务端代码就到这里，接着我们需要运行移动端代码
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201105200040967.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqMTg4MjY2,size_16,color_FFFFFF,t_70#pic_center)
### 2.4 移动端运行

用Android studio打开移动端代码，移动端代码只需要改动一行即可，打开app的build.gradle文件，找到 buildTypes ，修改其中的HOST地址为你本地的ip地址，端口号是8080，接着运行Android 工程即可。

> 移动端代码地址：[TopUniversity-Android](https://github.com/crazyandcoder/awesome-practise/tree/main/android)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201105200437685.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqMTg4MjY2,size_16,color_FFFFFF,t_70#pic_center)

好了，到了这里，整个项目的运行所需要的步骤都写完了，经过上面那几步，我们已经能够跑动整个项目了。后续的话，我会对每个端进行详细的讲解如何实现个端的实际业务开发。



![在这里插入图片描述](https://img-blog.csdnimg.cn/20201103210333759.gif#pic_center)


## 注意！！！
如果以上步骤有任何的问题的，都可以加qq群进行讨论解决，

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020110520092624.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqMTg4MjY2,size_16,color_FFFFFF,t_70#pic_center)


## 关于作者
专注于 Android 开发多年，喜欢写 blog 记录总结学习经验，blog 同步更新于本人的公众号，欢迎大家关注，一起交流学习～

![在这里插入图片描述](https://img-blog.csdnimg.cn/img_convert/edb471dd59ee15b43f54d78d9f4a7b4a.png)