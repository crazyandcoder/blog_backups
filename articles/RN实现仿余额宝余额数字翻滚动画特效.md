---
title: RN实现仿余额宝余额数字翻滚动画特效
categories:
  - RN
tags:
  - 'ReactNative'
  - '动画'
comments: true
date: 2019-12-17 16:37:23
img:  https://img-blog.csdnimg.cn/20191217164029280.jpg
---

## 前语
前段时间公司有个需求，需要实现一个数字翻滚的动画，要求为：数字格式化为每隔3位逗号分隔，数字最后三位执行动画，从左往右，依次翻滚，翻滚时间持续3秒钟，类似于余额宝金额滚动的动画，要求RN实现，效果最终实现如下：

![](https://img-blog.csdnimg.cn/20191217150956649.gif)
在此记录总结一下实现思路细节，有需要的可以参考一下。

## 实现思路
首先需要理一下实现要求，总的要求如下：

 1. 用ReactNative实现该动画
 2. 数字超过三位需要格式化，少于三位不需要，正常显示
 3. 最后三位执行动画
 4. 最后三位从左到右，从下往上依次执行动画，
 5. 动画间隔时间3秒钟

以上便是这次动画实现的要求，接下来我们依次分解上面的要求，分别实现。

### ReactNative实现
首先这是用RN实现的，这没啥好说的。主要使用RN中提供的Animated来实现该动画。关于Animated简单了解一下，Animated提供了三种动画类型。每种动画类型都提供了特定的函数曲线，用于控制动画值从初始值变化到最终值的变化过程：

 1. Animated.decay()以指定的初始速度开始变化，然后变化速度越来越慢直至停下。
 2. Animated.spring()提供了一个简单的弹簧物理模型.
 3. Animated.timing()使用easing 函数让数值随时间动起来。

 在本次动画中，我们主要使用第三种方式来实现该动画。我们来详细分析一下该动画。首先它的数字是循环滚动的，即数字从0，1，2，3，4，5。。。9，0，1，2，3，4，5。。。9这种方式循环滚动的:

![](https://img-blog.csdnimg.cn/2019121716051629.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqMTg4MjY2,size_16,color_FFFFFF,t_70)


如何能达到这种效果呢？我们可以通过一个数组来实现，即：

```
// 创建"0","1","2","3","4"..."9"的数组,默认绘制数据
const resourceData = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"];
```

使用Animated.timing来实现，这就需要我们提供一个fromValue和toValue来实现。我们可以给这个数组进行编号：

![](https://img-blog.csdnimg.cn/2019121715534011.png)
假设，我们需要将数字从3滚到7，那么根据上面的编号，我们只需要将上面的fromValue设置成-30，toValue设置成-70即可。如果需要继续滚，将数字7滚到数字6的话，因为需要连续滚动而不能往回滚，所以需要将fromValue设置成-70，然后将toValue设置成-160，滚动到-160的时候，我们需要立即将该坐标回到起始坐标-60，这样如果下次数字再来的话，我们直接滚动即可，这样数组就不会越界了。这便是上面的思路。用代码实现如下：

#### 计算数组下标

```
/**
 * getPosition这个方法是用来计算目标数字的y轴坐标值，
 * 根据当前数字在数组中的下标乘以测量出的数字文本绘制高度取负值，得出坐标值。
 * @param {*} param0 
 */
const getPosition = ({ text, items, height }) => {
    // 获得文本在数组的下标
    return parseInt(text) * height * -1;
};
```

计算出数组下标后，我们需要设置fromValue和toValue。

#### 计算fromValue

```
		let nowValue = parseInt(this.props.text);
        let now = nowValue + 10 - this.props.rollNum;

        let init = getPosition({
            text: "" + now,
            items: this.props.rotateItems,
            height: this.props.height,
        });

        this.setState({
            animation: new Animated.Value(
                init
            ),
        });
```

#### 计算toValue

```
componentDidUpdate(prevProps) {
        const { height, duration, rotateItems, text } = this.props;
        //第一次进来，不执行动画
        var endValue = getPosition({
            text: "" + (parseInt(this.props.text) + 10),
            items: rotateItems,
            height,
        });
    }
```

#### 执行动画

```
 		// 数字变化,用当前动画值和变化后的动画值进行插值,并启动动画
        Animated.timing(this.state.animation, {
            toValue: endValue,
            duration,
            useNativeDriver: true,
        }).start();
```


### 格式化数字
格式化数字很简单，需要判断是否存在小数点的情况：

```
formatNumber = numStr => {
    numStr += '';
    x = numStr.split('.');
    x1 = x[0];
    x2 = x.length > 1 ? '.' + x[1] : '';
    var rgx = /(\d+)(\d{3})/;
    while (rgx.test(x1)) {
      x1 = x1.replace(rgx, '$1' + ',' + '$2');
    }
    return x1 + x2;
  }
```

以上便是实现该动画需要注意的地方，最后提供所有的代码，有需要的可以参考一下。[NumberTickerDemo](https://github.com/crazyandcoder/awesome-reactnative/tree/master/NumberTickerDemo)
