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