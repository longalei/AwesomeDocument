### [参考官方文档链接](https://developer.android.com/guide/components/activities/process-lifecycle)

---

> Android进程的生命周期不是由应用直接控制，而是由系统控制。在低内存的时候决定哪个进程被杀死取决于它的进程优先级，关机进程优先级如下：

- ##### 前台进程

  - 在屏幕上启动一个activity，用户再与其进行交互(onResume方法被调用)；
  - BroadcastReceiver当前正在运行(onReceive()正在被调用)

- ##### 可见进程

  - Activity可见但是onPause()已经调用(比如一个Activity在此Activity上以dialog的形式显示)
  - Service startForeground

- 服务进程

  - 通过startService启动

- 缓存进程