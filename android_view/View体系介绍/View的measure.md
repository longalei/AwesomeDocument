ViewRootImpl : performMeasure()-->View:measure()

##### 从上面的流程中知道三点：

- measure()用来测量View该显示多宽以及多高

- measure()方法内提供可重写方法onMeasure()供子类使用

- measure()方法内处理自身显示多宽多高逻辑后必须得调用

  setMeasuredDimension()



##### 从了解onMeasure()开始

- MeasureSpec:widthMeasureSpec、heightMeasureSpec

##### MeasureSpec

- makeMeasureSpec()
- getMode()
  - UnSpecified
  - Exactly
  - AtMost
- getSize()

综上所述看来，如果是继承View的话，那么重点应该在`onDraw()`

