---
title: Android中关于Context的三言两语
categories:
  - Android
tags:
  - 'Android'
  - ''
comments: true
date: 2019-11-26 16:09:32
img: https://img-blog.csdnimg.cn/20191126161045450.jpg
---

## 前言
今天我们来分析一下 Context 的源码，在 APP 开发中，我们会经常用到 Context ，那么什么是 Context 呢？它的常规语义是“上下文”那么这个“上下文”到底是什么呢？通过源码分析，我们能对这个Context有个基本的认识。

### 类继承图
我们来看下关于 Context 的类继承图，我们通过查看源码得知，Context 是一个抽象类，所以它肯定有其实现类，查阅得知它的实现类为 ContextWrapper 和 ContextImpl ，所以它的继承图如下：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191126155224483.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqMTg4MjY2,size_16,color_FFFFFF,t_70)

以上的 Context 类继承关系清晰简洁，可以得知，Application 、 Service 、Activity 都是继承的 Context 类，所以从这里我们可以得知：

```
Context 数量 = Activity 数量 + Service 数量 + 1
```

另外，我们可以看到 Application 和 Service 都是直接继承 ContextWrapper 的而 Activity 却是继承 ContextThemeWrapper 的，这是为何？其实 ContextThemeWrapper 是关于主题类的，Activity 是有界面的，而 Application 和 Service 却没有。接下来我们来详细看下它们的源码实现。

### ContextWrapper
我们进入到 ContextWrapper 源码中可以发现，它其实调用了 mBase 里面的方法，而 mBase 其实是 ContextImpl ，所以最终还是得调用它的实现类 ContextImpl 类里面的方法。


```
public class ContextWrapper extends Context {
    Context mBase;
    public ContextWrapper(Context base) {
        mBase = base;
    }
    protected void attachBaseContext(Context base) {
        if (mBase != null) {
            throw new IllegalStateException("Base context already set");
        }
        mBase = base;
    }
    //其余的都是覆盖Context里面的方法
}
```

我们可以按照上面的类的继承图进行依次分析，由上面可以知道 ContextWrapper 其实是调用 ContextImpl 里面的方法，所以 Application 和 Service 还有 Activity 它们应该都跟 ContextImpl 有关的。到底是不是这样的呢？我们追踪源码进行分析。

### Application 
类似于 Java 的 main 启动方法程序，Android 也有一个类似的方法，那就是在 ActivityThread 类中也有一个 main ，这是开始的地方，我们从这里进行一点一点跟踪：

> ActivityThread#main

```
      //省略部分代码...
	  Looper.prepareMainLooper();
      ActivityThread thread = new ActivityThread();
      thread.attach(false);
      //省略部分代码...
      Looper.loop();      
      //省略部分代码...
```

我们找到 ActivityThread 的 main 方法，省略无关代码，这个 main 方法就是不断的从消息队列中获取消息，然后进行处理。我们本次不分析 Looper 相关的东西，只分析跟 Context 有关的内容，继续进入 attach 方法，

**Android 分析源码，不能一头扎进去，我们应该主要分析它的流程。**

> ActivityThread#attach

```
//省略部分代码...
				mInstrumentation = new Instrumentation();
                ContextImpl context = ContextImpl.createAppContext(
                        this, getSystemContext().mPackageInfo);
				//Application的实例创建
                mInitialApplication = context.mPackageInfo.makeApplication(true, null);
                
                //调用Application里面的生命周期方法onCreate
                mInitialApplication.onCreate();
//省略部分代码...
```
这里面出现了 ContextImpl ，所以下面应该会跟 Application 扯上关系，所以进入到 makeApplication 方法中继续往下追踪，

> LoadedApk#makeApplication

```
//省略部分代码...
  Application app = null;
 ContextImpl appContext = ContextImpl.createAppContext(mActivityThread, this);
            app = mActivityThread.mInstrumentation.newApplication(
                    cl, appClass, appContext);
            appContext.setOuterContext(app);
//省略部分代码...
```
最终又进入到 Instrumentation#newApplication 方法里面

> Instrumentation#newApplication

```
static public Application newApplication(Class<?> clazz, Context context)
            throws InstantiationException, IllegalAccessException, 
            ClassNotFoundException {
        Application app = (Application)clazz.newInstance();
        app.attach(context);
        return app;
    }
```

> Application#attach

```
	/**
    * @hide
    */
   /* package */ 
   final void attach(Context context) {
       attachBaseContext(context);
       mLoadedApk = ContextImpl.getImpl(context).mPackageInfo;
   }
```
走到这里就很明清晰了，最终将会调用 ContextWrapper 的 attachBaseContext 方法。从上面到这里，如预料的一样，分析到这里，记住了多少？是不是只知道 Application 里面最终会调用 attachBaseContext 这个方法？这样的话就对了，不能一头扎进代码的海洋里，到处遨游，那样会迷失方向的，Android 源码那么大，那么多，一一细节分析根本是不大可能的，所以只能把握流程，然后再针对性的分析实现过程。接着分析 Service 里面相关的方法。

### Service 
对于 Service ，我们在 ActivityThread 中可以发现有个方法叫 handleCreateService ，这里面有关于 Service 和 ContextImpl 之间的联系。

> ActivityThread#handleCreateService

```
Service service = null;
ContextImpl context = ContextImpl.createAppContext(this, packageInfo);
           context.setOuterContext(service);
           Application app = packageInfo.makeApplication(false, mInstrumentation);
           service.attach(context, this, data.info.name, data.token, app,
                   ActivityManager.getService());
           service.onCreate();
```
对于 Application 的那段代码我们可以发现，这两者及其类似，我们进入到 attach 方法中查看相关代码，发现

```
/**
    * @hide
    */
   public final void attach(
           Context context,
           ActivityThread thread, String className, IBinder token,
           Application application, Object activityManager) {
	//调用attachBaseContext方法
       attachBaseContext(context);
       mThread = thread;           // NOTE:  unused - remove?
       mClassName = className;
       mToken = token;
       mApplication = application;
       mActivityManager = (IActivityManager)activityManager;
       mStartCompatibility = getApplicationInfo().targetSdkVersion
               < Build.VERSION_CODES.ECLAIR;
   }
```

代码很简单，就是这样跟 ContextImpl 扯上关系的。因为 Service 和 Application 都是继承的 ContextWrapper 类，接下来我们来分析一下关于 Activity 的代码。

### Activity  
在这里说明一下为什么 Service 和 Application 都是继承的 ContextWrapper 类而 Activity 却是继承 ContextThemeWrapper 那是因为 Activity 是带有界面显示的，而 Service 和 Application 却没有，所以从名字我们可以看到 ContextThemeWrapper 包含主题的信息，同时 ContextThemeWrapper 却又是继承自 ContextWrapper ，分析 ContextThemeWrapper 源码我们可以看到，里面基本都是关于 theme 的方法，同时它也覆盖了 attachBaseContext 方法。

我们进入 Activity 源码也发现它也有和 Service 类似的 attach 方法

```
final void attach(Context context, ActivityThread aThread,
            Instrumentation instr, IBinder token, int ident,
            Application application, Intent intent, ActivityInfo info,
            CharSequence title, Activity parent, String id,
            NonConfigurationInstances lastNonConfigurationInstances,
            Configuration config, String referrer, IVoiceInteractor voiceInteractor,
            Window window, ActivityConfigCallback activityConfigCallback) {
		//省略部分代码...
        attachBaseContext(context);
```

接下来我们来分析一下 Activity 在哪里和这个扯上关系的。

> ActivityThread#performLaunchActivity

performLaunchActivity 这个方法其实就是启动 Activity 的方法 ，我们以后再来学习关于这个方法的内容，现在先分析 Context 的内容。我们进入到这个方法查看：

```
//省略部分代码...
ContextImpl appContext = createBaseContextForActivity(r);
Activity activity = null;
//省略部分代码...
  activity.attach(appContext, this, getInstrumentation(), r.token,
                        r.ident, app, r.intent, r.activityInfo, title, r.parent,
                        r.embeddedID, r.lastNonConfigurationInstances, config,
                        r.referrer, r.voiceInteractor, window, r.configCallback);
//省略部分代码...
```
首先通过 createBaseContextForActivity 方法创建ContextImpl 然后直接有 Activity attach 进去。到此为止，关于 Application 、Service 和 Activity 关于Context 的源码基本就差不多了。接下来我们来解决一些实际的内容。

### 实例理解
既然 Application、Service 和 Activity 都有 Context 那么它们之间到底有啥区别呢？同时 getApplicationContext 和 getApplication() 又有什么区别呢？接下来我们通过代码进行验证。

我们现在的项目一般都有自定义 Application 的类进行一些初始化操作，本例中也新建一个 MyApplication 的类继承自 Application，然后在Manifest.xml中进行注册，代码如下:

```
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("androidos_analysis", "getApplicationContext()——> " + getApplicationContext());
        Log.d("androidos_analysis", "getBaseContext()       ——> " + getBaseContext());
    }
}
```
打印结果如下：

```
getApplicationContext()——> com.ihidea.androidosanalysis.MyApp@9831cf9
getBaseContext()       ——> android.app.ContextImpl@13d643e
```
我们发现当我们通过 getApplicationContext 获取的是我们申明的 Application 实例，而通过 getBaseContext 获取到的却是 ContextImpl 这是为什么呢？我们查看它们的实现发现

> ContextWrapper#getBaseContext

```
/**
   * @return the base context as set by the constructor or setBaseContext
   */
  public Context getBaseContext() {
      return mBase;
  }
```
其实在上文我们已经分析过了它们的源码，我们知道其实这个mBase就是 ContextImpl 了。而 getApplicationContext

> ContextWrapper#getApplicationContext

```
@Override
  public Context getApplicationContext() {
      return mBase.getApplicationContext();
  }
```
通过上面分析我们知道 其实 Application 它本身也是一个 Context 所以，这个们返回的就是它自己了。所以这里获取getApplicationContext()得到的结果就是MyApplication本身的实例。

有时候我们代码里面也会有关于 getApplication 的用法，那么 这个跟 getApplicationContext 又有什么区别呢？我们再来log一下就知道了。

我们创建一个 MainActivity 然后在里面打印两行代码：

> MainActivity#onCreate

```
Log.d("androidos_analysis", "getApplicationContext()——> " + getApplicationContext());
Log.d("androidos_analysis", "getApplication()       ——> " + getApplication());
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191126160030557.png)


我们可以发现 这两个返回的结果都是 一样的，其实不难理解，

> Activity#getApplication

```
/** Return the application that owns this activity. */
   public final Application getApplication() {
       return mApplication;
   }
```
其实 getApplication 返回的就是 Application 所以这两者是一样的了。但是都是返回的 Application ，Android 为什么要存在这两个方法呢？这就涉及到作用域的问题了，我们可以发现使用 getApplication 的方法的作用范围是 Activity 和 Service ，但是我们在其他地方却不能使用这个方法，这种情况下我们就可以使用 getApplicationContext 来获取 Application 了。什么情况下呢？譬如：BroadcastReceiver 我们想在Receiver 中获取 Application 的实例我们就可以通过这种方式来获取：

```
public class MyReceiver extends BroadcastReceiver {  
    @Override  
    public void onReceive(Context context, Intent intent) { 
        MyApplication myApp = (MyApplication) context.getApplicationContext();  
        //...
    }  
}
```
以上内容就是关于 Context 的部分内容。

 