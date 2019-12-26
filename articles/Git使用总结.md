---
title: Git使用总结
categories:
  - Android
tags:
  - 'Git'
  - '思维导图'
comments: true
date: 2019-12-24 14:22:23
img: https://img-blog.csdnimg.cn/20191224142357185.jpg
---


![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/12/24/16f35d82d9848d00?w=1198&h=844&f=png&s=250156)

本文末尾 **微信公众号** 回复 **“git”** 获取git命令总结思维导图。原文链接：[https://github.com/crazyandcoder/awesome-git](https://github.com/crazyandcoder/awesome-git) 

## Git
Git是目前世界上最先进的分布式版本控制系统。

### 1. 版本控制
所谓版本控制就是在文件的修改历程中保留修改历史，让你可以方便地查询历史提交记录以及撤销之前对文件的修改操作。版本控制系统主要有集中式版本控制系统和分布式版本控制系统两种。

#### **1.1 集中式版本控制系统**

集中式版本控制系统，版本库是集中存放在中央服务器的，工作时需要先从中央服务器取得最新的版本，然后开始干活，干完活了，再把自己的活推送给中央服务器。

![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/12/24/16f35d82dbc869cb?w=638&h=316&f=png&s=30447)

#### **1.2 分布式版本控制系统**

分布式版本控制系统根本没有“中央服务器”，每个人的电脑上都是一个完整的版本库，这样，你工作的时候，就不需要联网了，因为版本库就在你自己的电脑上。既然每个人电脑上都有一个完整的版本库，那多个人如何协作呢？比方说你在自己电脑上改了文件A，你的同事也在他的电脑上改了文件A，这时，你们俩之间只需把各自的修改推送给对方，就可以互相看到对方的修改了。

![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/12/24/16f35d82dcf34761?w=762&h=398&f=png&s=31457)

### 2. Git理论基础
学习git首先需要了解其中涉及到的四个重要概念：

 1. 远程仓库(Remote Directory)
 2. 工作目录（Working Directory）
 3. 暂存区(Stage/Index)
 4. 版本库(Repository或Git Directory)
 

对于以上四个概念，我们依次理解。

#### 2.1 远程仓库(Remote Directory)
远程仓库(Remote Directory)就是我们在远程服务器上面创建的一个仓库，通常这个仓库存储我们的代码，我们拿GitHub来做个demo，我们在GitHub上面创建一个远程仓库[awesome-git](https://github.com/crazyandcoder/awesome-git)，这个仓库空空如也，刚创建的，等会我们便拿这个仓库进行学习git的相关操作。后面我们需要对这个仓库进行操作，如pull，push，branch，tag，reset等等命令，这些命令后面会详细了解。

#### 2.2 工作目录（Working Directory）
在上个小节，我们创建了一个远程仓库[awesome-git](https://github.com/crazyandcoder/awesome-git)，我们需要将它先放到本地进行相关操作，如存放在本地电脑awesome-git目录下，这个本地文件夹awesome-git就是我们的工作目录，这个就是我们平时存放项目代码的地方。
![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/12/24/16f35d82de541579?w=742&h=306&f=png&s=80298)
#### 2.3 暂存区(Stage/Index)
我们在上个小节中工作目录下面会看见一个.git的文件夹，其实这是一个隐藏的文件夹，这个文件夹是Git的版本库，他是存放Git管理信息的目录，初始化仓库的时候自动创建。暂存区英文叫stage, 或index。一般存放在 ".git目录下" 下的index文件（.git/index）中，所以我们把暂存区有时也叫作索引（index）。其次，Git为我们自动创建的第一个分支master，以及指向master的一个指针叫HEAD。
![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/12/24/16f35d82df141096?w=502&h=464&f=png&s=49833)
#### 2.4 版本库(Repository或Git Directory)
工作目录中有一个隐藏目录.git，这个就是Git的版本库。

### 3. Git工作流程
我们用一张图来表示这四个区域之间的联系：

![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/12/24/16f35d82df36273c?w=1490&h=578&f=png&s=64847)

上面的这张图就是整个git的工作流程，整体如下：

 1. 我们将文件存放到工作目录中，譬如我们新建的代码文件
 2. 用git add把工作目录中的文件添加到暂存区，此时暂存区的目录树被更新
 3. 用git commit提交就是把暂存区的所有内容提交到当前分支，如当前分支是master上面，此时master 分支会做相应的更新。

经过以上三个步骤的操作，此时工作目录中的文件状态会经历三种过程：已修改（modified）=> 已暂存（staged）=> 已提交(committed)

#### 3.1 实战练习
上面一系列操作文件的状态会发生变化，我们来实际学习一下，上面我们创建的awesome-git这个仓库之后会生成一个README.md这个文件，打开看一下里面内容：里面只有一行“# awesome-git”![](https://user-gold-cdn.xitu.io/2019/12/24/16f35d8305f7a551?w=584&h=212&f=png&s=30302)

我们在这个文件里面添加一行注释 “深入理解git” 变成下面：

![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/12/24/16f35d83060903f8?w=596&h=264&f=png&s=37297)

此时我们更改了文件，我们通过一个命令 **git  status** 来查看此时的文件状态：

![](https://user-gold-cdn.xitu.io/2019/12/24/16f35d8306f23aad?w=1064&h=380&f=png&s=286168)
上面很清楚的显示出 “modified: README.md”，这个文件被修改了。此时该文件还存在工作目录中，我们需要将它存入到暂存区stage中，通过命令 **git add** 来执行：
![](https://user-gold-cdn.xitu.io/2019/12/24/16f35d83073d7bcd?w=842&h=332&f=png&s=211284)
上面显示已经存入了暂存区Stage中去了，接下来需要commit，把它放到master分支上区，通过 **git commit** 命令来执行：
![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/12/24/16f35d830ce3345d?w=1130&h=130&f=png&s=141609)

此刻我们经过一系列的操作，已经提交到master分支上了，同时工作区里面没有改动了：
![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/12/24/16f35d830f5e0eda?w=770&h=218&f=png&s=143273)
看，“nothing to commit , working tree clean”，工作区是“干净”的。

经过一系列上述的操作，此刻文件已经放到了master分支上，接下来我们需要将这个文件push到远程仓库中去，

![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/12/24/16f35d833075d639?w=1000&h=216&f=png&s=189083)
通过 **git push** 命令即可推送到远程仓库的master分支上去，我们到远程仓库看一下：

![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/12/24/16f35d833511fe05?w=1020&h=590&f=png&s=53182)

此刻已经推送到了远程仓库中去了。这是最简单git操作了，下面我们将在第7小节中讨论其他一系列复杂的操作。

### 4. Git分支管理
我们开发项目一般都是进行在分支上面开发的，而不是直接在master分支，当开发完成之后我们再将在其他分支上面的代码合并到master分支。接下来我们将详细学习分支的常用操作。我们每次提交，Git都把它们串成一条时间线，这条时间线就是一个分支。截止到目前，只有一条时间线，在Git里，这个分支叫主分支，即master分支。HEAD严格来说不是指向提交，而是指向master，master才是指向提交的，所以，HEAD指向的就是当前分支。

![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/12/24/16f35d833520a920?w=622&h=456&f=png&s=68855)

一开始的时候，master分支是一条线，Git用master指向最新的提交，每次提交，master分支都会向前移动一步，这样，随着你不断提交，master分支的线也越来越长。

#### 4.1 新建分支
我们通过以下命令来创建一个分支：
##### 4.1.1 创建分支
```
//新建dev分支
git branch dev
```

##### 4.1.2 查看所有分支
通过以上命令即可创建一个新分支dev，我们查看以下目前这个repo包含哪些branch

```
//查看分支
git branch 
```
![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/12/24/16f35d8339a40f40?w=754&h=94&f=png&s=56176)
#### 4.2 切换分支

可以看到目前存在两个分支，dev和master分支，master分支前面有个*号表示当前分支是在master上面。我们可以通过下面命令来切换分支：

```
//切换到dev分支
git checkout dev
```

![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/12/24/16f35d8339ff984c?w=856&h=152&f=png&s=111087)
从上面可以看到，我们已经切换到dev分支上面了，dev前面存在*号。

#### 4.3 合并分支
上面我们已经创建了新分支dev，假设我们在新分支上面开发了一些内容譬如新建一个文件dev.md，我们把这个文件提交到dev分支，通过以下命令即可完成：

```
//将dev.md这个文件添加到stage
git add dev.md

//将dev.md这个文件提交到dev分支
git commit -m "add new file to branch dev"
```

![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/12/24/16f35d8341336f1d?w=1176&h=428&f=png&s=317972)
这个时候我们将创建的文件提交到了dev分支，而master分支上面是不存在这个文件的。这个时候需要我们将dev分支上面的东西合并到master分支上面，就需要用到以下的命令：

**1.首先要切回到master分支**
![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/12/24/16f35d83568428a0?w=906&h=190&f=png&s=96937)

**2.合并dev分支**

```
//合并语法
git merge 分支名
```

![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/12/24/16f35d8362ef10b2?w=978&h=224&f=png&s=163973)
可以很清楚的看到master分支上面多了一个文件dev.md，这是理想的情况，正常情况下，我们在分支上面开发的话，一般都会出现**冲突**的问题，我们需要解决冲突：

**3.解决冲突**

首先，我们切回到dev分支，然后在dev.md这个文件中将里面的内容替换一下：

![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/12/24/16f35d8357cbfcd3?w=1106&h=232&f=png&s=48328)

替换成：

![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/12/24/16f35d83634dbe98?w=1970&h=320&f=png&s=112811)
这个时候我们需要将dev分支上面的内容提交到dev分支上面，然后再切回master分支修改dev.md文件并且提交到master分支：

![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/12/24/16f35d8366b0be95?w=1174&h=276&f=png&s=252703)

现在尝试合并dev分支就会出现冲突：

![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/12/24/16f35d836c11f0e0?w=1182&h=122&f=png&s=93012)
因为冲突的地方不能自动解决合并，所以需要我们手动解决，我们进入dev.md这个文件中看看冲突：

![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/12/24/16f35d838484b5e1?w=2018&h=602&f=png&s=218383)

Git用<<<<<<<，=======，>>>>>>>标记出不同分支的内容，我们只需要确认哪些部分是需要的，删除不需要的部分即可。最后再冲洗提交到master分支。

我们可以通过下面的命令查看分支合并情况：

```
git log --graph --pretty=oneline --abbrev-commit
```

![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/12/24/16f35d8386cb4ca1?w=1182&h=400&f=png&s=332433)
这样就完成了分支冲突合并的操作。

#### 4.4 删除分支

在dev分支上面开发好了之后把代码合并到master分支上面，这个dev分支就不需要了，我们将它删除并查看一下分支情况，可以发现只有一个master分支了：

![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/12/24/16f35d8391c63cb0?w=868&h=162&f=png&s=81743)
 
### 5. Git标签管理
发布一个版本时，我们通常先在版本库中打一个标签（tag），这样，就唯一确定了打标签时刻的版本。 tag 和 branch 有点相似，两者有何区别呢？tag 对应某次 commit, 是一个点，是不可移动的，branch 对应一系列 commit，是很多点连成的一根线，有一个HEAD 指针，是可以依靠 HEAD 指针移动的。所以，两者的区别决定了使用方式，改动代码用 branch ,不改动只查看用 tag。

tag 和 branch 的相互配合使用，有时候起到非常方便的效果，例如 已经发布了 v1.0 v2.0 v3.0 三个版本，这个时候，我突然想不改现有代码的前提下，在 v2.0 的基础上加个新功能，作为 v4.0 发布。就可以 检出 v2.0 的代码作为一个 branch ，然后作为开发分支。

#### 5.1 新建tag

```
// 新建tag语法
git tag <tag名>
```

![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/12/24/16f35d839470e486?w=816&h=38&f=png&s=29314)

#### 5.2 查看tag

**注意:** 标签不是按时间顺序列出，而是按字母排序的。可以用以下语法进行查看标签信息 


```
// 查看tag语法
git tag 
```
![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/12/24/16f35d83975e5261?w=766&h=92&f=png&s=55855)

#### 5.3 删除tag
要删除掉你本地仓库上的标签，可以使用如下命令：

```
//删除tag语法
 git tag -d   <tag名>
```

![在这里插入图片描述](https://user-gold-cdn.xitu.io/2019/12/24/16f35d83ae462808?w=780&h=166&f=png&s=108461)

通过以上命令我们删除v1.0的tag 然后再查看以下tag，发现只剩下v2.0的tag了。


### 6. Git远程操作
GitHub 是最大的 Git 版本库托管商，是成千上万的开发者和项目能够合作进行的中心。 大部分 Git 版本库都托管在 GitHub，很多开源项目使用 GitHub 实现 Git 托管、问题追踪、代码审查以及其它事情。 所以，尽管这不是 Git 开源项目的直接部分，但如果想要专业地使用 Git，你将不可避免地与 GitHub 打交道，所以这依然是一个绝好的学习机会。

在GitHub上面新建一个test的repo，可以进行以下的命令操作，即可以将本地的东西提交到GitHub上面的test仓库中：

```
//新建一个readme.md文件
echo "# test" >> README.md

//初始化本地仓库
git init

//将readme.md这个文件加入到stage中去
git add README.md

//将readme.md这个文件放到master分支上去
git commit -m "first commit"
git remote add origin https://github.com/crazyandcoder/test.git

//将这个推送到远程的master分支上去
git push -u origin master
```
