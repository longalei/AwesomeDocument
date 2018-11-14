> 为了在AndroidStudio上Terminal方便的使用gradle命令行，则需要配置好相应的gradle环境。

---

### 首先



> 打开mac终端窗口，执行 open .bash_profile命令。打开bash_profile文件。



### 然后

> 在bash_profile文件中配置好你的环境变量。**（注意：配置gradle环境时同时也需要配置好相应的java环境。）**
>
> > - 配置Java环境。 
> >   由于AndroidStudio下载时默认带有相应的jre,所以这里无需再下载相应的jdk.找到AndroidStudio安装过程中默认存放jre的位置，可点击右键—>显示包内容——..>最后找到/Applications/Android Studio.app/Contents/jre/jdk/Contents/home文件夹。在home文件夹内存放了相应的java环境。
> >
> > - 在bash_profile配置(**注意：Android\ Studio之间有一个空格，这里用了反斜杠进行转义**)
> >
> >   ```
> >   #Java
> >   JAVA_HOME=/Applications/Android\ Studio.app/Contents/jre/jdk/Contents/Home
> >   CLASS_PATH=$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
> >   PATH_=$JAVA_HOME/bin:
> >   export JAVA_HOME
> >   export CLASS_PATH
> >   export PATH_
> >   ```
> >
> > - 配置gradle环境，此时，我们也将使用AndroidStudio中依赖好的Gradle环境。首先，同样的道理，在AndroidStudio中找到对应版本下的Gradle命令窗口。此时我们可以找到它在/Applications/Android Studio.app/Contents/gradle/gradle-4.6/bin下。最后注意空格之间的转义。
> >
> >   ```
> >   #Gradle
> >   GRADLE_HOME=/Applications/Android\ Studio.app/Contents/gradle/gradle-4.6
> >   export GRADLE_HOME
> >   export PATH=$PATH:$GRADLE_HOME/bin
> >   ```
> >

### 最后

> 在bash_profile中写好了Java与Gradle的环境配置，点击保存。同时在命令窗口中输入source .bash_profile进行应用。接下来验证java是否安装成功，可以输入``` java -version``` 。同时验证gradle是否安装成功了则输入```gradle -v``` 。
>
> **注意：如果发现在输入```gradle -v```**时收到permission denied.则需要开启对应gradle命令窗口操作的权限。在命令窗口输入``` chmod +x /Applications/Android\ Studio.app/Contents/gradle/gradle-4.6/bin/gradle```
>
> 这样就可以成功了。如果发现最后出现```not find such file...```可以试着检查一下是否路径上有空格没有被转义。同时命令行不起效，可以试着重启一下电脑。