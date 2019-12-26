---
title: 排序算法总结-Java实现
categories:
  - DSA
tags:
  - '排序算法'
  - ''
comments: true
date: 2019-12-05 17:26:00
img: https://img-blog.csdnimg.cn/2019120517300436.png
---

一直以来都想记录一下关于算法和数据结构方面的东西，在android开发过程中很少专门使用这些算法以及数据结构，所以有点生疏，没有系统的学习这方面的知识，所以打算记录总结记录常用的算法以及相应的数据结构，以备查阅。

## 排序
### 插入排序
#### 直接插入排序
**基本思想：**
在要排序的一组数中，假设前面(n-1) [n>=2] 个数已经是排 好顺序的，现在要把第n个数插到前面的有序数中，使得这n个数
也是排好顺序的。如此反复循环，直到全部排好顺序。

```
    public void insertSort(int[] a) {
        System.out.println("\n排序前数据：");
        for (int i = 0; i < a.length; i++) {
            System.out.print("  " + a[i]);
        }
        // 数组长度
        int length = a.length;
        // 要插入的数
        int insertNum;
        // 插入的次数
        for (int i = 1; i < length; i++) {
            // 要插入的数
            insertNum = a[i];
            // 已经排序好的序列元素个数
            int j = i - 1;
            // 序列从后到前循环，将大于insertNum的数向后移动一格
            while (j >= 0 && a[j] > insertNum) {
                // 元素移动一格
                a[j + 1] = a[j];
                j--;
            }
            // 将需要插入的数放在要插入的位置。
            a[j + 1] = insertNum;
        }
        System.out.println("\n排序后数据：");
        for (int i = 0; i < a.length; i++) {
            System.out.print("  " + a[i]);
        }
    }
```
**直接插入排序输出:**

```
排序前数据：
  32  43  23  13  5
排序后数据：
  5  13  23  32  43
```

#### 希尔排序
**基本思想**
希尔排序的诞生是由于插入排序在处理大规模数组的时候会遇到需要移动太多元素的问题。希尔排序的思想是将一个大的数组“分而治之”，划分为若干个小的数组，以 gap 来划分，比如数组 [1, 2, 3, 4, 5, 6, 7, 8] ，如果以 gap = 2 来划分，可以分为 [1, 3, 5, 7] 和 [2, 4, 6, 8] 两个数组（对应的，如 gap = 3 ，则划分的数组为： [1, 4, 7] 、 [2, 5, 8] 、 [3, 6] ）然后分别对划分出来的数组进行插入排序，待各个子数组排序完毕之后再减小 gap 值重复进行之前的步骤，直至 gap = 1 ，即对整个数组进行插入排序，此时的数组已经基本上快排好序了，所以需要移动的元素会很小很小，解决了插入排序在处理大规模数组时较多移动次数的问题。

```
/**
     * 希尔排序
     *
     * @param a
     */
    public void shellSort(int a[]) {
        FormatPrint.print("排序前数据：", a);
        int length = a.length;
        int temp = 0;
        while (true) {
            //增量
            length = length / 2;
            //分成的组数
            for (int x = 0; x < length; x++) {
                //每组内进行直接插入排序
                for (int i = x + length; i < a.length; i += length) {
                    int j = i - length;
                    temp = a[i];
                    for (; j >= 0 && temp < a[j]; j -= length) {
                        a[j + length] = a[j];
                    }
                    a[j + length] = temp;
                }
            }
            if (length == 1) {
                break;
            }
        }
        FormatPrint.print("排序后数据：", a);
    }
```


### 选择排序
#### 简单选择排序
**基本思想**
在要排序的一组数中，选出最小的一个数与第一个位置的数交换；然后在剩下的数当中再找最小的与第二个位置的数交换，如此循环到倒数第二个数和最后一个数比较为止。

```
/**
     * 简单选择排序
     *
     * @param a
     */
    public void simpleSort(int[] a) {
        FormatPrint.print("排序前数据：", a);
        int d = a.length;
        int position = 0;
        for (int i = 0; i < d; i++) {
            // 待比较的数的下标
            position = i;
            // 待比较的数
            int temp = a[i];
            // 遍历待比较后面的数，取出最小的数与待比较的数进行交换
            for (int j = i + 1; j < d; j++) {
                // 有较小的数，则保存下标，进行交换
                if (a[j] < temp) {
                    position = j;
                    temp = a[j];
                }
            }
            // 与待比较的数进行交换位置
            a[position] = a[i];
            a[i] = temp;
        }
        FormatPrint.print("排序后数据：", a);
    }
```
### 交换排序
#### 冒泡排序

冒泡排序（Bubble Sort）是一种简单的排序算法。它重复地走访过要排序的数列，一次比较两个元素，如果他们的顺序错误就把他们交换过来。走访数列的工作是重复地进行直到没有再需要交换，也就是说该数列已经排序完成。这个算法的名字由来是因为越小的元素会经由交换慢慢“浮”到数列的顶端。

**基本思想**
 1. 比较相邻的元素。如果第一个比第二个大，就交换他们两个。
 2. 对每一对相邻元素作同样的工作，从开始第一对到结尾的最后一对。在这一点，最后的元素应该会是最大的数。
 3. 针对所有的元素重复以上的步骤，除了最后一个。
 4. 持续每次对越来越少的元素重复上面的步骤，直到没有任何一对数字需要比较。

```
	/**
	 * 冒泡排序
	 * 
	 * @param a
	 */
	public void bubbleSort(int a[]) {
		FormatPrint.print("排序前数据：", a);
		int temp = 0;
		int size = a.length;
		for (int i = 0; i < size-1; i++) {
			for (int j = 0; j < size - i - 1; j++) {
				if (a[j] > a[j + 1]) {
					temp = a[j];
					a[j] = a[j + 1];
					a[j + 1] = temp;
				}
			}
		}
		FormatPrint.print("排序后数据：", a);
	}
```

