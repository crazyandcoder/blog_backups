---
title: Java中关于synchronized的浅析
categories:
  - Java
tags:
  - 'Java'
  - '同步锁'
comments: true
date: 2016-08-27 15:39:38
img: https://img-blog.csdnimg.cn/20191127154127440.jpeg
---

### 进程
我们都知道计算机的核心是CPU，它承担了所有的计算任务；而操作系统是计算机的管理者，它负责任务的调度、资源的分配和管理，统领整个计算机硬件；应用程序侧是具有某种功能的程序，程序是运行于操作系统之上的。

进程是一个具有一定独立功能的程序在一个数据集上的一次动态执行的过程，是操作系统进行资源分配和调度的一个独立单位，是应用程序运行的载体。进程是一种抽象的概念，从来没有统一的标准定义。进程一般由程序、数据集合和进程控制块三部分组成。程序用于描述进程要完成的功能，是控制进程执行的指令集；数据集合是程序在执行时所需要的数据和工作区；程序控制块(Program Control Block，简称PCB)，包含进程的描述信息和控制信息，是进程存在的唯一标志。

### 进程具有的特征：

动态性：进程是程序的一次执行过程，是临时的，有生命期的，是动态产生，动态消亡的；
并发性：任何进程都可以同其他进程一起并发执行；
独立性：进程是系统进行资源分配和调度的一个独立单位；
结构性：进程由程序、数据和进程控制块三部分组成。
线程
在早期的操作系统中并没有线程的概念，进程是能拥有资源和独立运行的最小单位，也是程序执行的最小单位。任务调度采用的是时间片轮转的抢占式调度方式，而进程是任务调度的最小单位，每个进程有各自独立的一块内存，使得各个进程之间内存地址相互隔离。

后来，随着计算机的发展，对CPU的要求越来越高，进程之间的切换开销较大，已经无法满足越来越复杂的程序的要求了。于是就发明了线程，线程是程序执行中一个单一的顺序控制流程，是程序执行流的最小单元，是处理器调度和分派的基本单位。一个进程可以有一个或多个线程，各个线程之间共享程序的内存空间(也就是所在进程的内存空间)。一个标准的线程由线程ID、当前指令指针(PC)、寄存器和堆栈组成。而进程由内存空间(代码、数据、进程空间、打开的文件)和一个或多个线程组成。

### 进程与线程的区别
前面讲了进程与线程，但可能你还觉得迷糊，感觉他们很类似。的确，进程与线程有着千丝万缕的关系，下面就让我们一起来理一理：

线程是程序执行的最小单位，而进程是操作系统分配资源的最小单位；
一个进程由一个或多个线程组成，线程是一个进程中代码的不同执行路线；
进程之间相互独立，但同一进程下的各个线程之间共享程序的内存空间(包括代码段、数据集、堆等)及一些进程级的资源(如打开文件和信号)，某进程内的线程在其它进程不可见；
调度和切换：线程上下文切换比进程上下文切换要快得多。
synchronized关键字
synchronized是Java中的关键字，是一种同步锁。

### 修饰代码块
一个线程访问一个对象中的synchronized(this)同步代码块时，其他试图访问该对象的线程将被阻塞。

> 代码清单，synchronized修饰代码块用法

```
package synchronize;
public class SyncThread implements Runnable{
	private static int count=0;
	
	public SyncThread() {
		count=0;
	}
	
	public void run() {
		synchronized (this) {
			for(int i=0;i<5;i++){
				System.out.println(Thread.currentThread().getName()+" : "+(count++));
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	public int getCount(){
		return count;
	}
}
```

> 代码清单，synchronized修饰代码块测试用例

```
SyncThread syncThread1 = new SyncThread();
Thread thread1 = new Thread(syncThread1, "SyscThread1");
Thread thread2 = new Thread(syncThread1, "SyscThread2");
thread1.start();
thread2.start();
```

打印输出结果:
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191127152756987.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqMTg4MjY2,size_16,color_FFFFFF,t_70)

当两个并发线程(thread1和thread2)访问同一个对象(syncThread)中的synchronized代码块时，在同一时刻只能有一个线程得到执行，另一个线程受阻塞，必须等待当前线程执行完这个代码块以后才能执行该代码块。Thread1和thread2是互斥的，因为在执行synchronized代码块时会锁定当前的对象，只有执行完该代码块才能释放该对象锁，下一个线程才能执行并锁定该对象。

### 修饰方法
Synchronized修饰一个方法很简单，就是在方法的前面加synchronized，

```
public synchronized void method(){//todo};
```

synchronized修饰方法和修饰一个代码块类似，只是作用范围不一样，修饰代码块是大括号括起来的范围，而修饰方法范围是整个函数。

在用synchronized修饰方法时要注意以下几点：

虽然可以使用synchronized来定义方法，但synchronized并不属于方法定义的一部分，因此，synchronized关键字不能被继承。如果在父类中的某个方法使用了synchronized关键字，而在子类中覆盖了这个方法，在子类中的这个方法默认情况下并不是同步的，而必须显式地在子类的这个方法中加上synchronized关键字才可以。当然，还可以在子类方法中调用父类中相应的方法，这样虽然子类中的方法不是同步的，但子类调用了父类的同步方法，因此，子类的方法也就相当于同步了。这两种方式的例子代码如下：

> 在子类方法中加上synchronized关键字

```
class Parent {
   public synchronized void method() { }
}
class Child extends Parent {
   public synchronized void method() { }
}
```

> 在子类方法中调用父类的同步方法

```
class Parent {
   public synchronized void method() {   }
}
class Child extends Parent {
   public void method() { super.method();   }
}
```

 1. 在定义接口方法时不能使用synchronized关键字。
 2. 构造方法不能使用synchronized关键字，但可以使用synchronized代码块来进行同步。

**修饰一个静态的方法**
Synchronized也可修饰一个静态方法，用法如下：

```
public synchronized static void method() {
   // todo
}
```
我们知道静态方法是属于类的而不属于对象的。同样的，synchronized修饰的静态方法锁定的是这个类的所有对象。

> 代码清单，修饰静态方法

```
package synchronize;
public class synchronizestaticmethod implements Runnable {
	private static int count;
	public synchronizestaticmethod() {
		count = 0;
	}
	public synchronized static void method() {
		for (int i = 0; i < 5; i++) {
			try {
				System.out.println(Thread.currentThread().getName() + ":"
						+ (count++));
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public synchronized void run() {
		method();
	}
}
```

> 代码清单，修饰静态方法测试用例

```
synchronizestaticmethod syncThread1 = new synchronizestaticmethod();
synchronizestaticmethod syncThread2 = new synchronizestaticmethod();
Thread thread1 = new Thread(syncThread1, "SyscThread1");
Thread thread2 = new Thread(syncThread2, "SyscThread2");
thread1.start();
thread2.start();
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191127153342211.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqMTg4MjY2,size_16,color_FFFFFF,t_70)

syncThread1和syncThread2是synchronizestaticmethod 的两个对象，但在thread1和thread2并发执行时却保持了线程同步。这是因为run中调用了静态方法method，而静态方法是属于类的，所以syncThread1和syncThread2相当于用了同一把锁。这与下面代码是不同的。

> 代码清单，修饰方法

```
package synchronize;
public class synchronizestaticmethod implements Runnable {
	private static int count;
	public synchronizestaticmethod() {
		count = 0;
	}
	public synchronized void run() {
		for (int i = 0; i < 5; i++) {
			try {
				System.out.println(Thread.currentThread().getName() + ":"
						+ (count++));
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191127153526556.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqMTg4MjY2,size_16,color_FFFFFF,t_70)

这时创建了两个synchronizestaticmethod 的对象syncThread1和syncThread2，线程thread1执行的是syncThread1对象中的synchronized代码(run)，而线程thread2执行的是syncThread2对象中的synchronized代码(run)；我们知道synchronized锁定的是对象，这时会有两把锁分别锁定syncThread1对象和syncThread2对象，而这两把锁是互不干扰的，不形成互斥，所以两个线程可以同时执行。

### 修饰类

```
class ClassName {
   public void method() {
      synchronized(ClassName.class) {
         // todo
      }
   }
}
```

> 代码清单，修饰一个类

```
public class synchronizestaticmethod implements Runnable {
	private static int count;
	public synchronizestaticmethod() {
		count = 0;
	}
	public static void method() {
		synchronized (synchronizestaticmethod.class) {
			for (int i = 0; i < 5; i++) {
				try {
					System.out.println(Thread.currentThread().getName() + ":"
							+ (count++));
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public synchronized void run() {
		method();
	}
}
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191127153802436.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqMTg4MjY2,size_16,color_FFFFFF,t_70)

synchronized作用于一个类T时，是给这个类T加锁，T的所有对象用的是同一把锁。

### 总结

 1. 无论synchronized关键字加在方法上还是对象上，如果它作用的对象是非静态的，则它取得的锁是对象；如果synchronized作用的对象是一个静态方法或一个类，则它取得的锁是对类，该类所有的对象同一把锁。
 2. 每个对象只有一个锁（lock）与之相关联，谁拿到这个锁谁就可以运行它所控制的那段代码。
 3. 实现同步是要很大的系统开销作为代价的，甚至可能造成死锁，所以尽量避免无谓的同步控制。

 