activity之间是怎么进行传输数据的？Intent的作用是什么？startActivity与startActivityForResult有什么区别？

startActivity最终调用了startActivityForResult为什么收不到onActivityForResult()

> 归根结底，两个Activity之间传递数据是通过消息机制，而Activity启动是通过AMS。这在compileSdkVersion为27的情况下来看更明显，而Intent本身就是一个包含数据处理的处理类。比如：启动的目标页，携带的数据、状态码等等



##### Activity启动的问题

> 关于Activity启动的关键信息代码在ActivityStarter、ActivityStartController、ActivityStack、ActivityRecord、TaskRecord、AppWindowContainerController这些类中。

ActivityManager

1. 应用内Activity A启动Activity B
2.  桌面启动应用内的Activity (考虑应用是否存在？)
3.  应用内Activity 启动 系统的Activity(比如相机)
4.  应用内Activity启动第三方应用的Activity

上面这些场景有什么区别？

从2、3、4上来讲，性质是一致的。1是不一样的。关于具体可以参考以下链接

1.[Android系统是如何启动应用程序的？从Zygote到Activity的onCreate()](<https://juejin.im/post/5b0d0a0cf265da091f105858>)

2.[Android Instrumentation源码分析（附Activity启动流程)](<https://aspook.com/2017/02/10/Android-Instrumentation%E6%BA%90%E7%A0%81%E5%88%86%E6%9E%90%EF%BC%88%E9%99%84Activity%E5%90%AF%E5%8A%A8%E6%B5%81%E7%A8%8B%EF%BC%89/>)



##### Activity、Window、View之间的区别？



