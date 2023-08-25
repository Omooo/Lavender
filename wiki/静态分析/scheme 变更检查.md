---
exported 属性检测

---

| 功能            | 使用                          | 输出                    | 主要实现类              |
| --------------- | ----------------------------- | ----------------------- | ----------------------- |
| Scheme 变更检查 | ./gradlew checkSchemeModified | projectDir/schemes.json | CheckSchemeModifiedTask |

#### 一、背景

代码下线时可能会删除一些 Activity，如果这些 Activity 配置了 scheme 路由跳转，可能会导致一些线上问题。

所以需要在构建时，检查 scheme 相对于基线是否存在变更，**如果存在则触发编译失败**。

#### 二、如何使用

引入了 Lavender 插件的工程，可以直接运行：

```
 ./gradlew checkSchemeModified 
```

#### 三、如何解决

##### 本地编译

1. 【方法一】本地编译话，可以直接新增一个命令行参数 "-PskipCheck" 跳过该检查，例如：

   ```
   ./gradlew clean assembleDevDebug -PskipCheck
   ```

2. 拿生成的 schemes.json 文件覆盖 app-startup/runtime/devRelease/runtimeSchems.json 文件

   schemes.json 文件位于项目根目录，当触发编译失败时，会自动生成该文件。

##### 远程编译（jenkins 打包）

这种情况下，得先根据输出的错误信息，找到对应的人，询问清楚是否 scheme 已经下线了，然后按照上述的「本地编译」的【方法二】处理，将该变更提交上去。

#### 四、实现原理

实现步骤分为两步：

1. 首先在 Application 工程的 build.gradle 配置基线文件：

   ```
   checkSchemeModifiedConfig {
       enable = true
       baselineSchemeFile = file("runtime/devRelease/runtimeSchemes.json")
   }
   ```

   这个基线文件可以通过 "./gradlew listSchemes" 任务来生成。


2. 对比基线文件，如果发现发生变更则会输出以下提示信息，并且触发 Task 执行失败

   ```tex
   > Task :app:checkSchemeModifiedForDebug FAILED
   *********************************************
   ****** -- CheckSchemeModifiedTask -- ********
   *********************************************
   Reporter: file:///Users/x xx/AndroidStudioProjects/Lavender/schemes.json
   
   FAILURE: Build failed with an exception.
   
   * What went wrong:
   Execution failed for task ':app:checkSchemeModifiedForDebug'.
   > ---------------------------------- Lavender - Check Scheme Modified ----------------------------------
     发现下列 scheme 定义存在变更: 
      ---
           Class: app.ui.activity.DemoActivity
           Owner: owner@demo.com
           SchemeList: [scheme://host/path]
      ---
     如何解决，请参考文档: 
     本地编译跳过该任务检查: 增加 "skipCheck" 参数即可，例如: ./gradlew clean assembleDebug -PskipCheck
     ------------------------------------------------------------------------------------------------------
   ```

    一旦该任务执行失败，就会在项目的根目录（schemes.json）输出一份当前版本的 scheme 列表，你可以拿该文件的内容覆盖基线文件。