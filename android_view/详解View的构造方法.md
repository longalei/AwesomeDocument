#####导读

> 我们在自定义View的时候需要重写View的构造方法，但是关于View里面的构造参数我们用的更多的是Context，而关于AttributeSet,defStyleAttr，defStyleRes的使用知之甚少，带着这个疑问，我们来详细的了解一下这几个参数的作用。

---

##### AttributeSet

> 这是个接口，是一系列属性的容器，我们经常不会想直接用到这个接口，而是用过context.obtainStyledAttributes(attributeSet,R.styleable.xx,int,int)

##### defStyleAttr

> 当前主题的一个默认样式，指向了一个样式资源的引用，如果无需使用时指定为0即可
>
> 例如：在attrs资源目录下，
>
> <attr name="gestureOverlayViewStyle" format="reference" />

##### defStyleRes

> 默认的样式资源，如果无需使用时指定为0即可
>
> 例如：
>
> ```
> <style name="Widget.Material.GridView" parent="Widget.GridView">
>     <item name="listSelector">?attr/listChoiceBackgroundIndicator</item>
> </style>
> ```