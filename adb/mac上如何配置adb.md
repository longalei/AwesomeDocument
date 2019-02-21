> 有时候需要在AndroidStudio上使用adb,现在就来尝试一下如何在mac上配置adb.



### 首先

> 命令行中打开open  .bash_profile文件。



### 然后

> 找到你的androidSdk的安装目录，快捷的可以直接在androidStudio中找到。
>
> 在.bash_profile文件夹中输入
>
> ```
> #Android
> ANDROID_HOME=/Users/blossom/Library/Android/sdk
> export PATH=$PATH:$ANDROID_HOME/tools
> export PATH=$PATH:$ANDROID_HOME/platform-tools
> ```
>
>



### 最后

> 点击保存文件，在命令行中输入source   .bash_profile进行保存。执行adb,查看是否运行成功。