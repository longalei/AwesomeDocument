activity之间是怎么进行传输数据的？Intent的作用是什么？startActivity与startActivityForResult有什么区别？

startActivity最终调用了startActivityForResult为什么收不到onActivityForResult()

> 归根结底，两个Activity之间传递数据是通过消息机制，这在compileSdkVersion为27的情况下来看更明显，而Intent本身就是一个包含数据处理的处理类。比如：启动的目标页，携带的数据、状态码等等



##### Activity启动的问题

- ActivityManager

- 应用内Activity A启动Activity B
- 桌面启动应用内的Activity (考虑应用是否存在？)
- 应用内Activity 启动 系统的Activity(比如相机)
- 应用内Activity启动第三方应用的Activity

上面这些场景有什么区别？



##### Activity、Window、View之间的区别？



