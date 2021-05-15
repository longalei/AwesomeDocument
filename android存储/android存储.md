##### android存储分两大块

一块内置存储：/data/user/0/...

一块外置存储: /storage/emulated/0/Android/data/...



##### 提供的API

- getFileDir   获取内置存储，默认文件夹是”一块内置存储“/files
- getCacheDir  获取内置存储，默认文件夹是”一块内置存储“/cache
- getExternalFileDir(可传参控制参数) 获取外置存储,，默认文件夹是”一块外置存储“/files
- getExternalCacheDir() 获取外置存储，默认文件夹是”一块外置存储“/cache



##### FileProvider

Android11之下，其它App访问自己内置存储，需要自己Uri授权。访问外置存储无需。

FileProvider Uri访问必须授权。