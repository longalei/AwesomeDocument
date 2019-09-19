- [x] 详细的解析ConstraintLayout以及其相关的类

参考链接：[ConstraintLayout](https://developer.android.com/reference/android/support/constraint/ConstraintLayout.html)

> 常见的普通约束此处不做赘述，主要是讲解一些比较高深的用法

- **layout_goneMarginStart**

  这个属性只有在约束到的指定的View Gone时才显示相应的边距。

- **layout_constraintHorizontal_bias**

  这个属性只有在当前View左右进行相应的约束时才有效，值的区间在0-1,代表左边距距离约束与右边距约束比

- **layout_constrainedWidth**

  当View使用wrap_content并且有相应的约束时，约束并不会限制它的尺寸。比如：TextView左边约束A，右边约束B，而当TextView内容过多时，可能内容区域就会覆盖在A.B上,因此你就需要进行强制性约束：

  ```
  app:layout_constrainedWidth="true"
  app:layout_constrainedHeight="true"
  ```

- **layout_constraintWidth_min**

  这个用来控制View的最大、最小

- **layout_constraintWidth_percent**

  这个属性与上面的属性**layout_constraintWidth_min**有点相似，都是来控制当前的view应该显示多大，但是只不过这个属性是采用百分比(与父布局相比)的形式进行展现。使用这个属性时，需要满足下面三个条件：

  - layout_width/height必须设置成0dp
  - **app:layout_constraintWidth_default="percent"**,我测试这个其实不加也可以
  - layout_constraintWidth_percent设置成0-1

- ##### layout_constraintDimensionRatio

  这个属性是用来控制自身长宽的比例，在使用它时，必须当前View的宽或者高指定为0dp

  ```
  默认宽：高
  app:layout_constraintDimensionRatio="1:1"
  app:layout_constraintDimensionRatio="H,1:1"
  ```



---

##### 理解Chain

> 当两个View之间双向的连接在一起了，我们就认为创建出了一个Chain。比如：A的右边约束B的左边，B的左边约束A的右边。这样的话我们就认为A、B之间创建了Chain.同时我们把A叫作Chain Head

- **layout_constraintVertical_weight**

  关于这个属性，当创建了Chain时，你可以指定view的weight,此时的操作就有点像LinearLayout

- **layout_constraintHorizontal_chainStyle**

  关于chainStyle我觉得贴张图会更合适。

  ![](/Users/blossom/Desktop/AwesomeDocument/art/chains-styles.png)



---

##### 辅助的帮助对象----View

- GuideLine

  添加一条无大小的辅助线

- Barrier

- Group

  可以用来控制多个视图的可见性

- Placeholder

  这个我觉得用来改变View的位置是个不错的选择，通过setContent(id)可以将内容区域中的任何view移动到placeholder中，在使用Placeholder时，将其当做普通的view进行约束即可。

- Flow

  这个可以将多个View组合在一起创建出流式布局

##### 注意点

- 不要在View之间循环约束(例如：A右边约束B右边，B右边约束A右边，这样会导致OOM)
- 不推荐在ConstraintLayout中的控件的宽、高使用`match_parent`,应该使用left等约束到parent上