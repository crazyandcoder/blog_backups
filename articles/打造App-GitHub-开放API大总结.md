---
title: 打造App-GitHub 开放API大总结
categories:
  - Android
tags:
  - 'Github'
  - 'API'
comments: true
date: 2019-12-19 19:00:58
img: https://img-blog.csdnimg.cn/20191219201844393.jpg
---


今天整理了一下GitHub 开放出来API，将它罗列出来，用这些API可获取GitHub上面的数据，返回格式为json，通过使用这些数据，可以用Flutter，ReactNative，Android，iOS等技术开发出这款GitHub客户端练练手，巩固所学知识，岂不是高效率学习一门新技术的手段？

这套API列表主要包含了一下几个方面的内容：

 1. 登陆认证信息
 2. All Activity的信息
 3. Trending的信息
 4. 个人用户信息
 5. Repos包含的所有信息

下面详细列出每个方面的API，并给出所调用链接及返回的json结果信息。完整的API及返回的结果请查看链接 [**awesome-github-api**](https://github.com/crazyandcoder/awesome-github-api)

## 1. 登陆
建议使用OAuth2认证登陆，安全。相关链接：[https://developer.github.com/v3/#authentication](https://developer.github.com/v3/#authentication)

## 2. 关于我的-API
我的页面所涉及到的API，包括：

 1. 我的主页
 2. 我的关注
 3. 我的仓库
 4. 我的粉丝


### 2.1 我的个人主页
#### 2.1.1 请求说明
| 说明    | URL                                                | 备注 |
| ------- | -------------------------------------------------- | ---- |
| 请求URL | https://api.github.com/user?access_token={token值} |      |
| 参数    | access_token                                       |

#### 2.1.2 返回响应
详细json结果请看链接  [我的个人信息json解析结果](https://github.com/crazyandcoder/awesome-github-api/blob/master/doc/%E6%88%91%E7%9A%84-API.md)

![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/12/19/16f1dcfe35fb5603?w=2080&h=1164&f=png&s=346885)


### 2.2 我的关注 Following
#### 2.2.1 请求说明
| 说明    | URL                                                         | 备注         |
| ------- | ----------------------------------------------------------- | ------------ |
| 请求URL | https://api.github.com/users/{user}/following               |              |
| 参数1   | page                                                        | int类型      |
| 参数2   | user                                                        | 某个用户     |
| 例如    | https://api.github.com/users/crazyandcoder/following?page=1 | 需要分页处理 |


#### 2.2.2 返回响应
备注：结果返回一个数组

详细json结果请看链接  [我的个人信息json解析结果](https://github.com/crazyandcoder/awesome-github-api/blob/master/doc/%E6%88%91%E7%9A%84-API.md)

![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/12/19/16f1dcfe361e4812?w=2078&h=1158&f=png&s=425988)


### 2.3 我的粉丝 Followers
#### 2.3.1 请求说明
| 说明    | URL                                                         | 备注              |
| ------- | ----------------------------------------------------------- | ----------------- |
| 请求URL | https://api.github.com/users/{user}/followers?page=1        | 支持分页          |
| 参数1   | page                                                        | int类型           |
| 参数2   | user                                                        | 我的用户loginName |
| 例如    | https://api.github.com/users/crazyandcoder/followers?page=1 | 需要分页处理      |


#### 2.3.2 返回响应

详细json结果请看链接  [我的个人信息json解析结果](https://github.com/crazyandcoder/awesome-github-api/blob/master/doc/%E6%88%91%E7%9A%84-API.md)

![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/12/19/16f1dcfe3651179c?w=2080&h=1158&f=png&s=425220)


### 2.4 我的仓库 Repositories
#### 2.4.1 请求说明
| 说明    | URL                                                                                                    | 备注                        |
| ------- | ------------------------------------------------------------------------------------------------------ | --------------------------- |
| 请求URL | https://api.github.com/user/repos?affiliation=owner&direction=asc&page=1&sort=full_name&visibility=all |                             |
| 参数1   | affiliation                                                                                            | 隶属关系，固定值owner       |
| 参数2   | direction                                                                                              | 排序方式，asc升序，desc降序 |
| 参数3   | page                                                                                                   | 分页                        |
| 参数4   |                                                                                                        | sort                        | 排序 |
| 参数5   | visibility                                                                                             |                             |

#### 2.4.2 返回响应

详细json结果请看链接  [我的个人信息json解析结果](https://github.com/crazyandcoder/awesome-github-api/blob/master/doc/%E6%88%91%E7%9A%84-API.md)

![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/12/19/16f1dcfe3673d7dd?w=2080&h=1160&f=png&s=530208)

## 3. 关于All activity-API
All activity所涉及到的API，包括：


### 3.1 All activity
#### 3.1.1 请求说明
| 说明    | URL                                                                                  | 备注 |
| ------- | ------------------------------------------------------------------------------------ | ---- |
| 请求URL | https://api.github.com/users/{user}/received_events?name={}&page=1                   |      |
| 参数1   | user                                                                                 |
| 参数2   | name                                                                                 |
| 参数3   | page                                                                                 |
| demo    | https://api.github.com/users/crazyandcoder/received_events?name=crazyandcoder&page=1 |      |

#### 3.1.2 返回响应
 [All-Activity返回的结果json](https://github.com/crazyandcoder/awesome-github-api/blob/master/doc/All-Activity%E8%BF%94%E5%9B%9E%E7%9A%84%E7%BB%93%E6%9E%9Cjson.md)

![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/12/19/16f1dcfe394d2731?w=2082&h=1158&f=png&s=221418)

## 4. 关于Trending-API
Trending所涉及到的API，包括：

 1. Trending-Repositories
 2. Trending-Developers

### 4.1 Trending-Repositories
#### 4.1.1 请求说明
| 说明    | URL                                                         | 备注                         |
| ------- | ----------------------------------------------------------- | ---------------------------- |
| 请求URL | https://github-trending-api.now.sh/repositories?since={}    |                              |
| 参数1   | since                                                       | 参考值有daily-weekly-monthly |
| demo    | https://github-trending-api.now.sh/repositories?since=daily |                              |

#### 4.1.2 返回响应
 [Trending-Repositories返回所有的结果json](https://github.com/crazyandcoder/awesome-github-api/blob/master/doc/Trending-Repositories%E8%BF%94%E5%9B%9E%E6%89%80%E6%9C%89%E7%9A%84%E7%BB%93%E6%9E%9Cjson.md)

![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/12/19/16f1dcfe39f41411?w=2078&h=1158&f=png&s=286193)

### 4.2 Trending-Developers
#### 4.2.1 请求说明
| 说明    | URL                                                       | 备注                         |
| ------- | --------------------------------------------------------- | ---------------------------- |
| 请求URL | https://github-trending-api.now.sh/developers?since={}    |                              |
| 参数1   | since                                                     | 参考值有daily-weekly-monthly |
| demo    | https://github-trending-api.now.sh/developers?since=daily |                              |


#### 4.2.2 返回响应
 [Trending-Developers返回所有的结果json](https://github.com/crazyandcoder/awesome-github-api/blob/master/doc/Trending-Developers%E8%BF%94%E5%9B%9E%E6%89%80%E6%9C%89%E7%9A%84%E7%BB%93%E6%9E%9Cjson.md)

![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/12/19/16f1dcfe7345441c?w=2078&h=1158&f=png&s=258014)

## 5. 关于User-Repos-API
User-Repos所涉及到的API，包括：

 1. User 某个用户的信息
 2. Repos 某个用户下某个仓库的信息

### 5.1 User
查询某个用户的信息
#### 5.1.1 请求说明
| 说明    | URL                                     | 备注   |
| ------- | --------------------------------------- | ------ |
| 请求URL | URL	https://api.github.com/users/{user} |        |
| 参数1   | user                                    | 用户名 |
| demo    | URL	https://api.github.com/users/arvidn |        |

#### 5.1.2 返回响应
 [User-Repos-API结果返回的json](https://github.com/crazyandcoder/awesome-github-api/blob/master/doc/User-Repos-API.md)

![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/12/19/16f1dcfe75d911c9?w=2078&h=1158&f=png&s=312893)

### 5.2 Repos
查询某个用户下的某个Repos信息
#### 5.2.1 请求说明
| 说明    | URL                                                          | 备注                 |
| ------- | ------------------------------------------------------------ | -------------------- |
| 请求URL | https://api.github.com/repos/{user}/{repos}                  |                      |
| 参数1   | user                                                         | 某个用户             |
| 参数2   | repos                                                        | 该用户下的某个仓库名 |
| demo    | https://api.github.com/repos/xingshaocheng/architect-awesome |                      |

![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/12/19/16f1dcfe78751457?w=2080&h=1158&f=png&s=466660)
#### 5.2.2 返回响应

[User-Repos-API结果返回的json](https://github.com/crazyandcoder/awesome-github-api/blob/master/doc/User-Repos-API.md)

## 6. 关于搜索-API
搜索所涉及到的API，包括：

 1. 搜索整个GitHub仓库内容
 

### 6.1 搜索
搜索整个GitHub仓库内容
#### 6.1.1 请求说明
| 说明    | URL                                                     | 备注       |
| ------- | ------------------------------------------------------- | ---------- |
| 请求URL | https://api.github.com/search/repositories?q={content}  |            |
| 参数1   | q                                                       | 搜索的内容 |
| demo    | https://api.github.com/search/repositories?q=citypicker |            |

#### 6.1.2 返回响应
[搜索返回所有内容json](https://github.com/crazyandcoder/awesome-github-api/blob/master/doc/%E6%90%9C%E7%B4%A2%E8%BF%94%E5%9B%9E%E6%89%80%E6%9C%89%E5%86%85%E5%AE%B9.md)

![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/12/19/16f1dcfe7cecf515?w=2078&h=1158&f=png&s=491155)

## 7. 总结


利用上面的API我们基本可以获取GitHub的全部数据，有了这些靠谱的网络数据，通过组织这些数据，可以使用RN，Flutter，Android，iOS等技术开发一款GitHub客户端，巩固我们所学知识，提升学习效率，这是一个很不错的途径。
