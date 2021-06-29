##### Android 8

- 后台执行限制：1.进入后台，在持续数分钟的时间窗口结束后进入空闲状态，系统停止应用后台service

  > - 如果针对 Android 8.0 的应用尝试在不允许其创建后台服务的情况下使用 `startService()` 函数，则该函数将引发一个 `IllegalStateException`。
  > - 新的 `Context.startForegroundService()` 函数将启动一个前台服务。现在，即使应用在后台运行，系统也允许其调用 `Context.startForegroundService()`。不过，应用必须在创建服务后的五秒内调用该服务的 `startForeground()` 函数。

  解决方案：使用JobScheduler替代



##### Android 9

