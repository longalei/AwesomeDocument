在使用LayoutInflater中的inflate方法时，经常会遇到其他两个参数如下：

> LayoutInflater.from(context).inflate(layout,ViewGroup viewGroup,boolean attachToRoot);

在对这两个参数进行传参时，难免会有点疑惑，特别是最后一个，不太明白attachToRoot的具体作用。现在打开源码，一步一步进行分析。最后所有的方法都会调用View inflate(XmlPullParser parser, @Nullable ViewGroup root, boolean attachToRoot)方法；

##### 一、当根布局是以merge标签开头时；

> 当xml解析器解析到merge标签时，如果root为空或者attachToRoot为false时会抛出异常，因为：
>
> `
>
> ```
>                 if (TAG_MERGE.equals(name)) {
>                     if (root == null || !attachToRoot) {
>                         throw new InflateException("<merge /> can be used only with a valid "
>                                 + "ViewGroup root and attachToRoot=true");
>                     }
> 
>                     rInflate(parser, root, inflaterContext, attrs, false);
>                 }
> ```
>
> 最后会将实例化的view添加到此ViewGroup中，在使用merge标签时毫无疑问使用inflate(layout,this,true)



##### 二，不为merge标签时，参数ViewGroup不为null时；

> 当ViewGroup不为空时，会获取此ViewGroup的LayoutParams参数，当attachToRoot为false时，会将此ViewGroup上的参数设置给实例化的View并返回.如果attachToRoot为true时，则会将实例化的View添加到此ViewGroup中并返回。



##### 二、不为merge标签时，参数ViewGroup为null时;

> 接下来直接解析相应的tag标签实例化每一个View,当root为null时，则只会实例化出相应的View返回,



