---
title: Java学习总结-注解
date: 2017-11-20 14:34:31
categories:
  - 'Java'
tags: 
- Java
- 注解
img: https://img-blog.csdnimg.cn/20191127154127440.jpeg
---


### 注解概念
Java注解是附加在代码中的一些元信息，用于一些工具在编译、运行时进行解析和使用，起到说明、配置的功能。
注解不会也不能影响代码的实际逻辑，仅仅起到辅助性的作用。包含在 java.lang.annotation 包中。

其中涉及到另外一个概念“元注解”，元注解是指注解的注解。包括以下五种 ：

 1. @Retention
 2. @Target
 3. @Document
 4. @Inherited
 5. @Repeatable 

### 注解解释
#### @Retention
当 @Retention 应用到一个注解上的时候，它解释说明了这个注解的的存活时间。

```
RetentionPolicy.SOURCE 
注解只在源码阶段保留，在编译器进行编译时它将被丢弃忽视。

RetentionPolicy.CLASS 
注解只被保留到编译进行的时候，它并不会被加载到 JVM 中。

RetentionPolicy.RUNTIME 
注解可以保留到程序运行的时候，它会被加载进入到 JVM 中，所以在程序运行时可以获取到它们。

@DOCUMENTED
它的作用是能够将注解中的元素包含到 Javadoc 中去。
```

#### @Target
@Target 指定了注解运用的地方。


```
ElementType.ANNOTATION_TYPE 
可以给一个注解进行注解

ElementType.CONSTRUCTOR 
可以给构造方法进行注解

ElementType.FIELD 
可以给属性进行注解

ElementType.LOCAL_VARIABLE 
可以给局部变量进行注解

ElementType.METHOD 
可以给方法进行注解

ElementType.PACKAGE 
可以给一个包进行注解

ElementType.PARAMETER
可以给一个方法内的参数进行注解
 
ElementType.TYPE 
可以给一个类型进行注解，比如类、接口、枚举

@INHERITED
如果一个超类被 @Inherited 注解过的注解进行注解的话，那么如果它的子类没有被任何注解应用的话，那么这个子类就继承了超类的注解。

@REPEATABLE
@Repeatable 是 Java 1.8 才加进来的，所以算是一个新的特性，其实只是语法糖而已。
```

#### 自定义注解
注解通过 @interface 关键字进行定义。


```
public @interface PersonAnnotation {
}
```

#### 注解的属性
注解的属性也叫做成员变量。注解只有成员变量，没有方法。注解的成员变量在注解的定义中以“无形参的方法”形式来声明，其方法名定义了该成员变量的名字，其返回值定义了该成员变量的类型。


```
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PersonAnnotation {
    int age();
    String name();
}
```

上面代码定义了 PersonAnnotation 这个注解中拥有 age 和 name 两个属性。在使用的时候，我们应该给它们进行赋值。赋值的方式是在注解的括号内以 value=”” 形式，多个属性之前用 ，隔开。


```
@PersonAnnotation(age=3,name="Xiaoming")
public class People {
}
```

参数成员只能用基本类 byte,short,char,int,long,float,double,boolean 八种基本数据类型和 String、Enum、Class、annotations 等数据类型,以及这一些类型的数组.

注解中属性可以有默认值，默认值需要用 default 关键值指定。


```
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PersonAnnotation {
    int age() default 20;
    String name() default "Xiaoming";
}
```

则可以直接使用，不需要进行复制。


```
@PersonAnnotation
public class People {
}
```

#### 注解获取
要获取类方法和字段的注解信息，必须通过 Java 的反射技术来获取 Annotation 对象,因为你除此之外没有别的获取注解对象的方法

```
isAnnotationPresent()
```

通过 Class 对象的 isAnnotationPresent() 方法判断它是否应用了某个注解。

```
getAnnotation()
```

通过 getAnnotation() 方法来获取 Annotation 对象。

#### 属性值获取
通过以上两个注解方法就可以获取注解的值了，接下来我们通过一个自定义的实例来了解注解以上知识点。

```
import java.lang.annotation.*;
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PersonAnnotaion {
    int age() default 20;
    String name() default "Xiaoming";
}
```

首先定义一个 PersonAnnotaion 注解，接着写一个 Person 类进行使用该注解。


```
@PersonAnnotaion(age = 22, name = "LiLei")
public class Person {
}
```

最后写个测试类用于获取该注解的属性值。


```
public class PersonAnnataionTest {
    public static void main(String[] args) {
        boolean hasAnnotation = Person.class.isAnnotationPresent(PersonAnnotaion.class);
        if (hasAnnotation) {
            PersonAnnotaion testAnnotation = Person.class.getAnnotation(PersonAnnotaion.class);
            System.out.println("age :" + testAnnotation.age());
            System.out.println("name :" + testAnnotation.name());
        }
    }
}
```


以上是类的注解，同样的，属性、方法都可以使用注解，最常见的就是 Java 提供了一些内置的注解。

@Deprecated 
这个元素是用来标记过时的元素，编译器在编译阶段遇到这个注解时会发出提醒警告，告诉开发者正在调用一个过时的元素比如过时的方法、过时的类、过时的成员变量。

@Override 
提示子类要复写父类中被 @Override 修饰的方法

@SuppressWarnings 
阻止警告的意思。之前说过调用被 @Deprecated 注解的方法后，编译器会警告提醒，而有时候开发者会忽略这种警告，他们可以在调用的地方通过 @SuppressWarnings 达到目的。

#### 自定义注解类规则
Annotation型定义为@interface, 所有的Annotation会自动继承java.lang.Annotation这一接口,并且不能再去继承别的类或是接口.参数成员只能用public或默认(default)这两个访问权修饰参数成员只能用基本类型byte,short,char,int,long,float,double,boolean八种基本数据类型和String、Enum、Class、annotations等数据类型,以及这一些类型的数组.要获取类方法和字段的注解信息，必须通过Java的反射技术来获取 Annotation对象,因为你除此之外没有别的获取注解对象的方法注解也可以没有定义成员, 不过这样注解就没啥用了



