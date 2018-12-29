#### Recyclerview

- 不要在notyfyDataSetChanged()时调用getAdapterPosition();



#### Gradle

- 在进行库依赖的时候，里面库的版本一定不能比主Module的版本高。同时要尽量保证

  库的版本与主Module的版本一致。如果不能一致，应该对内部库进行exclude

  ```
  Error : Program type already present: xxxxxxx
  ```

#### 混淆

- 当数据一切正常时，debug版本与release版本上数据加载有问题，考虑是否代码被混淆了。

#### 循环

- 增强的循环内是不能够删除元素的。

#### 集合   ConcurrentModificationException

```
    这里写是错误的
    public static List<Switcher> getSwitcherExclusiveOwn(String targetUserId) {
        List<Switcher> list = getSwitcher();
        boolean isExclusive = false;
        //错误的，增强循环里面只适合查询
        for(Switcher switcher : list) {
            if(targetUserId.equals(switcher.getUserId())) {
                isExclusive = true;
                list.remove(switcher);
            }
        }
        if(isExclusive) {
            return list;
        } else {
            return new ArrayList<>();
        }
    }
    在使用listIterator 或 ArrayList.subList时
    并且有一点，对于ArrayList和Vector,不能同时存在add和remove操作。否则也是报这个异常，可以将其转化外其他的集合进行操作。
```

