---
title: Java中关于泛型的浅析
categories:
  - 'Java'
tags:
  - 'Java'
  - '泛型'
comments: true
date: 2015-06-29 10:16:01
img: https://img-blog.csdnimg.cn/20191127154127440.jpeg
---


### 前言
java 泛型是java SE 1.5的新特性，泛型的本质是参数化类型，也就是说所操作的数据类型被指定为一个参数。这种参数类型可以用在类、接口和方法的创建中，分别称为泛型类、泛型接口、泛型方法。

泛型（Generic type 或者 generics）是对 Java 语言的类型系统的一种扩展，以支持创建可以按类型进行参数化的类。可以把类型参数看作是使用参数化类型时指定的类型的一个占位符，就像方法的形式参数是运行时传递的值的占位符一样。

### 为什么使用泛型
#### 问题引入
泛型是在java SE 1.5中引入的，在这之前，如果要使用集合的话是怎么样子的呢？对于集合List而言，往里面添加数据的方法add的参数是一个Object类型，这也就意味着什么数据类型都可以往里面添加。

```
List list = new ArrayList();
	list.add("abc");
	list.add("cdf");
```
然而，添加容易取出来就有点麻烦了。因为我们取的时候需要进行强制转换：

```
String s = (String) list.get(0);
```

有的时候，我们忘了添加进去是什么类型了，或者我们写的方法，类给别人使用的时候，这个时候就会很容易出错，因为我们不知道是什么类型，因此在运行的时候就会出现异常。

```
List list = new ArrayList();
		list.add("abc");
		list.add("cdf");
		list.add(12);
		list.add(23.9);

		for (int i = 0; i < list.size(); i++) {
			String tempString = (String) list.get(i);
			System.out.println(tempString);
		}
```

![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/11/28/16eb2097a0aec275?w=1208&h=72&f=png&s=31889)

 #### 解决方法
 对于上述引发的问题，我们可以通过限定传入数据的类型来解决，就是在add数据的时候我们就将它限定为某一类型，其他类型的数据add不进去，这样的话，我们就很简单的解决了该问题。这种方法就是下面需要说的泛型。

```
List<String> list = new ArrayList<>();
		list.add("abc");
		list.add("cdf");
```
通过限定add时传入的类型是String，这样在编译时就能检测出来，这样就能避免了上述问题。

### 泛型分类
泛型有三种使用方式，分别为：泛型类、泛型接口、泛型方法。
#### 泛型类
泛型类说起来比较抽象，我们通过一个示例来了解一下：

```
/**
 * 此处T可以随便写为任意标识，常见的如T、E、K、V等形式的参数常用于表示泛型
 * 
 * @author admin
 *
 * @param <T>
 */
public class Generic<T> {

	/**
	 * 这个成员变量的类型为T,T的类型由外部指定
	 */
	private T t;

	/**
	 * 泛型构造方法形参t的类型也为T，T的类型由外部指定
	 * 
	 * @param t
	 */
	public Generic(T t) {
		super();
		this.t = t;
	}

	/**
	 * 泛型方法getT的返回值类型为T，T的类型由外部指定
	 * 
	 * @return
	 */
	public T getT() {
		return t;
	}

	/**
	 * 泛型方法setT的参数类型为T，T的类型由外部指定
	 * 
	 * @param t
	 */
	public void setT(T t) {
		this.t = t;
	}
}
```

泛型类比较简单，需要指定一个参数类型T，然后由外部传入，我们看一下这个泛型类它的使用方式：

```
//泛型参数T这里指定为String类型
Generic<String> generic = new Generic<String>("Hello");
System.out.println(generic.getT());
```

打印结果如下：

```
Hello
```

这里需要注意的是：**泛型的类型参数只能是类类型（包括自定义类），不能是简单类型。**

#### 泛型接口
泛型接口与泛型类的定义及使用基本相同。我们也通过示例来了解一下：

```
/**
 * 定义一个泛型接口
 * 
 * @author admin
 *
 * @param <T>
 */
public interface IGeneric<T> {

	T result();

}
```

定义一个泛型接口后，使用这个泛型接口有两种情况：
##### 未传入泛型参数

```
/**
 * 未传入泛型实参时，与泛型类的定义相同，在声明类的时候，需将泛型的声明也一起加到类中
 * 
 * @author admin
 *
 * @param <T>
 */
public class Generic1Test<T> implements IGeneric<T> {

	@Override
	public T result() {
		return null;
	}

}
```

##### 传入参数类型

```
/**
 * 在实现类实现泛型接口时，如已将泛型类型传入实参类型，则所有使用泛型的地方都要替换成传入的实参类型
 * 
 * @author admin
 *
 */
public class Generic2Test implements IGeneric<String> {

	@Override
	public String result() {

		return "";
	}
}
```

#### 泛型方法
泛型方法，是在调用方法的时候指明泛型的具体类型 ，但是相对于泛型类而言，泛型方法就比较复杂了，我们来看一个具体示例：

```
/**
	 * 声明一个泛型方法，该泛型方法中带一个T类型形参，
	 * 
	 * @param <T>
	 * @param a
	 * @param c
	 */
	static <T> void fromArrayToCollection(T[] a, List<T> c) {
		for (T o : a) {
			c.add(o);
		}
	}
```

申明一个泛型方法时，首先在public与返回值之间的<T>必不可少，这表明这是一个泛型方法，并且声明了一个泛型T，这个T可以出现在这个泛型方法的任意位置，泛型的数量也可以为任意多个。

对于泛型参数的申明比较复杂的多，后面需要专门的讲解。

### 泛型通配符
我们在定义泛型类，泛型方法，泛型接口的时候经常会碰见很多不同的通配符，比如 T，E，K，V ，？等等，这些通配符又都是什么意思呢？

其实，这些不同的通配符没什么本质上的区别，只不过在Java开发过程中我们约定了这些不同的通配符所表达的意思不一样：

 1. ？表示不确定的 java 类型
 2. T (type) 表示具体的一个java类型
 3. K V (key value) 分别代表java键值中的Key， Value
 4. E (element) 代表Element

#### 无界通配符 <?>
对于通配符<?>我们可以理解为无界，就是<?>, 比如List<?>，通配符<?>的主要作用就是让泛型能够接受未知类型的数据。如果泛型的类型只在方法声明中出现一次，就可以用通配符<？>取代它。

我们说明一点：List<?> list和List list的区别：

 1. List<?> list是表示持有某种特定类型对象的List，但是不知道是哪种类型；List
    list是表示持有Object类型对象的List。
 2. List<?> list因为不知道持有的实际类型，所以不能add任何类型的对象，但是List
    list因为持有的是Object类型对象，所以可以add任何类型的对象。

**注意：List<?> list可以add(null)，因为null是任何引用数据类型都具有的元素。**

####  上界通配符 < ? extends E>
用 extends 关键字声明，表示参数化的类型可能是所指定的类型，或者是此类型的子类。

#### 下界通配符 < ? super E>
用 super 进行声明，表示参数化的类型可能是所指定的类型，或者是此类型的父类型，直至 Object。

### 泛型擦除
Java的泛型是伪泛型，这是因为Java在编译期间，所有的泛型信息都会被擦掉，正确理解泛型概念的首要前提是理解类型擦除。Java的泛型基本上都是在编译器这个层次上实现的，在生成的字节码中是不包含泛型中的类型信息的，使用泛型的时候加上类型参数，在编译器编译的时候会去掉，这个过程成为类型擦除。

譬如：
对于List<String>类型，在编译后会变成List，JVM看到的只是List，而由泛型附加的类型信息对JVM是看不到的。Java编译器会在编译时尽可能的发现可能出错的地方。

我们举个例子来说明泛型擦除：

```
public static void main(String[] args) {
		List<String> strList = new ArrayList<String>();
		strList.add("AAAAA");

		List<Integer> intList = new ArrayList<>();
		intList.add(123);

		System.out.println("编译时期类型是否相同： " + (strList.getClass() == intList.getClass()));
	}
```
打印结果如下：
![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/11/28/16eb2097a06a0919?w=540&h=164&f=png&s=33300)

从上面的例子看出，我们定义了两个ArrayList数组，不过一个是ArrayList<String>泛型类型的，只能存储字符串；一个是ArrayList<Integer>泛型类型的，只能存储整数，最后，我们通过list1对象和list2对象的getClass()方法获取他们的类的信息，最后发现结果为true。说明泛型类型String和Integer都被擦除掉了，只剩下原始类型。

### 泛型好处
Java 语言中引入泛型是一个较大的功能增强。不仅语言、类型系统和编译器有了较大的变化，以支持泛型，而且类库也进行了大翻修，所以许多重要的类，比如集合框架，都已经成为泛型化的了。

 1. 类型安全。 泛型的主要目标是提高 Java 程序的类型安全。
 2. 消除强制类型转换。 泛型的一个附带好处是，消除源代码中的许多强制类型转换。这使得代码更加可读，并且减少了出错机会。


### 总结
在上面所举的例子都是一些简单的示例并不具有实际的应用，只是为了简单说明泛型的概念。本篇文章只是作为泛型的简单阐述，对于开发过程中的泛型还需要我们深入的探索，后期会出一遍深入理解泛型的文章，敬请期待。


