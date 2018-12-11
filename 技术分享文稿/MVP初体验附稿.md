### 什么是MVP？

> Model-View-Presenter ；MVP 是从经典的模式MVC演变而来，它们的基本思想有相通的[地方](https://baike.baidu.com/item/%E5%9C%B0%E6%96%B9/2262175)：Controller/Presenter负责逻辑的处理，Model提供数据，View负责显示。

![img](/Users/blossom/Desktop/Document/art/1544511174263.jpg)



### 为什么选择MVP?

- [ ] 代码逻辑易于梳理、维护（单一职责进一步确定）
- [ ] 方便扩展（关心具体）
- [ ] 容易复用（依赖抽象）
- [ ] 简洁（解决Activity等层臃肿）



#### Loaders

> Android 3.0 中引入了加载器，支持轻松在 Activity 或片段中异步加载数据。 加载器具有以下特征：
>
> - 可用于每个 `Activity` 和 `Fragment`。
> - 支持异步加载数据。
> - 监控其数据源并在内容变化时传递新结果。
> - 在某一配置更改后重建加载器时，会自动重新连接上一个加载器的游标。 因此，它们无需重新查询其数据。

---

关键性代码：

```
 /** true if the activity is being destroyed in order to recreate it with a new configuration */
boolean mChangingConfigurations = false;

mFragments.doLoaderStop(mChangingConfigurations /*retain*/);

    void doDestroy() {
        if (!mRetaining) {
            if (DEBUG) Log.v(TAG, "Destroying Active in " + this);
            for (int i = mLoaders.size()-1; i >= 0; i--) {
                mLoaders.valueAt(i).destroy();
            }
            mLoaders.clear();
        }
        
        if (DEBUG) Log.v(TAG, "Destroying Inactive in " + this);
        for (int i = mInactiveLoaders.size()-1; i >= 0; i--) {
            mInactiveLoaders.valueAt(i).destroy();
        }
        mInactiveLoaders.clear();
        mHost = null;
    }
```

