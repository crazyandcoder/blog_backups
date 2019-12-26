---
title: Android编译8.0源码
categories:
  - 'Android'
tags:
  - '源码编译'
  - ''
comments: true
date: 2018-11-23 14:06:20
img: https://img-blog.csdnimg.cn/20191126161045450.jpg
---
 
### 前言
搞Android的人不编译一下Android的源码感觉人生好像不大完整似的。早就想编译Android源码，但是前前后后经历好长时间都没有把它搞出来，就这样拖着，直到昨天晚上才真正的把Android8.0源码给编译出来。一开始在Mac下面编译的，但是环境搭建配置非常麻烦，而且问题特别多，编译到最后一步时出现问题，Google了好几天都没有解决，最后转战Ubuntu，一次性编译成功。来欣赏一下编译成果！

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191129140101214.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqMTg4MjY2,size_16,color_FFFFFF,t_70)

### 编译环境

```
1、Ubuntu16.04
2、Android8.0源码
```

对于Ubuntu16.04系统，有的人认为虚拟机里面安装也可以，我没有试过，因为电脑配置不够，我怕跑不起来，所以直接用了公司的Windows7台式机安装了Ubuntu、windows双系统，所以建议还是在原生的Ubuntu系统里面进行编译，防止出现各种诡异的问题。

### 配置环境

```
Ubuntu16.04系统安装
```

对于Ubuntu16.04系统，我是是通过u盘来安装的，方便快捷，这里就不说怎么安装系统了，不会的话建议百度。

```
Android8.0源码下载
```


Android8.0源码的下载就比较蛋疼了，因为我们只有通过VPN才能访问Google的一些东西，对于源码这东西，动不动几十个G的话，通过VPN来下载的话，肯定懵逼啊，所以我们得另辟途径。[清华大学开源软件镜像站](https://mirrors.tuna.tsinghua.edu.cn/help/AOSP/)，这个站点就提供Google的Android源码下载，最好使用里面的[每月更新的初始化包](https://mirrors.tuna.tsinghua.edu.cn/aosp-monthly/aosp-latest.tar)来下载，我是通过迅雷下载的，初始化包大概有42G，用迅雷，我这边大概花了3个小时就下载好了。这里说个题外话。我一开始是在Ubuntu里面的Firefox直接下载源码，大概试了三四次，都没有成功，也不知道为什么，前两次都是下载到20G左右的时候，就停了，不再继续下载，第三次下载到40G左右时停掉了，莫名其妙，也不能断点下载，所以换了个方式，直接在Mac里面迅雷下载好了，再传到Ubuntu中，太耗时间了！！！

下载好了初始化包，我们把它放到一个文件夹下面，随便建一个目录即可，然后解压，解压下来你会发现里面什么都没有，是个空的，又是一个大大的懵逼！！！，其实里面存在一个隐藏的文件.repo，我们通过这个文件在sync一下就可以了。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191129140220285.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqMTg4MjY2,size_16,color_FFFFFF,t_70)

### 下载 repo 工具

```
mkdir ~/bin
PATH=~/bin:$PATH
curl https://storage.googleapis.com/git-repo-downloads/repo > ~/bin/repo
chmod a+x ~/bin/repo
```

可能有些某种原因，所以自备梯子。


```
wget https://mirrors.tuna.tsinghua.edu.cn/aosp-monthly/aosp-latest.tar # 下载初始化包
tar xf aosp-latest.tar
cd AOSP   # 解压得到的 AOSP 工程目录
```
这时 ls 的话什么也看不到，因为只有一个隐藏的 .repo 目录

```
repo sync # 正常同步一遍即可得到完整目录
或 repo sync -l 仅checkout代码
```


这是清华大学开源软件镜像站提供的方法，按照这个做，没错，只不过耗时而已，看个人的网速。当然了也可以下载指定的Android版本源码，具体方法，请查看介绍： Android 镜像使用帮助

首先我们需要安装一些依赖：

```
sudo apt-get install -y git flex bison gperf build-essential libncurses5-dev:i386 
sudo apt-get install libx11-dev:i386 libreadline6-dev:i386 libgl1-mesa-dev g++-multilib 
sudo apt-get install tofrodos python-markdown libxml2-utils xsltproc zlib1g-dev:i386 
sudo apt-get install dpkg-dev libsdl1.2-dev libesd0-dev
sudo apt-get install git-core gnupg flex bison gperf build-essential  
sudo apt-get install zip curl zlib1g-dev gcc-multilib g++-multilib 
sudo apt-get install libc6-dev-i386 
sudo apt-get install lib32ncurses5-dev x11proto-core-dev libx11-dev 
sudo apt-get install lib32z-dev ccache
sudo apt-get install libgl1-mesa-dev libxml2-utils xsltproc unzip m4
```

设置编译器高速缓存

```
prebuilts/misc/linux-x86/ccache/ccache -M 50G
```

安装openJDK8

```
sudo apt-get install openjdk-8-jdk
```

接着导入编译Android源码所需的环境变量和其它参数:

```
source build/envsetup.sh
```

使用 lunch 选择要编译的目标。确切的配置可作为参数进行传递

```
lunch aosp_arm-eng
```

编译代码

```
make -j4
```

通过以上几步，我们就可以耐心的等待编译了，我这边是下班的时候开始编译的，等到第二天早上过来的时候，就提示已经编译成功，我也没想到一次性编译通过，毕竟在Mac上面编译了一个多月，基本都是在1%的时候就会报错，然后就一直Google，奈何一直找不到解决方法，就换成Ubuntu了，没想到一次性编译通过。

我们编译Android源码的主要目的还是查看分析Android源码，所以接下来我们得生成AndroidStudio查看源码的格式。

以上若已经编译成功的话，我们进入到Android源码的路径下面，首先我们需要生成AndroidStudio所需要的格式

```
mmm development/tools/idegen/
```

执行成功之后再次执行


```
sh ./development/tools/idegen/idegen.sh
```

通过以上两条命令的话，就可以生产AS 所需要的文件了android.ipr，进入Android源码根目录，打开AndroidStudio，导入这个文件即可，初次导入，时间比较久。

以上便是大概的过程，我大概经历了一个多月的时间才真正的编译成功，这期间放弃、继续、放弃、继续，来回挣扎，所以如果真想编译Android源码的话，建议，耐心一点，出现问题，找Google，换种思路继续，坚持到最后应该就可以了编出来了。














