##### SystemServiceRegistry

> 这是一个用context获取服务的系统服务注册类，例如获取inflater,而这些都是抽象，具体的实现为PhoneLayoutInflater。
>
> ```
>  registerService(Context.LAYOUT_INFLATER_SERVICE, LayoutInflater.class,
>                 new CachedServiceFetcher<LayoutInflater>() {
>             @Override
>             public LayoutInflater createService(ContextImpl ctx) {
>                 return new PhoneLayoutInflater(ctx.getOuterContext());
>             }});
> ```
>
> 

###### ContextImpl

> Context 的实现类，里面有丰富的获取资源的方法

###### LayoutInflaterCompat

> 经典给LayoutInflater设置Factory的写法

##### ViewAnimationUtils

> 提供了一个圆角裁剪的动画

##### Outline、ViewOutlineProvider

> View中会调用invalidateOutline(),正是这个方法，可以用来修改View的样式，比如圆角、裁剪等等

##### PhotoViewActivity

> 这个类完全是属于Controller控制，界面中无需关心具体的细节，只需要关注自己的Controller

##### ObjectPool 

> 可以用来存储再次使用的对象

##### CountDownLatch

