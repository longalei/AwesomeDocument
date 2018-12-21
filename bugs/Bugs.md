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

