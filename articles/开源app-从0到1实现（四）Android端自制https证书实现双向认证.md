 ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201209160449518.jpeg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqMTg4MjY2,size_16,color_FFFFFF,t_70#pic_center)


## 相关文章
[1.开源app-从0到1实现（一）效果预览](https://mp.weixin.qq.com/s?__biz=MzIwNjQ2NTc5OQ==&tempkey=MTA4N19YOTJpd0dXV09RUWZtZUZ3TXBZMnVrTVc5Yjh3TlV0YmItbUV2RC1helN5bE02OEk3YWc0MWRMdms3M3VfNWs0ZFB6RUZ4R21neE9IODFqOEwzTl9ENHlGTkY2UGhTYndVMVRHcmZyNnZXTzJDNGdjYnVfRnMzZXhUWnl6SlpLU0Vvc3BIOHpQMTlBRmczOEx2ZFZucGhvUkFJb0xUSlJGRjBXN1Fnfn4=&chksm=17207c9f2057f589c8bacb0c4b800ff9ed602818ed09c102a6b6420f236c7db21e8cec168bca&__mpa_temp_link_flag=1&token=1235406301#rd)

[2.开源app-从0到1实现（二）项目运行](https://mp.weixin.qq.com/s?__biz=MzIwNjQ2NTc5OQ==&tempkey=MTA4N19LYm5WV0NKVTA1RDJFUVNDTXBZMnVrTVc5Yjh3TlV0YmItbUV2RC1helN5bE02OEk3YWc0MWRMdms3MVVxQ3Vlcl95QWNRZFgyOUJYNXhrN1d6MmhwM0IxdkJseFdTMm5OM0M1cFdaY0l0UUl6VE43a0NXVVRScmlyQW13NzltQ1Fqd0J3QjhpU3dDU2dUeWhCblhjVGI3Y3BSVHJFWVRjeU9ZdWFBfn4=&chksm=17207d652057f47321250c515f7f719efa138fb94c1a4304ae0bcef6f2de015809822f5d45d3#rd)

[3.开源app-从0到1实现（三）爬虫实现数据采集](https://mp.weixin.qq.com/s?__biz=MzIwNjQ2NTc5OQ==&tempkey=MTA4N19LYm5WV0NKVTA1RDJFUVNDTXBZMnVrTVc5Yjh3TlV0YmItbUV2RC1helN5bE02OEk3YWc0MWRMdms3MVVxQ3Vlcl95QWNRZFgyOUJYNXhrN1d6MmhwM0IxdkJseFdTMm5OM0M1cFdaY0l0UUl6VE43a0NXVVRScmlyQW13NzltQ1Fqd0J3QjhpU3dDU2dUeWhCblhjVGI3Y3BSVHJFWVRjeU9ZdWFBfn4=&chksm=17207d652057f47321250c515f7f719efa138fb94c1a4304ae0bcef6f2de015809822f5d45d3#rd)


## 1. 背景
前一阶段学习了服务端的知识，然后就写了一套接口，然后在阿里云上面租了一个服务器将服务部署到服务器上面，由于域名一直没有备案成功，所以只能通过 ip 的方式进行访问。通过 ip 的方式其实也没什么大不了的，前阶段工作中还简单实现了一套 HTTPDNS，原理其实也是通过 ip 直连的方式进行接口访问。本人是 Android 端开发，写了一套接口，然后就简单实现了一个 app，将前后端打通，练练手。心血来潮，在 Google 注册了一个开发者账号，将应用上传到 GooglePlay 市场，如果有兴趣的话，后期简单写一篇关于上传应用到 Googleplay 市场的文章。上传 GooglePlay 应用市场时出现了问题，我用 ip 直连，但是是 http 请求，不是 https 的请求。现阶段基本上都是 https 了，但是因为是个人写着玩，练练手的，也没那么多钱去买个正规的 CA 证书，所以就自己动手制作了一个个人证书，接下来将详细介绍，如何在 Android 端下和服务端实现自制证书的连接。
## 2. 基本概念
### 2.1 HTTP
HyperText Transfer Protocol，超文本传输协议，是互联网上使用最广泛的一种协议，所有 WWW 文件必须遵循的标准。HTTP 协议传输的数据都是未加密的，也就是明文的，因此使用 HTTP 协议传输隐私信息非常不安全。
### 2.2 HTTPS
Hyper Text Transfer Protocol over Secure Socket Layer，安全的超文本传输协议，Netscape 设计了 SSL(Secure Sockets Layer) 协议用于对 Http 协议传输的数据进行加密，保证会话过程中的安全性
简单来说，HTTPS 就是“安全版”的 HTTP, HTTPS = HTTP + SSL。HTTPS 相当于在应用层和 TCP 层之间加入了一个 SSL（或 TLS ），SSL 层对从应用层收到的数据进行加密。

SSL：（Secure Socket Layer，安全套接字层），为 Netscape 所研发，用以保障在 Internet 上数据传输之安全，利用数据加密( Encryption )技术，可确保数据在网络上之传输过程中不会被截取。它已被广泛地用于 Web 浏览器与服务器之间的身份认证和加密数据传输。SSL 协议位于 TCP/IP 协议与各种应用层协议之间，为数据通讯提供安全支持。

### 2.3 加密方式
#### 2.3.1 对称加密
对称加密是指双方持有相同的密钥进行通信，加密速度快，但是有一个安全问题，双方怎样获得相同的密钥？常见的对称加密算法有 DES、3DES、AES 等。
#### 2.3.2 非对称加密
非对称加密，又称为公开密钥加密，是为了解决对称加密中的安全问题而诞生，一个称为公开密钥 (public key) ，即公钥，另一个称为私钥(private key)，即私钥。但是它的加密速度相对于对称加密来说很慢。

1. 公钥是对外开放的，私钥是自己拥有的。
2. 公钥加密的数据，只能用私钥解密。
3. 私钥加密的数据，只能用公钥解密。

TLS/SSL 中使用了 RSA 非对称加密，对称加密以及 HASH 算法。RSA 算法基于一个十分简单的数论事实：将两个大素数相乘十分容易，但那时想要对其乘积进行因式分解却极其困难，因此可以将乘积公开作为加密密钥。

SSL 协议即用到了对称加密也用到了非对称加密(公钥加密)，在建立传输链路时，SSL 首先对对称加密的密钥使用公钥进行非对称加密，链路建立好之后，SSL 对传输内容使用对称加密。

1. 对称加密 
速度高，可加密内容较大，用来加密会话过程中的消息

2. 公钥加密 
加密速度较慢，但能提供更好的身份认证技术，用来加密对称加密的密钥

### 2.4 SSL 功能
#### 2.4.1 客户对服务器的身份认证:
SSL 服务器允许客户的浏览器使用标准的公钥加密技术和一些可靠的认证中心（CA）的证书，来确认服务器的合法性。
#### 2.4.2 服务器对客户的身份认证:
也可通过公钥技术和证书进行认证，也可通过用户名，password 来认证。
#### 2.4.3 建立服务器与客户之间安全的数据通道:
SSL 要求客户与服务器之间的所有发送的数据都被发送端加密、接收端解密，同时还检查数据的完整性。 SSL 协议位于 TCP/IP 协议与各种应用层协议之间，为数据通讯提供安全支持。
### 2.5 CA证书
CA 证书是由 CA（Certification Authority）机构发布的数字证书。其内容包含：电子签证机关的信息、公钥用户信息、公钥、签名和有效期。这里的公钥服务端的公钥，这里的签名是指：用hash散列函数计算公开的明文信息的信息摘要，然后采用 CA 的私钥对信息摘要进行加密，加密完的密文就是签名。 即：证书 =  公钥 + 签名 +申请者和颁发者的信息。 客户端中因为在操作系统中就预置了 CA 的公钥，所以支持解密签名
## 2.6 认证
### 2.6.1 单向认证
单向认证大体意思就是客户端只验证服务端的合法性。即服务器保存公钥证书和私钥两个文件，客户端获取这个证书里面的公钥生成私钥，服务器端用自己的私钥去解密这个客户端发过来的私钥，后续的 https 通信过程就用这个私钥进行加密。
![图片来源于网页](https://img-blog.csdnimg.cn/20201209111948667.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqMTg4MjY2,size_16,color_FFFFFF,t_70#pic_center)
### 2.6.2 双向认证
对于双向证书验证，也就是说，客户端持有服务端的公钥证书，并持有自己的私钥，服务端持有客户的公钥证书，并持有自己私钥，建立连接的时候，客户端利用服务端的公钥证书来验证服务器是否上是目标服务器；服务端利用客户端的公钥来验证客户端是否是目标客户端。 服务端给客户端发送数据时，需要将服务端的证书发给客户端验证，验证通过才运行发送数据，同样，客户端请求服务器数据时，也需要将自己的证书发给服务端验证，通过才允许执行请求。

![图片来源于网页](https://img-blog.csdnimg.cn/20201209152529233.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqMTg4MjY2,size_16,color_FFFFFF,t_70#pic_center)


## 3. 证书制作
上面简单的介绍了https通信过程中需要的一些概念，接下来进入实战操作，生成我们自己的证书。在生成证书的过程中，会涉及到证书一些格式，下面简单介绍一下：

 - JKS：数字证书库。JKS 里有 KeyEntry 和 CertEntry，在库里的每个 Entry 都是靠别名（alias）来识别的。
 -  P12：是 PKCS12 的缩写。同样是一个存储私钥的证书库，由 .jks 文件导出的，用户在 PC 平台安装，用于标示用户的身份。
 -  CER：俗称数字证书，目的就是用于存储公钥证书，任何人都可以获取这个文件 。
 -  BKS：由于 Android 平台不识别 .keystore 和 .jks 格式的证书库文件，因此 Android 平台引入一种的证书库格式，BKS。

 

 **注意：对于以下括号括起来的需要换成你自己的数据**
### 3.1 生成客户端 keystore

```java
keytool -genkeypair -alias (AAAAA) -keyalg RSA -validity (BBBBB) -keypass (CCCCC) -storepass (DDDDD) -keystore (EEEEE)
```

1. （AAAAA）别名，随便填写，参考值：client
2. （BBBBB）此处的需要填写证书有效期，最好时间长点，可以选择 25 年以上，不过单位是天，所以可以天 365*25=9125（天），参考值：9125
3. （CCCCC）密码，参考值：123456
4. （DDDDD）密码，参考值：123456
5. （EEEEE）keystore 保存的地址，参考值：/my/keystore/client.jks
### 3.2 生成服务器 keystore

```java
keytool -validity （AAAAA） -genkey -v -alias （BBBBB） -keyalg RSA -keystore （CCCCC） -dname （DDDDD） -storepass （EEEEE） -keypass （FFFFF）
```

1. （AAAAA）此处的需要填写证书有效期，最好时间长点，可以选择 25 年以上，不过单位是天，所以可以天 365*25=9125（天），参考值：9125
2. （BBBBB）别名，随便填写，参考值：server
3. （CCCCC）keystore保存的地址，参考值：/my/keystore/server.keystore
4. （DDDDD）参考值："CN=服务器ip地址,OU=android,O=android,L=Shanghai,ST=Shanghai,c=cn"
5. （EEEEE）密码，参考值：123456
6. （FFFFF）密码，参考值：123456
**其中需要注意的是第四点，CN 需要填写服务器 ip 地址。**

### 3.3 生成客户端证书库

```java
keytool -validity （AAAAA） -genkeypair -v -alias （BBBBB） -keyalg RSA -storetype PKCS12 -keystore （CCCCC） -dname （DDDDD） -storepass （EEEEE） -keypass （FFFFF）
```

1. （AAAAA）此处的需要填写证书有效期，最好时间长点，可以选择 25 年以上，不过单位是天，所以可以天 365*25=9125（天），参考值：9125
2. （BBBBB）客户端别名，参考值：client
3. （CCCCC）P12存放地址，参考值：/my/keystore/client.p12
4. （DDDDD）参考值："CN=client,OU=android,O=android,L=Shanghai,ST=Shanghai,c=cn"
5. （EEEEE）密码，参考值：123456
6. （FFFFF）密码，参考值：123456
### 3.4 从客户端证书库中导出客户端证书

```java
keytool -export -v -alias (AAAAA ) -keystore （BBBBB） -storetype PKCS12 -storepass （CCCCC） -rfc -file （DDDDD）
```

1. （AAAAA）参考值：client
2. （BBBBB）参考值：/my/keystore/client.p12 
3. （CCCCC）参考值：123456
4. （DDDDD）参考值：/my/keystore/client.cer
### 3.5 从服务器证书库中导出服务器证书

```java
keytool -export -v -alias （AAAAA） -keystore （BBBBB） -storepass （CCCCC） -rfc -file （DDDDD）
```

1. （AAAAA）参考值：server
2. （BBBBB）参考值：/my/keystore/server.keystore
3. （CCCCC）参考值：123456
4. （DDDDD）参考值：/my/keystore/server.cer
### 3.6 生成客户端信任证书库(由服务端证书生成的证书库)

```java
 keytool -import -v -alias （AAAAA） -file （BBBBB） -keystore （CCCCC） -storepass （DDDDD） -storetype BKS -provider org.bouncycastle.jce.provider.BouncyCastleProvider -providerpath （EEEEE）
```

1. （AAAAA）参考值：server
2. （BBBBB）参考值：/my/keystore/server.cer
3. （CCCCC）参考值：/my/keystore/truststore.jks
4. （DDDDD）参考值：123456
5. （EEEEEE）参考值：/my/keystore/bcprov-ext-jdk15on-166.jar，下载地址（jar地址）
### 3.7 将客户端证书导入到服务器证书库(使得服务器信任客户端证书)

```java
keytool -import -v -alias (AAAAA) -file (BBBBB) -keystore (CCCCC) -storepass (DDDDD)
```

1. （AAAAA）参考值：client
2. （BBBBB）参考值：/my/keystore/client.cer
3. （CCCCC）参考值：/my/keystore/server.keystore
4. （DDDDD）参考值：123456
### 3.8 查询证书库中的全部证书

```java
keytool -list -keystore (AAAAA) -storepass （BBBBBB）
```

1. （AAAAA）参考值：/my/keystore/server.keystore
2. （BBBBB）参考值：123456

通过第三步，我们便生成了我们需要的证书，接着就在实际项目中使用我们自制的证书了，本期提供 Java 服务端的和移动 Android 端的代码集成。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201209154651962.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqMTg4MjY2,size_16,color_FFFFFF,t_70#pic_center)


## 4. 实际使用
### 4.1 生成BKS文件
Java 平台默认识别 jks 格式的证书文件，但是 android 平台只识别 bks 格式的证书文件。因此需要将上面生成的 jks 文件转成 bks 格式的，使用工具通过 portecle 来执行。portecle 下载地址：[portecle](https://download.csdn.net/download/lj188266/13507712)

转换步骤：
1. 下载上述的 jar 包放到桌面上
2. 运行这个 jar 文件，执行语句：java -jar /desktop/protecle.jar,此时会弹出 portecle 工具
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201209144852507.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqMTg4MjY2,size_16,color_FFFFFF,t_70#pic_center)
3. 运行 protecle.jar 将 client.jks 和 truststore.jks 分别转换成 client.bks 和 truststore.bks ,然后放到 android 客户端的 assert 目录下

```java
1.点击File
2.选择open Keystore File
3.选择上面的client.jks
4.输入密码（123456）
5.选择Tools
6.选择change keystore type
7.选择BKS
8.选择save keystore as
9.选择保存即可

重复上述步骤，生成truststore.bks文件
```

### 4.2 Android 端
目前 Android 使用的主流网络框架一般是 Okhttp 或者 Retrofit，不管是哪种网络请求框架，它们都提供了一个设置入口，我们以 OkHttp 为例：
#### 4.2.1 创建 assert 目录
在 Android 工程下面创建一个 assert 目录，然后将上述生成的 client.bks 和 truststore.bks 文件放进去
#### 4.2.2 创建自定义 SSLSocketFactory

```java
public class SSLHelper {
    private static String CLIENT_PRI_KEY = "client.bks";
    private static String TRUSTSTORE_PUB_KEY = "truststore.bks";
    private static String CLIENT_BKS_PASSWORD = "123456";
    private static String TRUSTSTORE_BKS_PASSWORD = "123456";
    private final static String KEYSTORE_TYPE = "BKS";
    private final static String PROTOCOL_TYPE = "TLS";
    private final static String CERTIFICATE_FORMAT = "X509";
    public static SSLSocketFactory getSSLCertifcation(Context context) {
        SSLSocketFactory sslSocketFactory = null;
        try {
            // 服务器端需要验证的客户端证书，其实就是客户端的keystore
            KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
            // 客户端信任的服务器端证书
            KeyStore trustStore = KeyStore.getInstance(KEYSTORE_TYPE);
            //读取证书
            InputStream ksIn = context.getAssets().open(CLIENT_PRI_KEY);
            InputStream tsIn = context.getAssets().open(TRUSTSTORE_PUB_KEY);
            //加载证书
            keyStore.load(ksIn, CLIENT_BKS_PASSWORD.toCharArray());
            trustStore.load(tsIn, TRUSTSTORE_BKS_PASSWORD.toCharArray());
            ksIn.close();
            tsIn.close();
            SSLContext sslContext = SSLContext.getInstance(PROTOCOL_TYPE);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(CERTIFICATE_FORMAT);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(CERTIFICATE_FORMAT);
            trustManagerFactory.init(trustStore);
            keyManagerFactory.init(keyStore, CLIENT_BKS_PASSWORD.toCharArray());
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sslSocketFactory;
    }
}
```

上述代码就是读取 assert 目录下面的bks文件，然后设置到 OkHttp 中去。

```java
  //OkHttp
  OkHttpClient okHttpClient = new OkHttpClient.Builder()
  //获取SSLSocketFactory
  .sslSocketFactory(SSLHelper.getSSLCertifcation(context))
  //添加hostName验证器
  .hostnameVerifier(new UnSafeHostnameVerifier())
  .build();
  //Retrofit
   Retrofit retrofit = new Retrofit.Builder()
   .baseUrl("baseUrl")
   .addConverterFactory(GsonConverterFactory.create())
   .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
   .client(okHttpClient)
   .build();
```


以上便是 Android 端的设置步骤。接着我们设置服务端的配置。
### 4.3 Java 服务端
服务端配置非常简单，只是简单的配置项即可。将第三步生成的 server.keystore 放到 resources 目录下面即可。然后在application.properties 这个配置文件中加入以下代码即可：

```java
server.ssl.enabled=true
server.ssl.key-store=classpath:server.keystore
server.ssl.key-store-password=123456
server.ssl.key-alias=server
server.ssl.keyStoreType=JKS
server.ssl.trust-store=classpath:server.keystore
server.ssl.trust-store-password=123456
server.ssl.client-auth=need
server.ssl.trust-store-type=JKS
server.ssl.trust-store-provider=SUN
```

## 5. 总结
经过以上所有的步骤，我们便可以在自己的项目中使用自制的 https 证书了，以前是通过 http 访问的，现在是通过 https 访问，大大增强了数据传输的安全性。


## 关于作者
专注于 Android 开发多年，喜欢写 blog 记录总结学习经验，blog 同步更新于本人的公众号，欢迎大家关注，一起交流学习～

![在这里插入图片描述](https://img-blog.csdnimg.cn/img_convert/edb471dd59ee15b43f54d78d9f4a7b4a.png)