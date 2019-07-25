> EventBus中进行信息传递的思路就是：数据信息在内存中进行中转，通过对注册类方法等信息的保存，最后发送事件时对方法的反射。从而实现了消息之间的跨越传输。



##### 普通Event与StickyEvent有什么区别？

- 普通event必须先注册，发送事件后才能收到；
- 发送StickyEvent事件时，EventBus会将它存好，然后有新的注册者进来时，并且指定sticky=true时会收到发送的事件，根本原因是在rigister的时候，会枚举所有的StickyEvent事件

