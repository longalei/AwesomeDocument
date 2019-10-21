### 场景

> 当手指点击屏幕时从而出发具体事件的响应这一过程涉及到事件的传递。

---

#### 笼统的整理一下

> 当手指点击屏幕时(电压、电容)变化，电信号再经过Linux操作系统变成计算机指令（输入、输出）。然后通过Android Server、InputManagerService、WindowManagerService、消息机制等等输出Java对象InputEvent(MotionEvent、KeyEvent)紧接着交由ViewGroup进行分发到指定View。

##### 常见的谈到分发（我认为是不够准确的）

> Activity-->PhoneWindow-->DecorView-->ViewGroup-->View

因为具体的开始过程是在PhoneWindow-->DecorView dispatchTouchEvent中,紧接着才开始上面的逻辑分发。