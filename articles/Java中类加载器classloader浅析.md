---
title: Java中类加载器classloader浅析
categories:
  - Java
tags:
  - 'Java'
  - '类加载器'
comments: true
date: 2019-11-27 10:30:42
img: https://img-blog.csdnimg.cn/20191127154127440.jpeg
---

本篇文章来简析一下 classloader 在 Java 中的应用。
##  前言
现在一般一个应用程序开发会包含很多很多的类， Java 程序启动时并不是一次性将所有的类全部加载到内存中进行运行的，而是先加载部分的类到 JVM 中，然后等 JVM 需要用到其他的类时再加载进去，这样的好处就是节约内存，提高了效率。

在 Java 中类加载器就是 ClassLoader ， ClassLoader 的具体作用就是将 class 文件加载到 jvm 虚拟机中去，程序就可以正确运行了。

### Class 再认识
我们平常写的 Java 文件的格式是 xxx.java 文件格式的，这个格式并不是 JVM 执行的格式， JVM 执行的是 .class 格式的文件，这就需要将 .java 的格式文件转为 .class 的格式，这就是编译过程。命令是：

```
javac HelloWorld.java
```
通过 javac 命令即可在当前目录下面生成 .class 文件，这个文件就是 JVM 能够执行的文件格式。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191127101342437.png)

接着通过 java 命令即可运行这个文件：
```
java HelloWorld
```
打印结果如下所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/2019112710140486.png)
以上便是一个完整的 Java 文件运行过程，先经过编译将 .java 文件转为 .class 格式以便 JVM能够执行。接下来我们来详细分析。


## Java 类加载器
### ClassLoader 分类
每个 ClassLoader 对象都是一个 java.lang.ClassLoader 的实例。每个Class对象都被这些 ClassLoader 对象所加载，通过继承java.lang.ClassLoader 可以扩展出自定义 ClassLoader，并使用这些自定义的 ClassLoader 对类进行加载。

```
package java.lang;
public abstract class ClassLoader {
	 public Class loadClass(String name);
	 protected Class defineClass(byte[] b);
	 public URL getResource(String name);
	 public Enumeration getResources(String name);
	 public ClassLoader getParent();
	 Class<?> findClass(String name)
     //...
}
```


### 1，loadClass
它接受一个全类名，然后返回一个 Class 类型的实例。

### 2，defineClass
方法接受一组字节，然后将其具体化为一个 Class 类型实例，它一般从磁盘上加载一个文件，然后将文件的字节传递给 JVM ，通过 JVM （ native 方法）对于 Class 的定义，将其具体化，实例化为一个Class 类型实例。

### 3，getParent
返回其parent ClassLoader

我们通过实际demo来测试一下。

```
package demo;
public class Test {
    public static void main(String[] args) {
        ClassLoader classLoader = Test.class.getClassLoader();
        System.out.println(classLoader);
        ClassLoader classLoader1 = classLoader.getParent();
        System.out.println(classLoader1);
        ClassLoader classLoader2 = classLoader1.getParent();
        System.out.println(classLoader2);
    }
}
```

打印结果如下：

```
sun.misc.Launcher$AppClassLoader@135fbaa4
sun.misc.Launcher$ExtClassLoader@2503dbd3
null
```


在 Java 中提供了以下三种类加载 ClassLoader：

 1. Bootstrp loader
 2. ExtClassLoader
 3. AppClassLoader

其实上面的 demo 打印出来的结果就验证了这三种 ClassLoader。

**(1): 根类加载器(null)**

它是由本地代码(c/c++)实现的，你根本拿不到他的引用，但是他实际存在，并且加载一些重要的类，它加载(%JAVA_HOME%\jre\lib),如rt.jar(runtime)、i18n.jar等，这些是Java的核心类。

**(2): 扩展类加载器(ExtClassLoader)**

虽说能拿到，但是我们在实践中很少用到它，它主要加载扩展目录下的jar包， %JAVA_HOME%\lib\ext

**(3): 应用类加载器(AppClassLoader)**

它主要加载我们应用程序中的类，如Test,或者用到的第三方包,如jdbc驱动包等。

这里的父类加载器与类中继承概念要区分，它们在class定义上是没有父子关系的。

### 类加载器调用顺序
同样的，在 Java 中的 ClassLoader 存在一种调用的顺序，我们就以上面的 Test.java 类进行解析。

当 Test.class 要进行加载时，它将会启动应用类加载器进行加载Test类，但是这个应用类加载器不会真正去加载他，而是会调用看是否有父加载器，结果有，是扩展类加载器，扩展类加载器也不会直接去加载，它看自己是否有父加载器没，结果它还是有的，是根类加载器。

所以这个时候根类加载器就去加载这个类，可在%JAVA_HOME%\jre\lib 下，它找不到 demo.Test 这个类，所以他告诉他的子类加载器，我找不到，你去加载吧，子类扩展类加载器去 %JAVA_HOME%\lib\ext 去找，也找不着，它告诉它的子类加载器 AppClassLoader，我找不到这个类，你去加载吧，结果AppClassLoader 找到了，就加到内存中，并生成 Class 对象。

借用网上的一张图来表示流程。
![在这里插入图片描述](https://img-blog.csdnimg.cn/2019112710175892.jpeg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqMTg4MjY2,size_16,color_FFFFFF,t_70)

这张图很明显的展示了 ClassLoader 的执行顺序流程。

### 双亲委托
双亲委托即“类装载器有载入类的需求时，会先请示其 Parent 使用其搜索路径帮忙载入，如果 Parent 找不到,那么才由自己依照自己的搜索路径搜索类”，上面那张图就显示了该流程。

我们还是使用上面的代码进行说明：

```
package demo;
public class Test {
    public static void main(String[] args) {
    
        ClassLoader classLoader = Test.class.getClassLoader();
        System.out.println(classLoader);
        ClassLoader classLoader1 = classLoader.getParent();
        System.out.println(classLoader1);
        ClassLoader classLoader2 = classLoader1.getParent();
        System.out.println(classLoader2);
    }
}
```


打印结果如下：

```
sun.misc.Launcher$AppClassLoader@135fbaa4
sun.misc.Launcher$ExtClassLoader@2503dbd3
null
```
打印结果可以看出，Test是由 AppClassLoader 加载器加载的，AppClassLoader 的 Parent 加载器是 ExtClassLoader ,但是ExtClassLoader 的 Parent 为 null 是怎么回事呵，朋友们留意的话，前面有提到 Bootstrap Loader 是用 C++ 语言写的，依 java 的观点来看，逻辑上并不存在 Bootstrap Loader 的类实体，所以在 java 程序代码里试图打印出其内容时，我们就会看到输出为 null。

为什么要有“委托机制”？可以从安全方面考虑，如果一个人写了一个恶意的基础类（如java.lang.String）并加载到 JVM 将会引起严重的后果，但有了全盘负责制，java.lang.String 永远是由根装载器来装载，避免以上情况发生。

假如我们自己写了一个 java.lang.String 的类，我们是否可以替换调JDK 本身的类？

答案是否定的。我们不能实现。为什么呢？我看很多网上解释是说双亲委托机制解决这个问题，其实不是非常的准确。因为双亲委托机制是可以打破的，你完全可以自己写一个 classLoader 来加载自己写的java.lang.String 类，但是你会发现也不会加载成功，具体就是因为针对java.* 开头的类，jvm 的实现中已经保证了必须由 bootstrp 来加载。

### 自定义 ClassLoader
既然系统已经存在三种 ClassLoader 为什么还需要我们自己定义 ClassLoader 呢？因为 Java 中提供的默认 ClassLoader 只加载指定目录下面的 jar 和 class ，但是如果我们需要加载其他地方的 jar 和 class 时则无能为力了，这个时候就需要我们自己实现 ClassLoader 了。我们从上面了解到 ClassLoader是一个抽象类，实现自定义的 ClassLoader 需要继承该类并实现里面的方法。一般情况下，我们重写父类的 findClass 方法即可。

```
package java.lang;
public abstract class ClassLoader {
	 public Class loadClass(String name);
	 protected Class defineClass(byte[] b);
	 public URL getResource(String name);
	 public Enumeration getResources(String name);
	 public ClassLoader getParent();
	 Class<?> findClass(String name)
	 //...
}
```

ClassLoader 方法那么多为什么只重写 findClass 方法？ 因为 JDK 已经在 loadClass 方法中帮我们实现了 ClassLoader 搜索类的算法，当在 loadClass 方法中搜索不到类时，loadClass 方法就会调用findClass 方法来搜索类，所以我们只需重写该方法即可。如没有特殊的要求，一般不建议重写 loadClass 搜索类的算法。

### 自定义 ClassLoader的示例
假如我们自定义一个 classloader 我们可以编写一个测试类来说明。在当前目录下面新建一个 Hello 类。里面有个方法 sayHello 然后放入到指定目录下面，如：我当前的目录为：

> /Users/java/intellidea/JavaTest/src/demo/Hello.java

```
package demo;
public class Hello {
    public void sayHello() {
        System.out.println("say hello ------> classloader");
    }
}
```
接着我们需要自定义一个 ClassLoader 来继承系统的 ClassLoader。我们命名为 HelloClassLoader 类。

```
package demo;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
public class HelloClassLoader extends ClassLoader {
    private String mLibPath;
    public HelloClassLoader(String path) {
        mLibPath = path;
    }
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String fileName = getFileName(name);
        File file = new File(mLibPath,fileName);
        try {
            FileInputStream is = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int len = 0;
            try {
                while ((len = is.read()) != -1) {
                    bos.write(len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] data = bos.toByteArray();
            is.close();
            bos.close();
            return defineClass(name,data,0,data.length);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return super.findClass(name);
    }
    //获取要加载 的class文件名
    private String getFileName(String name) {
        // TODO Auto-generated method stub
        int index = name.lastIndexOf('.');
        if(index == -1){
            return name+".class";
        }else{
            return name.substring(index)+".class";
        }
    }
}
```
在HelloClassLoader 类中我们通过 findClass() 方法来查找我们用到的 Hello.class 文件，从而生成了 Class 对象。接着我们编写HelloClassLoaderTest 测试类进行测：

```
package demo;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
public class HelloClassLoaderTest {
    public static void main(String[] args) throws InvocationTargetException {
        // TODO Auto-generated method stub
        //创建自定义classloader对象。
        HelloClassLoader diskLoader = new HelloClassLoader("/Users/java/intellidea/JavaTest/src/demo");
        try {
            //加载class文件
            Class c = diskLoader.loadClass("demo.Hello");
            if (c != null) {
                try {
                    Object obj = c.newInstance();
                    Method method = c.getDeclaredMethod("sayHello", null);
                    //通过反射调用Hello类的sayHello方法
                    method.invoke(obj, null);
                } catch (InstantiationException | IllegalAccessException
                        | NoSuchMethodException
                        | SecurityException |
                        IllegalArgumentException |
                        InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
```

测试类按照预测结果应该打印：

```
say hello ------> classloader
```

这行代码是我们在 Hello 类中的一个方法 sayHello 里面所打印的。我们运行一下上面的代码，查看下打印结果：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191127102947203.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqMTg4MjY2,size_16,color_FFFFFF,t_70)

如料想的一样，打印出了正确结果。以上便是一个简单的自定义ClassLoader 类的实现过程。

###  总结
以上便是关于 Java 中的 ClassLoader 里面的内容，还有一些暂未涉及到，接下来便是研究 Android中 的 ClassLoader 使用内容。





