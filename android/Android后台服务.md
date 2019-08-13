开发过程中经常需要使用后台处理，关于大型计算或长时间的耗时操作将会使用Service处理。现在我将结合Android官网上的资料对服务做一个简单的概括。

#### 后台服务限制变更

> 后台任务消耗有限的资源，比如RAM、电量等等。如果处理的不好，对用户来说伤害很大。为了提高用户体验以及节省电量，Android系统限制了App的后台工作。当App对用户不可见时（包含前台服务的通知栏）

- Android 6.0上引入了[低电耗模式和待机模式](https://developer.android.com/training/monitoring-device-state/doze-standby?hl=zh-CN)，当用户长时间未使用设备时，低电耗模式会延迟应用的后台 CPU 和网络活动，从而降低耗电量。应用待机模式会延迟用户近期未与之交互的应用的后台网络活动.

- Android 7.0上限制了隐式广播和加入了[随时随地待机模式](https://developer.android.com/about/versions/nougat/android-7.0?hl=zh-CN#doze_on_the_go),(只要屏幕关闭了一段时间，且设备未插入电源，地低电耗模式就会对应用使用熟悉的CPU和网络限制)
- Android 8.0上进一步限制了[后台行为](https://developer.android.com/about/versions/oreo/background?hl=zh-CN)，比如后台获取位置
- Android 9.0引入了[智能待机模式](https://developer.android.com/topic/performance/appstandby?hl=zh-CN)，这将会根据你使用的频率和时间做相应的调整。

