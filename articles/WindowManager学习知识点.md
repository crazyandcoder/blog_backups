# WindowManager

> https://blog.csdn.net/yhaolpz/article/details/68936932

Window是一个抽象类，具体实现是PhoneWindow。创建一个Window是很简单的事情，只需要通过WindowManager即可完成。WindowManager是外界访问Window的入口，Window的具体实现位于WindowManagerService中，WindowManager和WIndow-ManagerService的交互是一个IPC过程。

![1383985148472qbne](media/15349220758891/1383985148472qbnem.jpg)


## WindowManagerService
WindowManagerService 就是位于 Framework 层的窗口管理服务，它的职责就是管理系统中的所有窗口。窗口的本质是什么呢？其实就是一块显示区域，在 Android 中就是绘制的画布：Surface，当一块 Surface 显示在屏幕上时，就是用户所看到的窗口了。WindowManagerService 添加一个窗口的过程，其实就是 WindowManagerService 为其分配一块 Surface 的过程，一块块的 Surface 在 WindowManagerService 的管理下有序的排列在屏幕上，Android 才得以呈现出多姿多彩的界面。


## Window分类
Window 有三种类型，分别是应用 Window、子 Window 和系统 Window。应用类 Window 对应一个 Acitivity，子 Window 不能单独存在，需要依附在特定的父 Window 中，比如常见的一些 Dialog 就是一个子 Window。系统 Window是需要声明权限才能创建的 Window，比如 Toast 和系统状态栏都是系统 Window。

Window 是分层的，每个 Window 都有对应的 z-ordered，层级大的会覆盖在层级小的 Window 上面，这和 HTML 中的 z-index 概念是完全一致的。在三种 Window 中，应用 Window 层级范围是 1~99，子 Window 层级范围是 1000~1999，系统 Window 层级范围是 2000~2999，我们可以用一个表格来直观的表示：


| Window | 层级 |
| --- | --- |
| 应用 Window | 1~99 |
| 子 Window | 1000~1999 |
| 系统Window | 2000~2999 |

**这些层级范围对应着 WindowManager.LayoutParams 的 type 参数，如果想要 Window 位于所有 Window 的最顶层，那么采用较大的层级即可，很显然系统 Window 的层级是最大的，当我们采用系统层级时，需要声明权限。**

## WindowManager使用
我们对 Window 的操作是通过 WindowManager 来完成的，WindowManager 是一个接口，它继承自只有三个方法的 ViewManager 接口：


```
public interface ViewManager{
    public void addView(View view, ViewGroup.LayoutParams params);
    public void updateViewLayout(View view, ViewGroup.LayoutParams params);
    public void removeView(View view);
}
```

这三个方法其实就是 WindowManager 对外提供的主要功能，即添加 View、更新 View 和删除 View。 



## WindowManager内部机制
在实际使用中无法直接访问 Window，对 Window 的访问必须通过 WindowManager。WindowManager 提供的三个接口方法 addView、updateViewLayout 以及 removeView 都是针对 View 的，这说明 View 才是 Window 存在的实体。

WindowManager 是一个接口，它的真正实现是 WindowManagerImpl 类：


```
@Override
        public void addView(View view, ViewGroup.LayoutParams params){
            mGlobal.addView(view, params, mDisplay, mParentWindow);
        }

        @Override
        public void updateViewLayout(View view, ViewGroup.LayoutParams params){
            mGlobal.updateViewLayout(view, params);
        }

        @Override
        public void removeView(View view){
            mGlobal.removeView(view, false);
        }
```

可以看到，WindowManagerImpl 并没有直接实现 Window 的三大操作，而是交给了 WindowManagerGlobal 来处理。

### addView实现原理

```
@Override
    public void addView(@NonNull View view, @NonNull ViewGroup.LayoutParams params) {
        applyDefaultToken(params);
        mGlobal.addView(view, params, mContext.getDisplay(), mParentWindow);
    }
```

**检查参数合法性，如果是子 Window 做适当调整**

```
if(view == null){
   throw new IllegalArgumentException("view must not be null");
}

if(display == null){
   throw new IllegalArgumentException("display must not be null");
}

if(!(params instanceof WindowManager.LayoutParams)){
   throw new IllegalArgumentException("Params must be WindowManager.LayoutParams");
}

final WindowManager.LayoutParams wparams = (WindowManager.LayoutParams)params;
if(parentWindow != null){
   parentWindow.adjustLayoutParamsForSubWindow(wparams);
}
```

 **创建 ViewRootImpl 并将 View 添加到集合中**
 
在 WindowManagerGlobal 内部有如下几个集合比较重要：

```
private final ArrayList<View> mViews = new ArrayList<View>();
private final ArrayList<ViewRootImpl> mRoots = new ArrayList<ViewRootImpl>();
private final ArrayList<WindowManager.LayoutParams> mParams = new ArrayList<WindowManager.LayoutParams>();
private final ArraySet<View> mDyingViews = new ArraySet<View>();
```
 
 其中 mViews 存储的是所有 Window 所对应的 View，mRoots 存储的是所有 Window 所对应的 ViewRootImpl，mParams 存储的是所有 Window 所对应的布局参数，mDyingViews 存储了那些正在被删除的 View 对象，或者说是那些已经调用了 removeView 方法但是操作删除还未完成的 Window 对象，可以通过表格直观的表示：
 
 
| 集合 | 存储内容 |
| --- | --- |
| mViews<span class="Apple-tab-span" style="white-space:pre"></span> | Window 所对应的 View |
| mRoots<span class="Apple-tab-span" style="white-space:pre"></span> | Window 所对应的 ViewRootImpl |
| mParams<span class="Apple-tab-span" style="white-space:pre"></span> | Window 所对应的布局参数 |
| mDyingViews<span class="Apple-tab-span" style="white-space:pre"></span> | 正在被删除的 View 对象 |


addView 操作时会将相关对象添加到对应集合中：


```
root = new ViewRootImpl(view.getContext(),display);
view.setLayoutParams(wparams);
mViews.add(view);
mRoots.add(root);
mParams.add(wparams);
```

 
**通过 ViewRootImpl 来更新界面并完成 Window 的添加过程**

在学习 View 的工作原理时，我们知道 View 的绘制过程是由 ViewRootImpl 来完成的，这里当然也不例外，具体是通过 ViewRootImpl 的 setView 方法来实现的。在 setView 内部会通过 requestLayout 来完成异步刷新请求，如下：

```
 @Override
    public void requestLayout() {
        if (!mHandlingLayoutInLayoutRequest) {
            checkThread();
            mLayoutRequested = true;
            scheduleTraversals();
        }
    }
```

可以看到 scheduleTraversals 方法是 View 绘制的入口，继续查看它的实现：

```
res = mWindowSession.addToDisplay(mWindow, mSeq, mWindowAttributes, getHostVisibility(), 
          mDisplay.getDisplayId(),mAttachInfo.mContentInsets, mInputChannel);
```

mWindowSession 的类型是 IWindowSession，它是一个 Binder 对象，真正的实现类是 Session，这也就是之前提到的 IPC 调用的位置。在 Session 内部会通过 WindowManagerService 来实现 Window 的添加，代码如下：


```
public int addToDisplay(IWindow window, int seq, WindowManager.LayoutParams, attrs, int viewVisibility, 
                  int displayId, Rect outContentInsets, InputChannel outInputChannel){
   return mService.addWindow(this, window, seq, attrs, viewVisibility, displayId, outContentInsets, outInputChannel);
}
```

终于，Window 的添加请求移交给 WindowManagerService 手上了，在 WindowManagerService 内部会为每一个应用保留一个单独的 Session，具体 Window 在 WindowManagerService 内部是怎么添加的，就不对其进一步的分析，因为到此为止我们对 Window 的添加这一从应用层到 Framework 的流程已经清楚了，下面通过图示总结一下： 

![屏幕快照 2018-08-23 11.14.54](media/15349220758891/%E5%B1%8F%E5%B9%95%E5%BF%AB%E7%85%A7%202018-08-23%2011.14.54.png)

理解了 Window 的添加过程，Window 的删除过程和更新过程都是类似的，也就容易理解了，它们最终都会通过一个 IPC 过程将操作移交给 WindowManagerService 这个位于 Framework 层的窗口管理服务来处理。


## Window 的创建过程
View 是 Android 中的视图的呈现方式，但是 View 不能单独存在，它必须附着在 Window 这个抽象的概念上面，因此有视图的地方就有 Window。哪些地方有视图呢？Android 可以提供视图的地方有 Activity、Dialog、Toast，除此之外，还有一些依托 Window 而实现的视图，比如 PopUpWindow（自定义弹出窗口）、菜单，它们也是视图，有视图的地方就有 Window，因此 Activity、Dialog、Toast 等视图都对应着一个 Window。这也是面试中常问到的一个知识点：一个应用中有多少个 Window？下面分别分析 Activity、Dialog以及 Toast 的 Window 创建过程。

### Activity 的 Window 创建过程
Window 本质就是一块显示区域，所以关于 Activity 的 Window 创建应该发生在 Activity 的启动过程，Activity 的启动过程很复杂，最终会由 ActivityThread 中的 performLaunchActivity() 来完成整个启动过程，在这个方法内部会通过类加载器创建 Activity 的实例对象，并调用其 attach 方法为其关联运行过程中所依赖的一系列上下文环境变量。

Activity 的 Window 创建就发生在 attach 方法里，系统会创建 Activity 所属的 Window 对象并为其设置回调接口，代码如下：


```
        mWindow = new PhoneWindow(this, window, activityConfigCallback);
        mWindow.setWindowControllerCallback(this);
        mWindow.setCallback(this);
        mWindow.setOnWindowDismissedCallback(this);
        mWindow.getLayoutInflater().setPrivateFactory(this);
        if (info.softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED) {
            mWindow.setSoftInputMode(info.softInputMode);
        }
        if (info.uiOptions != 0) {
            mWindow.setUiOptions(info.uiOptions);
        }
```

由于 Activity 实现了 Window 的 Callback 接口，因此当 Window 接受到外界的状态改变时就会回调 Activity 的方法。Callback 接口中的方法很多，有几个是我们非常熟悉的，如 onAttachedToWindow、onDetachedFromWindow、dispatchTouchEvent 等等。

到这里 Window 以及创建完成了，下面分析 Activity 的视图是怎么附属到 Window 上的，而 Activity 的视图由 setContentView 提供，所以从 setContentView 入手，它的源码如下：

```
public void setContentView(@LayoutRes int layoutResID) {
        getWindow().setContentView(layoutResID);
        initWindowDecorActionBar();
    }
```

可以看到，Activity 将具体实现交给了 Window，而 Window 的具体实现是 PhoneWindow，所以只需要看 PhoneWindow 的相关逻辑即可，它的处理步骤如下：

**(1)、如果没有 DecorView 就创建一个**

DecorView 是 Activity 中的顶级 View，是一个 FrameLayout，一般来说它的内部包含标题栏和内容栏，但是这个会随着主题的变化而改变，不管怎么样，内容栏是一定存在的，并且有固定的 id：”android.R.id.content”，在 PhoneWindow 中，通过 generateDecor 方法创建 DecorView，通过 generateLayout 初始化主题有关布局。

**(2)、将 View 添加到 DecorView 的mContentParent 中**

这一步较为简单，直接将 Activity 的视图添加到 DecorView 的 mContentParent 中即可，由此可以理解 Activity 的 setContentView 这个方法的来历了，为什么不叫 setView 呢？因为 Activity 的布局文件只是被添加到 DecorView 的 mContentParent 中，因此叫 setContentView 更加具体准确。

**(3)、回调 Activity 的 onContentChanged 方法通知 Activity 视图已经发生改变**

前面分析到 Activity 实现了 Window 的 Callback 接口，这里当 Activity 的视图已经被添加到 DecorView 的 mContentParent 中了，需要通知 Activity，使其方便做相关的处理。

经过上面的三个步骤，DecorView 已经被创建并初始化完毕，Activity 的布局文件也已经成功添加到了 DecorView 的 mContentParent 中，但是这个时候 DecorView 还没有被 WindowManager 正式添加到 Window 中。在 ActivityThread 的 handleResumeActivity 方法中，首先会调用 Acitivy 的 onResume 方法，接着会调用 Acitivy 的 makeVisible() 方法，正是在 makeVisible 方法中，DecorView 才真正的完成了显示过程，到这里 Activity 的视图才能被用户看到，如下：


```
 void makeVisible() {
        if (!mWindowAdded) {
            ViewManager wm = getWindowManager();
            wm.addView(mDecor, getWindow().getAttributes());
            mWindowAdded = true;
        }
        mDecor.setVisibility(View.VISIBLE);
    }
```


### Dialog 的 Window 创建过程

Dialog 的 Window 的创建过程与 Activity 类似，步骤如下：

**(1)、创建 Window**

```
        final Window w = new PhoneWindow(mContext);
        mWindow = w;
        w.setCallback(this);
        w.setOnWindowDismissedCallback(this);
        w.setOnWindowSwipeDismissedCallback(() -> {
            if (mCancelable) {
                cancel();
            }
        });
```

**(2)、初始化 DecorView 并将 Dialog 的视图添加到 DecorView 中**

```
public void setContentView(int layoutResID){
   mWindow.setContentView(layoutResID);
}
```

**(3)、将 DecorView 添加到 Window 中并显示**
在 Dialog 的 show 方法中，会通过 WindowManager 将 DecorView 添加到 Window 中，如下：

```
mWindowManager.addView(mDecor, 1);
mShowing = true;
```

从上面三个步骤可以发现，Dialog 的 Window 创建过程和 Activity 创建过程很类似，当 Dialog 关闭时，它会通过 WindowManager 来移除 DecorView。**普通的 Dialog 必须采用 Activity 的 Context，如果采用 Application 的 Context 就会报错。这是因为没有应用 token 导致的，而应用 token 一般只有 Activity 拥有，另外，系统 Window 比较特殊，可以不需要 token。**


### Toast 的 Window 创建过程

```
Toast.makeText(this,"some msg",Toast.LENGTH_LONG).show();
```

Toast 与 Dialog 不同，它的工作过程稍显复杂，首先 Toast 也是基于 Window 来实现的，但是由于 Toast 具有定时取消这一功能，所以系统采用了 Handler。在 Toast 内部有两类 IPC 过程，一是 Toast 访问 NotificationManagerService，第二类是 NotificationManagerService 回调 Toast 里的 TN 接口。NotificationManagerService 同 WindowManagerService 一样，都是位于 Framework 层的服务，下面简称 NotificationManagerService 为 NMS。

Toast 属于系统 Window，它内部的视图可以是系统默认样式也可以通过 setView 方法自定义 View，不管如何，它们都对应 Toast 的内部成员 mNextView，Toast 提供 show 和 cancel 分别用于显示和隐藏 Toast，它们内部是一个 IPC 过程，代码如下：


```
@Override
public void show(IBinder windowToken) {
       mHandler.obtainMessage(SHOW, windowToken).sendToTarget();
}
```

利用一个Handler发送一个SHOW消息，然后在Handler中进行处理


```
mHandler = new Handler(looper, null) {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case SHOW: {
                            IBinder token = (IBinder) msg.obj;
                            handleShow(token);
                            break;
                        }
                        case HIDE: {
                            handleHide();
                            // Don't do this in handleHide() because it is also invoked by
                            // handleShow()
                            mNextView = null;
                            break;
                        }
                        case CANCEL: {
                            handleHide();
                            // Don't do this in handleHide() because it is also invoked by
                            // handleShow()
                            mNextView = null;
                            try {
                                getService().cancelToast(mPackageName, TN.this);
                            } catch (RemoteException e) {
                            }
                            break;
                        }
                    }
                }
            };
```


Toast 毕竟是要在 Window 中实现的，因此它最终还是要依附于 WindowManager，


```
mWM = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
mWM.addView(mView, mParams);
```


## 总结

下面让我们再次认清一些概念：任何 View 都是附属在一个 Window 上面的，Window 表示一个窗口的概念，也是一个抽象的概念，Window 并不是实际存在的，它是以 View 的形式存在的。WindowManager 是外界也就是我们访问 Window 的入口，Window 的具体实现位于 WindowManagerService 中，WindowManagerService 和 WindowManager 的交互是一个 IPC 过程。


