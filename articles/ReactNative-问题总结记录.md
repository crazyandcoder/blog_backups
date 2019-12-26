---
title: ReactNative 问题总结记录
categories:
  - RN
tags:
  - 'RN'
  - ''
comments: true
date: 2019-12-26 10:57:38
img: https://img-blog.csdnimg.cn/20191217164029280.jpg
---

## 问题1. 打包出现Gradle问题（2019-12-26更新）
### 1.1 问题描述

ReactNative在初始化一个项目时，运行打包命令出现以下问题：

```
//运行命令
react-native run-android

//问题
Deprecated Gradle features were used in this build, making it incompatible with Gradle 6.0.
```

### 1.2 解决方案
进入android目录下运行以下命令即可：

```
//运行下面命令
./gradlew assembleRelease
```
