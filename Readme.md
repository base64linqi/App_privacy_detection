## App privacy detection使用说明



## 工具界面

![image-20230314094908182](C:\Users\25425\AppData\Roaming\Typora\typora-user-images\image-20230314094908182.png)

### 工具环境

Java 1.8 开发环境(**部分人员开启工具会出现显示错位现象，请保证分辨率处于正常**)

Windows 10（测试没问题）

### 工具使用

1.请确保命令行可以启动`Frida`

2.确认`Frida`客户端版本和安卓`Frida`服务端版本一致

#### Android-frida

![image-20230314095033228](C:\Users\25425\AppData\Roaming\Typora\typora-user-images\image-20230314095033228.png)

点击`【新建测试方案】`后命名.如:`美团`

![image-20220728160858445](C:\Users\25425\AppData\Roaming\Typora\typora-user-images\image-20220728160858445.png)

测试方案命名后会打开菜单栏，点击`【获取】`后`【应用列表】`会将应用程序列出来

![image-20220306144036840](C:\Users\25425\AppData\Roaming\Typora\typora-user-images\image-20220306144036840.png)

可以使用搜索功能查找目标`App`，在**方框**中填写`App`，然后点击`【过滤】`

![image-20220306144221029](C:\Users\25425\AppData\Roaming\Typora\typora-user-images\image-20220306144221029.png)

选择`App`后,需要选择**注入方式**`【attach】`需要手动开启App后点击、`【Spawing】`无需手动启动App直接点击即可。

点击消息框的内容，即可看到详细的内容

![image-20220306144557036](C:\Users\25425\AppData\Roaming\Typora\typora-user-images\image-20220306144557036.png)

可以在测试中进行切换,数据会在其他页面中继续堆叠

![image-20220728161205528](C:\Users\25425\AppData\Roaming\Typora\typora-user-images\image-20220728161205528.png)



同时也新增了

1.选择权限可以单独显示选中的内容

2.添加了信息栏删除功能

3.脚本优化且新加了bypassRoot环境的脚本

![image-20220728162219888](C:\Users\25425\AppData\Roaming\Typora\typora-user-images\image-20220728162219888.png)



#### Android-Xposed

![image-20230314095308372](C:\Users\25425\AppData\Roaming\Typora\typora-user-images\image-20230314095308372.png)

创建完成之后，`包名`随意只是数据库中有这个表，测试不填也行~然后点击开始即可捕获，但是如果想切换到其他状态，需要`中止检测`后点击其他状态页面，否则多线程写入会导致数据库锁死从而不记录信息。

![image-20230314095446365](C:\Users\25425\AppData\Roaming\Typora\typora-user-images\image-20230314095446365.png)

`Xposed插件`需要修改成自己的包名

![image-20230314095657422](C:\Users\25425\AppData\Roaming\Typora\typora-user-images\image-20230314095657422.png)



### 常见问题

**一.应用列表内容过少**

解决方法：1.将手机手动重启，重新开启frida。2.重新将frida-server kill掉，重新启动

**二找不到应用程序**

解决方法：1.检测Frida是否准确或重新启动frida-server

**三.应用程序什么内容都没有**

在同事测试和开发Debug过程中没有发现任何报错，也未有逻辑错误，因此这个Bug无法修改，同事通过更换系统解决了这个问题，在这里不建议各位使用此方法。

**四.App打开崩溃（一）**

解决方法：

1.在我测试中，使用Pixel手机确实会出现App崩溃的情况，在使用小米机器或其他机器暂未发现有此情况

2.Frida被检测

**五.App打开崩溃（二）**

解决方法：使用frida-ps -U查看App是否存在双进程，切换版本到frida15试一试