# 电子会员卡SDK使用文档

---

[ ![Download](https://api.bintray.com/packages/mzhua/maven/pedometer/images/download.svg) ](https://bintray.com/mzhua/maven/pedometer/_latestVersion)

---

## 版本支持
minSdkVersion 15

---

##  接入方式

### Android Studio

* 在module的`build.gradle`中加入依赖

   `compile 'com.wonders.xlab.pedometer:pedometer:lastestVersion'`
   
   ***lastestVersion就是顶部Download后面的数字***
  

---

## 如何调用

* 打开电子会员卡

	```
	XPedometer.getInstance().start();
	```
* 启动计步服务（并且确保开机启动，不被系统kill进程）

	```
	startService(new Intent(this, StepCounterService.class));
	```
* 每次步数都会通过`BroadCast`广播，如果需要接收，则注册监听如下`action`即可

	```
	IntentFilter intentFilter = new IntentFilter(getPackageName() + ".pm.step.broadcast");
	```

---

## 设置自定义属性

电子会员卡SDK可以自定义2个属性，主要是为了最大程度的配合APP的设计，具体可自定义属性如下

 属性名 | 资源类型 | 解释 | 默认值 | 可用值
------------ | ------------- | ------------- | ------------- | -------------
pmTopBarTitleColor | `color` | TopBar标题颜色（返回按钮和菜单的颜色也是随着标题颜色）| `@android:color/white` |颜色资源
pmTopBarBackground | `color` | TopBar背景颜色 | `@android:color/white` | 颜色资源


----

## 如何同步数据

为了尽量简化SDK，所有数据不会再SDK内部进行网络操作，数据都是暂时缓存在`sqlite`中，图片则缓存在APP的私有空间。下面为具体的数据同步的方法。


### 取出SDK中的数据


```
	XPedometer.getInstance().getAllLocalRecords(this);
```

### 将APP在服务器中存储的数据同步到SDK中进行本地保存

```
	XPedometer.getInstance().updateLocalRecords();
```
