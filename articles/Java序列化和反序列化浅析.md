---
title: Java序列化和反序列化浅析
categories:
  - 'Java'
tags:
  - 'Java'
  - '序列化'
comments: true
date: 2015-08-05 17:01:06
img: https://img-blog.csdnimg.cn/20191127154127440.jpeg
---


### 概念
Java对象序列化的意思就是将对象的状态转化成字节流，以后可以通过这些值再生成相同状态的对象。对象序列化是对象持久化的一种实现方法，它是将对象的属性和方法转化为一种序列化的形式用于存储和传输。反序列化就是根据这些保存的信息重建对象的过程。

**序列化：** 将java对象转化为字节序列的过程。
**反序列化：** 将字节序列转化为java对象的过程。


我们知道，当两个进程进行远程通信时，可以相互发送各种类型的数据，包括文本、图片、音频、视频等， 而这些数据都会以二进制序列的形式在网络上传送。那么当两个Java进程进行通信时，能否实现进程间的对象传送呢？答案是可以的。如何做到呢？这就需要Java序列化与反序列化了。换句话说，一方面，发送方需要把这个Java对象转换为字节序列，然后在网络上传送；另一方面，接收方需要从字节序列中恢复出Java对象。当我们明晰了为什么需要Java序列化和反序列化后，我们很自然地会想Java序列化的好处。其好处一是实现了数据的持久化，通过序列化可以把数据永久地保存到硬盘上（通常存放在文件里），二是，利用序列化实现远程通信，即在网络上传送对象的字节序列。

```
java.io.ObjectOutputStream
```
表示对象输出流，它的writeObject(Object obj)方法可以对参数指定的obj对象进行序列化，把得到的字节序列写到一个目标输出流中。

```
java.io.ObjectInputStream
```
表示对象输入流，它的readObject()方法源输入流中读取字节序列，再把它们反序列化成为一个对象，并将其返回。

**只有实现了Serializable或Externalizable接口的类的对象才能被序列化，否则抛出异常。**


### 序列化和反序列化的步骤
#### 序列化
```
步骤一：创建一个对象输出流，它可以包装一个其它类型的目标输出流，如文件输出流：
ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(“目标地址路径”));
步骤二：通过对象输出流的writeObject()方法写对象：
out.writeObject("Hello");
out.writeObject(new Date());
```

#### 反序列化
```
步骤一：创建一个对象输入流，它可以包装一个其它类型输入流，如文件输入流：
ObjectInputStream in = new ObjectInputStream(new fileInputStream(“目标地址路径”));
步骤二：通过对象输出流的readObject()方法读取对象：
String obj1 = (String)in.readObject();
Date obj2 =  (Date)in.readObject();
```

**说明**：为了正确读取数据，完成反序列化，必须保证向对象输出流写对象的顺序与从对象输入流中读对象的顺序一致。

### 示例
我们首先写个Person实现Serializable接口，前面已经说过，只有实现了Serializable或Externalizable接口的类的对象才能被序列化，否则抛出异常。

```
import java.io.Serializable;
/**
 * 
 * 测试序列化和反序列化
 * @author crazyandcoder
 * @date [2015-8-5 上午11:14:32]
 */
public class Person implements Serializable  {
	
	private int age;
	private String name;
	//序列化ID
	private static final long serialVersionUID = -5809782578272943999L;
	
	public Person() {}
	
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
```

其次，我们在main()里面写个方法，执行序列化过程：

```
/**
 * 
 * 测试序列化和反序列化
 * @author crazyandcoder
 * @date [2015-8-5 上午11:16:14]
 */
public class ObjSerializeAndDeserializeTest { 
	public static void main(String[] args) {
		
		//将Person对象序列化
		SerializePerson();
	}
	
	/**
	 * 
	 * @author crazyandcoder
	 * @Title: 序列化Person对象，将其存储到 E:/hello.txt文件中
	 * @param  
	 * @return void 
	 * @throws 
	 * @date [2015-8-5 上午11:21:27]
	 */
	private static void SerializePerson() {
		Person person =new Person();
		person.setAge(30);
		person.setName("SerializePerson");
		ObjectOutputStream outputStream = null;
		try {
			outputStream=new ObjectOutputStream(new FileOutputStream("E:/hello.txt"));
			outputStream.writeObject(person);
			System.out.println("序列化成功。");
		} catch (FileNotFoundException e) {
			e.printStackTrace();			
		} catch (IOException e) {
			e.printStackTrace();		
		} finally {
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
	}
}
```
代码很简单，首先创建一个对象输出流ObjectOutputStream，它可以包装一个其它类型的目标输出流，如文件输出流FileOutputStream，并指定存储的位置为“E:/hello.txt”，然后通过对象输出流的writeObject()方法写对象便执行了序列化过程。运行看一下效果，正确的话便会在控制台打印“”，

![在这里插入图片描述](https://img-blog.csdnimg.cn/2019112516524032.png)

同时在本地E盘下会创建一个Hello.txt文件，

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191125165320544.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqMTg4MjY2,size_16,color_FFFFFF,t_70)

我们查看一下hello.txt文件中的内容，里面是一串字节序列，打开该文件的时候不要用自带的记事本打开，因为涉及到字符编码的问题，所以显示的话是一串乱码，建议用SublimeText打开。

![在这里插入图片描述](https://img-blog.csdnimg.cn/2019112516535293.png)

我们再写个方法来反序列化该字节成Person对象，并打印出里面的值。

```
/**
 * 
 * 测试序列化和反序列化
 * @author crazyandcoder
 * @date [2015-8-5 上午11:16:14]
 */
public class ObjSerializeAndDeserializeTest {
	 
	public static void main(String[] args) {
		
		//反序列化生成Person对象
		Person person=DeserializePerson();
		System.out.println("name :"+person.getName());
		System.out.println("age  :"+person.getAge());
		
	}
	
	/**
	 * 执行反序列化过程生产Person对象
	 * @author crazyandcoder
	 * @Title: DeserializePerson 
	 * @param @return 
	 * @return Person 
	 * @throws 
	 * @date [2015-8-5 下午1:30:12]
	 */
	private static Person DeserializePerson() {
		
		Person person=null;
		ObjectInputStream inputStream=null;
		try {
			inputStream=new ObjectInputStream(new FileInputStream("E:/hello.txt"));
			try {
				person=(Person)inputStream.readObject();
				System.out.println("执行反序列化过程成功。");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return person;
	}
｝
```

执行反序列化的代码也是很简单的，首先创建一个输入流对象ObjectInputStream，然后从指定的目录下“E:/hello.txt”获取它的字节序列，然后通过输入流对象的readObject()方法将其获得的对象强制转化为Person对象，这就完成了反序列化工作，正确的反序列化成功的情况下控制台打印输出为：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191125165612693.png)
### 序列化ID的作用
我们在代码里会发现有这样一个变量：serialVersionUID，那么这个变量serialVersionUID到底具有什么作用呢？能不能去掉呢？


```
public class Person implements Serializable  {
	
	private int age;
	private String sex;
	private String name;
	private String hobby;
	//序列化ID
	private static final long serialVersionUID = -5809782578272943999L;
        ............
｝
```


其实，这个序列化ID起着关键的作用，它决定着是否能够成功反序列化！简单来说，java的序列化机制是通过在运行时判断类的serialVersionUID来验证版本一致性的。在进行反序列化时，JVM会把传来的字节流中的serialVersionUID与本地实体类中的serialVersionUID进行比较，如果相同则认为是一致的，便可以进行反序列化，否则就会报序列化版本不一致的异常。等会我们可以通过代码验证一下。

### 序列化ID如何产生

当我们一个实体类中没有显示的定义一个名为“serialVersionUID”、类型为long的变量时，Java序列化机制会根据编译时的class自动生成一个serialVersionUID作为序列化版本比较，这种情况下，只有同一次编译生成的class才会生成相同的serialVersionUID。譬如，当我们编写一个类时，随着时间的推移，我们因为需求改动，需要在本地类中添加其他的字段，这个时候再反序列化时便会出现serialVersionUID不一致，导致反序列化失败。那么如何解决呢？便是在本地类中添加一个“serialVersionUID”变量，值保持不变，便可以进行序列化和反序列化。


![在这里插入图片描述](https://img-blog.csdnimg.cn/2019112516555580.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqMTg4MjY2,size_16,color_FFFFFF,t_70)
不出意外，报了一个异常。
![在这里插入图片描述](https://img-blog.csdnimg.cn/2019112516593579.png)

从上面两张图便可以看出两次的序列化ID是不一样的，导致反序列化失败。

### 总结
虚拟机是否允许反序列化，不仅取决于类路径和功能代码是否一致，一个非常重要的一点是两个类的序列化 ID 是否一致（就是 private static final long serialVersionUID = 1L）。

