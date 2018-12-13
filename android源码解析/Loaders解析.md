##### Loaders解析

> 首先需要分析的是Loader、LoaderManager

---

#### Loader

> ```
> LoaderInfo内的对象Loader；
> LoaderInfo使用SparseArray进行保存。通过
> ```



#### LoaderManager

```
LoaderManager使用ArrayMap进行保存。一个Activity只有一个LoaderManager，因为指定ArrayMap的Key是“(root)”

```



##### LoaderManager 在Activity重启下如何保存？

> 由Activity静态类NonConfigurationInstances维护。
>
> 当NonConfigurationInstances初始化后，在oncreat()方法下会进行检查。