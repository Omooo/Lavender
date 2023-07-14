---
exported 属性检测
---

| 功能                                                 | 使用                                 | 输出                          | 主要实现类        |
| ---------------------------------------------------- | ------------------------------------ | ----------------------------- | ----------------- |
| 检测 Manifest 注册组件是否声明 android:exported 属性 | ./gradlew check{variantName}Exported | projectDir/checkExported.json | CheckExportedTask |

#### 一、背景

进行了 Android 12 的适配时，有一条规则是：

> 在 Android 12 中包含 \<intent-filter> 的 activity、 service 或 receiver 必须为这些应用组件显示声明 android:exported 属性，否则 App 将无法安装。

所以，需要我们找出哪些模块，它的 Manifest 中声明的组件包含了 intent-filter 但未声明 exported 属性。

#### 二、如何使用

引入了 Lavender 插件的工程，可以直接运行：

```
 ./gradlew checkExported 
```

该 Task 会在项目的根目录输出一个 checkExported.json 文件，类似以下：

```
{
    "xxx:comweb-sdk:5.11.0": [             // AAR 名称
        "xxx.comweb.CommWebViewActivity"   // 声明的组件名称
    ],
    "app-startup": [
        "xxx.ui.activity.SplashActivity"
    ],
    // ...
}
```

#### 三、实现原理

解析所有的 AndroidManifest.xml 文件，找出声明的组件包含了 intent-filter 但未声明 exported 属性的组件名称。