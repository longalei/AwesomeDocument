##### 1

```
 void restoreLoaderNonConfig(ArrayMap<String, LoaderManager> loaderManagers) {
        if (loaderManagers != null) {
        //N = loaderManagers.size()循环内只计算一次
            for (int i = 0, N = loaderManagers.size(); i < N; i++) {
                ((LoaderManagerImpl) loaderManagers.valueAt(i)).updateHostController(this);
            }
        }
        mAllLoaderManagers = loaderManagers;
    }
```

