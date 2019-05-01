> 本篇文章主要记录一些对Lint的了解和使用，不包含工具栏中如何去anlyze code和设置inspections对lint警告配置以及如何去实现自定义Lint检查。并且这些都是在AndroidStudio提供的Lint代码扫描工具基础上。

[参考官方链接请点击这里](<https://developer.android.com/studio/write/lint?hl=zh-CN#commandline>)

在使用上主要包含这几点：

- 配置Lint命令行环境以及运行Lint

- 设置Lint检测到的每个问题的严重级别
- 分析Lint Report

Lint工具可检查Android项目源文件是否包含潜在错误，以及在**正确性**、**安全性**、**性能**、**易用性**、**便利性**和**国际化方面**是否需要优化改进。



##### Mac上配置Lint环境

```
open .bash_profile

在此文件中输入Lint Path
# Android Lint
ANDROID_HOME=你的android sdk目录
export PATH=$PATH:$ANDROID_HOME/tools/bin

保存
source .bash_profile

验证Lint是否配置成功
lint --version
例如：以下配置成功
Longalei:~ blossom$ lint --version
lint: version 26.1.1
Longalei:~ blossom$ 
```

##### 设置Lint检测级别

查看更多的lint支持命令可以输出lint --help

- 手动创建lint.xml置于你的项目中的根目录中

  ```
  <?xml version="1.0" encoding="UTF-8"?>
      <lint>
          <!-- list of issues to configure -->
  </lint>
  ```

- 您可以在 `<issue>` 标记中设置严重级别属性，以更改某个问题的严重级别或禁止 Lint 检查此问题。

  ```
  需要查询Lint支持的问题以及对应的问题的ID的完整列表，可以输入lint --list
  列举部分如下：
  "ContentDescription": Image without contentDescription
  "AddJavascriptInterface": addJavascriptInterface Called
  "ShortAlarm": Short or Frequent Alarm
  "AllCaps": Combining textAllCaps and markup
  "AllowAllHostnameVerifier": Insecure HostnameVerifier
  "AlwaysShowAction": Usage of showAsAction=always
  "InvalidUsesTagAttribute": Invalid name attribute for uses element.
  "MissingIntentFilterForMediaSearch": Missing intent-filter with action
        android.media.action.MEDIA_PLAY_FROM_SEARCH
  "MissingMediaBrowserServiceIntentFilter": Missing intent-filter with action
        android.media.browse.MediaBrowserService.
  "MissingOnPlayFromSearch": Missing onPlayFromSearch.
  "ImpliedTouchscreenHardware": Hardware feature touchscreen not explicitly
        marked as optional
  "MissingTvBanner": TV Missing Banner
  
  例如：使用了android.media.ExifInterface我设置成error
   <issue id="ExifInterface" severity="error" />
   安全层级大概分为：ignore、informational、warning、error、fatal
  ```



##### Gralde 配置Lint选项

可以使用**模块级**build.gradle文件中的lintOptions{}块来配置某些lint选项

```
android {
  ...
  lintOptions {
    // Turns off checks for the issue IDs you specify.
    disable 'TypographyFractions','TypographyQuotes'
    
    // Turns on checks for the issue IDs you specify. These checks are in
    // addition to the default lint checks.
    enable 'RtlHardcoded','RtlCompat', 'RtlEnabled'
    
    // To enable checks for only a subset of issue IDs and ignore all others,
    // list the issue IDs with the 'check' property instead. This property overrides
    // any issue IDs you enable or disable using the properties above.
    check 'NewApi', 'InlinedApi'
    
    // If set to true, turns off analysis progress reporting by lint.
    quiet true
    
    // if set to true (default), stops the build if errors are found.
    abortOnError false
    
    // if true, only report errors.
    ignoreWarnings true
  }
}
...
```



##### Mac分析Lint Report

当你在gradle上执行**./gradlew lint**,Lint工具完成检查后，会提供两个生成报告的路径，位于你的build文件夹下。

例如：build/reports/lint-results.html或build/reports/lint-results-你的构建变体.html

我们也可以在gralde构建过程中将报告中的信息输出。这需要我们在gradle中定义我们自己的task.