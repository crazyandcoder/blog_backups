---
title: Java学习总结-反射
categories:
  - 'Java'
tags:
  - 'Java'
  - '反射'
comments: true
date: 2017-12-20 17:49:20
img: https://img-blog.csdnimg.cn/20191127154127440.jpeg
---


反射经常听到这个词，但是总是不理解这个意思。今天便来理解一下反射这个概念，为什么说在框架设计中，反射用到的比较多。本文记录一下学习反射方面的知识点。

### 反射概念
JAVA反射机制是在运行状态中，对于任意一个类，都能够知道这个类的所有属性和方法；对于任意一个对象，都能够调用它的任意一个方法和属性；这种动态获取的信息以及动态调用对象的方法的功能称为java语言的反射机制。概念比较精确、抽象但是不便于理解。譬如我们存在一个类，它的相关属性、方法、构造器都是 private 类型的，对于这样一个类，我们不能通过 new 的方式来创建一个它的对象，更不能通过平常使用方法属性的方式来调用这个类的属性、方法，甚至来创建它的对象，但是通过反射这种方式却可以生成它的对象以及调用它的属性、方法。

#### Class
Class 与 class 有着本质的区别。小写的 class 是 Java 中的关键字，而大写的 Class 则是类。


```
public final class Class<T> implements java.io.Serializable,
                              GenericDeclaration,
                              Type,
                              AnnotatedElement {
}
```

在 Java 的世界里，一切皆是对象，在 Java 中存在两种对象，一种是 new 产生的对象另一种则是 Class 对象。一般的对象，我们可以通过 new 关键字来产生，而 Class 对象则不可以，因为它是 JVM 生成用来保存对应类的信息的。也就是说，如：当我们定义了一个类 Person 时，编译成功后，将会生成一个 Person.class 字节码，这个时候编译器同时为我们从创建了一个 Class 对象并将它保存在 Person.class 文件中。也就是说：Person.java 编译成 Person.class 后将会产生一个对应的Class 对象。

#### Class 对象获取
一般情况下，一个实例对象对应的 Class 对象有以下三种方式获取：

1、通过实例变量的 getClass 方法：

```
Person person = new Person();
Class personClass = person.getClass();
System.out.println("class1: " + personClass);
```

2、直接给出对象类文件的.class：

```
Class personClass1=Person.class;
System.out.println("class2: " + personClass1);
```

如果不能通过 new 关键字来创建对象的话，我们也可以通过第三种方式来获取：

3、通过类Class的静态方法forName():

```
try {
          Class personClass2 = Class.forName("reflect.Person");
          System.out.println("class3: " + personClass2);
      } catch (ClassNotFoundException e) {
          e.printStackTrace();
      }
```

打印结果如下：


```
class1: class reflect.Person
class2: class reflect.Person
class3: class reflect.Person
```

#### Class 使用
JAVA反射机制是在运行状态中，对于任意一个类，都能够知道这个类的所有属性和方法；对于任意一个对象，都能够调用它的任意一个方法和属性；这种动态获取的信息以及动态调用对象的方法的功能称为java语言的反射机制。

**1、CLASS 名称获取**
对于 name 的获取，Class 类提供了三种方法：


```
Class.getName();
Class.getSimpleName();
Class.getCanonicalName();
```

那么这三种方式有何区别？通过具体的实例还说明。

首先我们得创建一个类 Person.java 
 

```
package reflect;
public class Person {
    private int age;
    private String name;
    public Person() {
    }
    public Person(int age, String name) {
        this.age = age;
        this.name = name;
    }
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

然后分别获取它的 Class 名称：

```
      //getName()方式
      Person person = new Person();
      Class personClass = person.getClass();
      System.out.println("class1: " + personClass.getName());
 //getSimpleName()方式
      Class personClass1=Person.class;
      System.out.println("class2: " + personClass1.getSimpleName());
//getCanonicalName()方式
      try {
          Class personClass2 = Class.forName("reflect.Person");
          System.out.println("class3: " + personClass2.getCanonicalName());
      } catch (ClassNotFoundException e) {
          e.printStackTrace();
      }
```

打印结果如下：


```
class1: reflect.Person
class2: Person
class3: reflect.Person
```

可以看到第一种和第三种方式获取的结果是一样的，而只有第二种不一样。及一三中获取的是类的完整路径，而第二种获取的是类名，不包含包名。而一三种有何区别可以通过类的内部类来研究。

**2、修饰符获取**
Java 中的修饰符主要有 private 、public 、static、protected、等。Java 反射提供了 API 去获取这些修饰符。

通过 Class.getModifiers() 来获取修饰符


```
Person person = new Person();
Class personClass = person.getClass();
System.out.println("modifiers: " + personClass.getModifiers());
```

结果如下：


```
modifiers: 1
```

返回的却是一个 int 型的数值。为什么会返回一个整型数值呢？这是因为一个类定义的时候可能会被多个修饰符修饰，为了一并获取，所以 Java 工程师考虑到了位运算，用一个 int 数值来记录所有的修饰符，然后不同的位对应不同的修饰符，这些修饰符对应的位都定义在 Modifier 这个类当中。 
 

```
public class Modifier {
    public static final int PUBLIC           = 0x00000001;
    public static final int PRIVATE          = 0x00000002;
    public static final int PROTECTED        = 0x00000004;
    public static final int STATIC           = 0x00000008;
    public static final int FINAL            = 0x00000010;
	......
	public static String toString(int mod) {
        StringBuilder sb = new StringBuilder();
        int len;
        if ((mod & PUBLIC) != 0)        sb.append("public ");
        if ((mod & PROTECTED) != 0)     sb.append("protected ");
        if ((mod & PRIVATE) != 0)       sb.append("private ");
        /* Canonical order */
        if ((mod & ABSTRACT) != 0)      sb.append("abstract ");
        if ((mod & STATIC) != 0)        sb.append("static ");
        if ((mod & FINAL) != 0)         sb.append("final ");
        if ((mod & TRANSIENT) != 0)     sb.append("transient ");
        if ((mod & VOLATILE) != 0)      sb.append("volatile ");
        if ((mod & SYNCHRONIZED) != 0)  sb.append("synchronized ");
        if ((mod & NATIVE) != 0)        sb.append("native ");
        if ((mod & STRICT) != 0)        sb.append("strictfp ");
        if ((mod & INTERFACE) != 0)     sb.append("interface ");
        if ((len = sb.length()) > 0)    /* trim trailing space */
            return sb.toString().substring(0, len-1);
        return "";
    }
```

当然如果需要打印字符串的话，可以通过 Modifier 类提供的静态方法toString 来获取。

 

```
System.out.println("modifiers: " + Modifier.toString(personClass.getModifiers()));
```



**3、获取 CLASS 的成员**
一个类的成员包括属性、方法、构造函数。对应到 Class 中就是 Field、Method、Constructor。接下来我们通过具体实例来熟悉如何获取这些成员。

**3.1、获取 Field**
获取指定名称的属性 API：


```
public Field getDeclaredField(String name)
                       throws NoSuchFieldException,
                              SecurityException;
public Field getField(String name)
               throws NoSuchFieldException,
                      SecurityException
```

两者的区别就是 getDeclaredField() 获取的是 Class 中被 private 修饰的属性。 getField() 方法获取的是非私有属性，并且 getField() 在当前 Class 获取不到时会向祖先类获取。

获取所有的属性。


```
//获取所有的属性，但不包括从父类继承下来的属性
public Field[] getDeclaredFields() throws SecurityException {}

//获取自身的所有的 public 属性，包括从父类继承下来的。
public Field[] getFields() throws SecurityException {}
```

**3.2、获取 Method**
Method 即是 Class 中的方法。


```
public Method getDeclaredMethod(String name, Class<?>... parameterTypes)
public Method getMethod(String name, Class<?>... parameterTypes)
public Method[] getDeclaredMethods() throws SecurityException
public Method getMethod(String name, Class<?>... parameterTypes)
```

**3.3、获取 Constructor**

```
public Constructor<T> getDeclaredConstructor(Class<?>... parameterTypes)
public Constructor<T> getConstructor(Class<?>... parameterTypes)
public Constructor<?>[] getDeclaredConstructors() throws SecurityException 
public Constructor<?>[] getConstructors() throws SecurityException
```

#### 反射运用
以上便是简单的获取 Class 中的字段、方法、构造函数，反射的机制便是操控这些字段、方法、构造函数。

