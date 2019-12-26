---
title: Android中存储目录详解
categories:
  - 'Android'
tags:
  - 'Android'
  - '存储目录'
comments: true
date: 2019-12-04 17:01:16
img: https://img-blog.csdnimg.cn/20191126161045450.jpg
---


## 前言
前两天因为开发一个app更新的功能，我将从服务器下载的apk文件放在了内部存储目录（测试手机为小米，路径为：data/user/0/packagename/files）下面，然后安装的时候一直安装不了，提示解析包出错。后来查询发现，安装apk是调用了PackageInstaller，没有相关权限，这个无法获取内部路径，所以会安装不了。借机也复习了一遍Android下面存储相关的知识点，特来总结一番。

## 存储分类
![](https://img-blog.csdnimg.cn/20191204141946706.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqMTg4MjY2,size_16,color_FFFFFF,t_70)
对于Android存储目录，我总结成一张思维导图，如果有需要原图的，请在我的**公众号后台**回复**“存储目录”**即可获取原图。上面这张图很清楚的展示了Android存储的目录，接下来我们详细分析每一个目录。
### 内部存储 
内部存储位于系统中很特殊的一个位置，对于设备中每一个安装的 App，系统都会在 **data/data/packagename/xxx** 自动创建与之对应的文件夹。如果你想将文件存储于内部存储中，那么文件默认只能被你的应用访问到，且一个应用所创建的所有文件都在和应用包名相同的目录下。也就是说应用创建于内部存储的文件，与这个应用是关联起来的。当一个应用卸载之后，内部存储中的这些文件也被删除。对于这个内部目录，用户是无法访问的，除非获取root权限。

```
String fileDir = this.getFilesDir().getAbsolutePath();
String cacheDir = this.getCacheDir().getAbsolutePath();
```
一般情况下，我们获取到的路径为**data/data/packagename/xxx**，小米手机下面打印出来的结果如下：
![](https://img-blog.csdnimg.cn/20191204145737427.png)

对于内部存储路径，我们一般通过以下两种方式获取，内部存储空间的获取都需要使用Context：
##### context.getFileDir()
对应内部存储的路径为: **data/data/packagename/files**，但是对于有的手机如：华为，小米等获取到的路径为：**data/user/0/packagename/files**

##### context.getCacheDir()
对应内部存储的路径为: **data/data/packagename/cache**，但是对于有的手机如：华为，小米等获取到的路径为：**data/user/0/packagename/cache**应用程序的缓存目录，该目录内的文件在设备内存不足时会优先被删除掉，所以存放在这里的文件是没有任何保障的，可能会随时丢掉。

### 外部存储
针对于外部存储比较容易混淆，因为在Android4.4以前，手机机身存储就叫内部存储，插入的SD卡就是外部存储，但是在Android4.4以后的话，就目前而言，现在的手机自带的存储就很大，现在Android10.0的话，有的手机能达到256G的存储，针对于这种情况，手机机身自带的存储也是外部存储，如果再插入SD卡的话也叫外部存储，因此对于外部存储分为两部分：SD卡和扩展卡内存

我们通过一段代码来获取手机的外部存储目录，我们用的测试手机是三星G4，带有插入SD卡的：
```
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            File[] files = getExternalFilesDirs(Environment.MEDIA_MOUNTED);
            for (File file : files) {
                Log.e("file_dir", file.getAbsolutePath());
            }
}
```

对于以上代码，打印的结果如下：
![](https://img-blog.csdnimg.cn/20191204153641809.png)
打印出两行目录，第一行目录是机身自带的外部存储目录，目录结构为：/storage/emulated/0/Android/data/packagename/files
第二行是存储卡的目录结构，路径为：/storage/extSdCard/Android/data/packagename/files

#### 扩展外部存储
此目录路径需要通过context来获取，同时在app卸载之后，这些文件也会被删除。类似于内部存储。

##### getExternalCacheDir()
对应外部存储路径:/storage/emulated/0/Android/data/packagename/cache

##### getExternalFilesDir(String type)
对应外部存储路径:/storage/emulated/0/Android/data/packagename/files


#### SD卡存储
SD卡里面的文件是可以被自由访问，即文件的数据对其他应用或者用户来说都是可以访问的，当应用被卸载之后，其卸载前创建的文件仍然保留。

对于SD卡上面的文件路径需要通过Environment获取，同时在获取前需要判断SD的状态：

> MEDIA_UNKNOWN   SD卡未知
> MEDIA_REMOVED    SD卡移除
> MEDIA_UNMOUNTED  SD卡未安装
> MEDIA_CHECKING   SD卡检查中，刚装上SD卡时
> MEDIA_NOFS            SD卡为空白或正在使用不受支持的文件系统
> MEDIA_MOUNTED   SD卡安装
> MEDIA_MOUNTED_READ_ONLY  SD卡安装但是只读
> MEDIA_SHARED   SD卡共享
> MEDIA_BAD_REMOVAL  SD卡移除错误
> MEDIA_UNMOUNTABLE  存在SD卡但是不能挂载，例如发生在介质损坏

```
 String externalStorageState = Environment.getExternalStorageState();
 if (externalStorageState.equals(Environment.MEDIA_MOUNTED)){
            //sd卡已经安装，可以进行相关文件操作
 }
```
##### getExternalStorageDirectory()
对应外部存储路径:/storage/emulated/0

##### getExternalStoragePublicDirectory(String type)
获取外部存储的共享文件夹路径如：

> DIRECTORY_MUSIC  音乐目录
DIRECTORY_PICTURES  图片目录
DIRECTORY_MOVIES  电影目录
DIRECTORY_DOWNLOADS  下载目录
DIRECTORY_DCIM   相机拍照或录像文件的存储目录
DIRECTORY_DOCUMENTS   文件文档目录

```
String externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath();
```

以上便是获取相机DCIM目录，对应获取的路径为:/storage/emulated/0/DCIM。


### 系统存储目录
##### getRootDirectory()
对应获取系统分区根路径:/system

##### getDataDirectory()
对应获取用户数据目录路径:/data

##### getDownloadCacheDirectory()
对应获取用户缓存目录路径:/cache

## 相关概念区别
### getFileDir()和getCacheDir()区别
这两个都位于内部存储目录/data/data/packagename/下面，位于同一级别，前者是file目录下面，后面是cache目录下。
![](https://img-blog.csdnimg.cn/20191204163259705.png)

### getFileDir()和getExternalFilesDir(String type)区别
前者位于内部存储目录/data/data/packagename/file下面，后者位于外部存储目录/storage/emulated/0/Android/data/packagename/files下面，它们都存在于应用包名下面，也就是说属于app应用的，所以当app卸载后，它们也会被删除的。

对于前面提到的app下载升级功能，我们从服务器端下载的app需要放到外部存储目录下面，而不是内部存储目录，因为内部存储目录的空间很小。另外我也做了相关测试，如果将apk放到内部存储目录file下面的话，安装时会出现问题，提示解析包出错。


### 清除数据和清除缓存的区别
在app中有清除数据和清除缓存这两个概念，那么这两者分别清除的是什么目录下面的数据呢？

#### 清除数据
清除数据清除的是保存在app中所有数据，就是上面提到的位于packagename下面的所有文件，包含内部存储(/data/data/packagename/)和外部存储(/storage/emulated/0/Android/data/packagename/)。当然除了SD卡上面的数据，SD卡上面的数据当app卸载之后还会存在的。

#### 清除缓存
缓存是程序运行时的临时存储空间，它可以存放从网络下载的临时图片，从用户的角度出发清除缓存对用户并没有太大的影响，但是清除缓存后用户再次使用该APP时，由于本地缓存已经被清理，所有的数据需要重新从网络上获取。为了在清除缓存的时候能够正常清除与应用相关的缓存，请将缓存文件存放在getCacheDir()或者 getExternalCacheDir()路径下。

![](https://img-blog.csdnimg.cn/20191204152033541.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqMTg4MjY2,size_16,color_FFFFFF,t_70)


以上便是Android系统中管存储目录的一些知识。



















