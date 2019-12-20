##### 空安全

> Kotlin的类型系统就是要消除来自代码引用的空引用的危险，我认为除非是自己显示的想制造出空异常的情况，其它场景都不应该出现空异常。

关于Kolin中出现NPE的唯一可能原因是：

- 显示的调用throw NullPointerException()
- 使用了Kotlin中的`!!`操作符
- 有些数据在初始化时不一致（例如：传递一个在构造函数中出现的未初始化的this并用于其它地方）

 

java互操作

- 企图访问平台类型的null引用的成员
- 用于具有错误可空性的Java互操作的泛型类型，例如：一段Java代码可能会向Kotlin的MutableList<String>

中加入null。

- 由外部java代码引发的