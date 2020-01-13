了解回收机制首先要得从RecyclerView的`getViewForPosition()`说起。

并且也有其相关的类Recycler。在Recycler类中有几个跟ViewHolder缓存相关的成员变量，RecyclerView的缓存机制基本上离不开这几个变量，按照使用顺序排列依次是：

- mAttachedScrap
- mChangedScrap
- mCacheViews
- RecyclerViewPool

关于几点要说的：

- mCacheViews中清除的ViewHolder会放入RecyclerViewPool中
- 可以通过ViewCacheExtension实现自己的缓存机制