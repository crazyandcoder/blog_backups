
在开发过程中，我们或多或少的都会接触到Bitmap这个东西，用的不好的话就会出现OOM问题，同时，也会有压缩的需求，所以今天就来理一理关于Bitmap的一些内容。

### 关于Bitmap的Config的理解

A：透明度  R：红色  G：绿   B：蓝

```
/**
     * Possible bitmap configurations. A bitmap configuration describes
     * how pixels are stored. This affects the quality (color depth) as
     * well as the ability to display transparent/translucent colors.
     */
    public enum Config {
      
        ALPHA_8     (1),
 
        RGB_565     (3),
        
        @Deprecated
        ARGB_4444   (4),

         */
        ARGB_8888   (5);

    }
```

Bitmap.Config ARGB_4444：每个像素占四位，即A=4，R=4，G=4，B=4，那么一个像素点占4+4+4+4=16位 

Bitmap.Config ARGB_8888：每个像素占四位，即A=8，R=8，G=8，B=8，那么一个像素点占8+8+8+8=32位

Bitmap.Config RGB_565：每个像素占四位，即R=5，G=6，B=5，没有透明度，那么一个像素点占5+6+5=16位

Bitmap.Config ALPHA_8：每个像素占四位，只有透明度，没有颜色。

#### 内存计算

一张 1024 * 1024 像素，采用ARGB8888格式，一个像素32位，每个像素就是4字节，占有内存就是4M若采用RGB565，一个像素16位，每个像素就是2字节，占有内存就是2M。

Glide加载图片默认格式RGB565，Picasso为ARGB8888，默认情况下，Glide占用内存会比Picasso低，色彩不如Picasso鲜艳，自然清晰度就低。

通常我们优化Bitmap时，当需要做性能优化或者防止OOM（Out Of Memory），我们通常会使用Bitmap.Config.RGB_565这个配置，因为Bitmap.Config.ALPHA_8只有透明度，显示一般图片没有意义，Bitmap.Config.ARGB_4444显示图片不清楚，Bitmap.Config.ARGB_8888占用内存最多。

#### 图片加载
如果我们想要加载一张大图到内存中，如果不进行压缩的话，那么很显然就会出现OOM的崩溃，

![](http://img.blog.csdn.net/20161215111059132?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvbGlqaV94Yw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

譬如我们加载一张5440*3000的大图到手机上面，如果不进行压缩处理的话，那么就会出现OOM。

> 代码清单，

```
java.lang.OutOfMemoryError
android.graphics.BitmapFactory.nativeDecodeStream(Native Method)
android.graphics.BitmapFactory.decodeStreamInternal(BitmapFactory.java:703)
android.graphics.BitmapFactory.decodeStream(BitmapFactory.java:679)
android.graphics.BitmapFactory.decodeFile(BitmapFactory.java:446)
android.graphics.BitmapFactory.decodeFile(BitmapFactory.java:480)
com.example.ly.bitmapdemo.MainActivity.onCreate(MainActivity.java:21)
                                                                               
```

导致这种情况的发生的根据原因就是内存溢出，Android给每个APP的内存都是有限的，所以不能容忍这种情况的发生，所以我们就必须进行压缩一下。


压缩后的效果如下：

![](http://img.blog.csdn.net/20161215142228219?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvbGlqaV94Yw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

### 图片压缩
我们在上传一张图片到服务器时一般都会先进行压缩一下，这样不仅可以节省流量同时也可以节约上传的时候。最近碰到项目里碰到一个问题：头像上传时总是出现超时的问题，客户那边出现的频率非常高，而我的手机却基本没出现过，检查了一下原因，大概有两个：

 1. 客户的网速不是太好，3G速度不够，上传图片需要很长时间导致超时产生。
 2. 图片的体积很大，占了3、4M，没经过压缩直接上传，导致超时产生。

针对以上两种情况，主要对于第二种原因进行优化。总结来说就是将图片进行压缩再上传，经过一系列的操作，发现超时现象基本不会出现了，原先2M的图片，经过压缩只有50Kb不到，压缩率达到60%，效果很明显。


#### 原理分析
如何将一张大图压缩到100kb以下并且保持不失真的特性？这就需要用到下面这个类了。
> 主要运用BitmapFactory.Options

BitmapFactory.Options缩放图片主要用到inSample采样率

inSample = 1，采样后图片的宽高为原始宽高
inSample > 1，例如2，**宽高均为原图的宽高的1/2**

一个采用ARGB8888的1024 * 1024 的图片
inSample = 1，占用内存就 1024 * 1024 * 4 = 4M
inSample = 2，占用内存就 512 * 512 * 4 = 1M

BitmapFactory 给我们提供了一个解析图片大小的参数类 BitmapFactory.Options ，把这个类的对象的 inJustDecodeBounds 参数设置为 true，这样解析出来的 Bitmap 虽然是个 null，但是 options 中可以得到图片的宽和高以及图片的类型。得到了图片实际的宽和高之后我们就可以进行压缩设置了，主要是计算图片的采样率。

```
 // 第一次解析将inJustDecodeBounds设置为true,用以获取图片大小,并且不需要将Bitmap对象加载到内存中
 BitmapFactory.Options options = new BitmapFactory.Options();
 options.inJustDecodeBounds = true;
 BitmapFactory.decodeFile(filePath, options); // 第一次解析
```

接下来就需要进行选定压缩的采样率了。目前市场上的主流手机分辨率一般最低是720*1280了所以就按照此分辨率进行压缩


```
		//原始图片的宽度与720的比值，然后向上取整这里为8
		int wRatio = (int) Math.ceil(options.outWidth / (float) 720);
		//原始图片的高度与1280的比值，然后向上取整这里为3
        int hRatio = (int) Math.ceil(options.outHeight / (float) 1280);

		//获取采样率
        if (wRatio > 1 && hRatio > 1) {
            if (wRatio > hRatio) {
                options.inSampleSize = wRatio;
            } else {
                options.inSampleSize = hRatio;
            }
        }
```

经过上面这个采样率进行压缩后的宽和高肯定是小于720*1270的，我们计算的结果是：680*375

![](http://img.blog.csdn.net/20161215134326559?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvbGlqaV94Yw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)


我们来实际比较一下压缩结果：

**原先：5440*3000**
如果采用ARGB_8888模式的话，那么如果不压缩直接加载到内存的话，那么它将占：

5440/1024 * 3000/1024 *4 = 62.25M，不崩溃才怪呢~

**那么现在：680*375**
见证奇迹的时候，680/1024 * 375/1024 *4=0.9M 

两者一比较的话，那么效果还是比较明显的，相差大约64倍，所以还是可以的。当然了经过上面的压缩方法，我们将压缩后的图片上传到服务器的话，那么将会大大的减少流量同时也会减少上传超时的几率的。

当然了，如果还嫌大的话，我们可以进一步增加压缩的比例，可以设置成**480*800**，那么这样的话，质量肯定是有所下降的。



 
----------

**参考链接**

[1、http://www.jianshu.com/p/3950665e93e6](http://www.jianshu.com/p/3950665e93e6)

[2、http://www.jianshu.com/p/9aaed7310180](http://www.jianshu.com/p/9aaed7310180)



----------


**关于作者：**

[1. 简书 http://www.jianshu.com/users/18281bdb07ce/latest_articles](http://www.jianshu.com/users/18281bdb07ce/latest_articles)
 

[2. 博客 http://crazyandcoder.github.io/](http://crazyandcoder.github.io/)


[3. github https://github.com/crazyandcoder](https://github.com/crazyandcoder)

[4. 开源中国 https://my.oschina.net/crazyandcoder/blog](https://my.oschina.net/crazyandcoder/blog)

 
