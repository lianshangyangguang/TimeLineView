# TimeLineView
可滑动、缩放的时间轴
[![](https://jitpack.io/v/lianshangyangguang/TimeLineView.svg)](https://jitpack.io/#lianshangyangguang/TimeLineView)

使用：<br>
配置文件中：  
``` 
allprojects {
    repositories {
        jcenter()
        maven { url "https://jitpack.io" }
     }
    }                                          
    加入依赖
    dependencies {
    compile 'com.github.lianshangyangguang:TimeLineView:v1.0'
    }
```

布局中使用如下：

```
 <com.time.view.library.TimeLineView
        android:id="@+id/timeline"
        android:layout_width="match_parent"
        android:layout_height="50dp"/>
```
代码中使用如下：

```
        TimeLineView timeLineView = (TimeLineView)findViewById(R.id.timeline);
        //设置参数
        timeLineView.setTextSize(10).setCenterNum(10).setMultiple(16).setMarkColor(Color.RED);
        
  ```
