![在这里插入图片描述](https://img-blog.csdnimg.cn/20201117071955902.jpeg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqMTg4MjY2,size_16,color_FFFFFF,t_70#pic_center)




## 前言
开发一款app，最重要的一部分就是数据了，现在也是大数据时代，数据信息非常庞大，同时数据对app来说，是不可或缺的一部分。在我们这个app当中，由于是查询类app，因此数据采集非常重要，至关重要的组成部分，因此，本篇着重介绍如何通过爬虫来获取数据并存入数据库。

**全部代码见github仓库：[**awesome-practise**](https://github.com/crazyandcoder/awesome-practise/tree/main/python/university/venv/src)**
## 准备
数据来源：[中国教育在线](https://gkcx.eol.cn/)
爬虫脚本：python
数据库：Mysql
数据库可视化工具：Navicat

## 网站分析
首先我们打开高校网站：[中国教育在线](https://gkcx.eol.cn/)，

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201116195035783.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqMTg4MjY2,size_16,color_FFFFFF,t_70#pic_center)
当时我在看这个网页的源码时意外发现，该网站的开发人员竟然直接将高校数据以json的方式存储，没有任何其他的加密操作，真是简直了。。。

我们随便选择一个栏目，以[**查高校**](https://gkcx.eol.cn/school/search)为例子，点击顶部的[**查高校**](https://gkcx.eol.cn/school/search) tab，进入到二级页面，我们打开chrome的调试工具控制台，

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020111620060693.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqMTg4MjY2,size_16,color_FFFFFF,t_70#pic_center)
打卡XHR那一列，然后查看左侧的网络返回数据，我们会发现点击查高校请求的API接口就是左侧圈起来的那个，右侧是我们请求到的返回数据。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201116202401152.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqMTg4MjY2,size_16,color_FFFFFF,t_70#pic_center)



我们把左侧的请求URL ccopy出来，分析一下他的内容：

```java
https://api.eol.cn/gkcx/api/?access_token=&admissions=&central=&department=&dual_class=&f211=&f985=&is_dual_class=&keyword=&page=1&province_id=&request_type=1&school_type=&signsafe=&size=20&sort=view_total&type=&uri=apigkcx/api/school/hotlists
```

我们仔细分析一下上面的请求URL，应该很好理解，我们着重分析一些重要的请求参数：

```java

请求域名：  https://api.eol.cn/gkcx/api/?
请求的token：  access_token
是否是211:  f211
是否是985:  f985
分页查询：   page
关键字搜索：  keyword
省份id：    province_id
院校类别：  school_type
办学类型：   department
分页每次查询的大小：  size
。。。
```

至于这些请求参数的值是哪些，你可以对照着上面的筛选条件进行查看，你每点击一个对应的请求条件，下面的请求URL就会将参数的值带入，通过这种方式你就可以将数据请求下来。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201116201836307.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqMTg4MjY2,size_16,color_FFFFFF,t_70#pic_center)
另外需要注意的是，查高校是带分页的，所以要注意page这个参数的取值范围，同时要注意size
## python爬虫

上面那种方式是我们分析网页得到的数据，我们不可能通过手动复制的方式来获取这些数据，因此需要我们借助自动化脚本来获取这些数据。我们可以借助python这门语言，天然的处理数据最拿手。

使用python爬取网页的数据的话，我们需要将数据存储到mysql中，因此第一步就是在数据库中新建表，具体的操作方式可以Google一下，也可以查询前一篇我写好的文章来操作[《开源app-从0到1实现（二）项目运行》](https://mp.weixin.qq.com/s?__biz=MzIwNjQ2NTc5OQ==&tempkey=MTA4N19LYm5WV0NKVTA1RDJFUVNDTXBZMnVrTVc5Yjh3TlV0YmItbUV2RC1helN5bE02OEk3YWc0MWRMdms3MVVxQ3Vlcl95QWNRZFgyOUJYNXhrN1d6MmhwM0IxdkJseFdTMm5OM0M1cFdaY0l0UUl6VE43a0NXVVRScmlyQW13NzltQ1Fqd0J3QjhpU3dDU2dUeWhCblhjVGI3Y3BSVHJFWVRjeU9ZdWFBfn4=&chksm=17207d652057f47321250c515f7f719efa138fb94c1a4304ae0bcef6f2de015809822f5d45d3#rd)。假设我们已经在mysql中新建好了高校表：**top_university_school_list**

### 第一步连接mysql

```java
try:
    conn = pymysql.connect(
        # mysql本机连接 或者写127.0.0.1也可以连接远程数据库
        host="localhost",
        # 数据库用户名称
        user="数据库用户名称",
        # 密码
        passwd="数据库密码",
        # 连接的数据库名字
        db="top_university",
        # 端口
        port=3306
    )
except pymysql.Error as e:
    print("连接失败：%s" % e)

```

### 第二步发起请求
发起请求的话就要用到python的requests框架，python 详细用法自行Google

```java
    url = "https://api.eol.cn/gkcx/api/?access_token=&admissions=&central=&department=&dual_class=&f211=&f985=&is_dual_class=&keyword=&page=" + str(
        id) + "&province_id=&request_type=1&school_type=&signsafe=&size=20&sort=view_total&type=&uri=apigkcx/api/school/hotlists"
    infoes = json.loads(get_html(url))
    length = len(infoes['data']['item'])
```
### 第三步存mysql
获取到接口返回的数据后，我们通过json来解析，解析成功之后就需要进行数据库插入操作了：

```java
    for i in range(length):
        result = []
        try:
            name = infoes['data']['item'][i]['name']
            result.append(infoes['data']['item'][i]['address'])
            result.append(infoes['data']['item'][i]['belong'])
            result.append(infoes['data']['item'][i]['city_id'])
            result.append(infoes['data']['item'][i]['city_name'])
            result.append(infoes['data']['item'][i]['code_enroll'])
            result.append(infoes['data']['item'][i]['colleges_level'])
            result.append(infoes['data']['item'][i]['department'])
            result.append(infoes['data']['item'][i]['dual_class'])
            result.append(infoes['data']['item'][i]['dual_class_name'])
            result.append(infoes['data']['item'][i]['f211'])
            result.append(infoes['data']['item'][i]['f985'])
            result.append(infoes['data']['item'][i]['level'])
            result.append(infoes['data']['item'][i]['level_name'])
            result.append(infoes['data']['item'][i]['is_top'])
            result.append(infoes['data']['item'][i]['name'])
            result.append(infoes['data']['item'][i]['nature_name'])
            result.append(infoes['data']['item'][i]['nature'])
            result.append(infoes['data']['item'][i]['province_id'])
            result.append(infoes['data']['item'][i]['province_name'])
            result.append(infoes['data']['item'][i]['publish_id'])
            # result.append(infoes['data']['item'][i]['rank'])
            # result.append(infoes['data']['item'][i]['rank_type'])
            result.append(infoes['data']['item'][i]['school_id'])
            result.append(infoes['data']['item'][i]['school_type'])
            result.append(infoes['data']['item'][i]['type'])
            result.append(infoes['data']['item'][i]['type_name'])
            sql = "insert into top_university_school_list_temp(address,belong,city_id,city_name,code_enroll,colleges_level,department,dual_class,dual_class_name,f211,f985,level,level_name,is_top,name,nature_name,nature,province_id,province_name,publish_id,school_id,school_type,type,type_name)values(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)"
            cursor.execute(sql, (result))
            print("***********正在写入" + name)
        except Exception as e:
            print(e)
```

需要注意的是，高校数据很大，如果我们一个一个去爬取的话非常耗时，这个时候我们便可以开启多线程去同时爬取，提高效率。

### 第四步多线程爬取

```java
pool = Pool(5)
    origin_num = [x for x in range(1, 149)]
    # origin_num = [x for x in range(1, 2)]
    try:
        pool.map(insert_mysql, origin_num)
    except Exception as e:
        print(e)
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201117071328814.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqMTg4MjY2,size_16,color_FFFFFF,t_70#pic_center)

## **源码**

上面是部分比较重要的源代码，没有全部发出来，全部代码见仓库：[**awesome-practise**](https://github.com/crazyandcoder/awesome-practise/tree/main/python/university/venv/src)

以上便是通过python来爬取查高校的数据步骤，至于其他tab的数据，你可以类似的爬取，这里不做详细描述，如果有任何问题，可以联系作者咨询。



## 关于作者
专注于 Android 开发多年，喜欢写 blog 记录总结学习经验，blog 同步更新于本人的公众号，欢迎大家关注，一起交流学习～

![在这里插入图片描述](https://img-blog.csdnimg.cn/img_convert/edb471dd59ee15b43f54d78d9f4a7b4a.png)