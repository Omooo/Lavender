---
APK 增量分析
---

| 功能         | 使用                 | 输出                             | 主要实现类     |
| ------------ | -------------------- | -------------------------------- | -------------- |
| APK 增量分析 | ./gradlew apkAnalyse | *{projectDir}/*apkAnalyse*.*html | ApkAnalyseTask |

#### 一、背景

APK 每个版本都有近 10M 的增量，但是无法知道这些增量来源于哪里？不利于 App 包体积良性增长。

如果在某个业务中引用了较大的资源文件，如何能够及时发现呢？

那么，这正是这个任务所要解决的问题。

#### 二、如何使用

在接入 Lavender 的 Application 工程中，直接运行：

```JSON
./gradlew apkAnalyse
```

该任务会在终端输出：

```JSON
> Task :app-startup:apkAnalyseForDevRelease
*********************************************
*********** -- ApkAnalyseTask -- ************
***** -- projectDir/apkAnalyse.json -- ******
*********************************************
Reporter: file:///Users/xxx/projectRootDir/apkAnalyse.json
Reporter: file:///Users/xxx/projectRootDir/apkAnalyse.html
Spend time: 32990ms
```

同时会在项目的根目录输出 apkAnalyse.html 报告。

#### 三、实现原理

##### 1. 解析 APK

这一步主要的目标是，解析 Apk 生成文件列表。它涉及解析依赖、解 dex 文件、解混淆。

其中比较麻烦的是资源的混淆问题。

不同于类的反混淆，资源混淆是没有默认生成的 mapping 文件的。那如何去解决这个问题呢？

我们首先想到的是使用 md5 对比 resources-release.ap_ 和 resources-release-optimize.ap_ 文件，这可以解决绝大部分的资源混淆问题。因为 [OptimizeResourcesTask](https://android.googlesource.com/platform/tools/base/+/studio-master-dev/build-system/gradle-core/src/main/java/com/android/build/gradle/internal/tasks/OptimizeResourcesTask.kt) 默认只做 "--shorten-resource-paths" 处理，即缩短资源路径，不会对文件内容处理，所以可以通过对比混淆前后资源文件的 md5 值，得到混淆前后资源名的映射。但有一种情况例外，即资源未被使用，在 ShrinkResourcesTask 时被重写成了空文件。

这种情况下，就无法使用 md5 对比了。那还有什么办法呢？

其实 AAPT2 提供了生成资源 mapping 文件的命令行参数，见：[AAPT2 - 优化选项](https://developer.android.com/studio/command-line/aapt2?hl=zh-cn#optimize_options)，但是该参数，没法通过 gradle.properties 或 aaptOptions 来指定，所以我们最终解决方案就是，使用 AAPT2 对 resources-release.ap_ 文件进行再处理一次，获取到 mapping 文件即可。混淆规则可见：[ResourcePathShortener](https://android.googlesource.com/platform/frameworks/base/+/master/tools/aapt2/optimize/ResourcePathShortener.cpp)。

##### 2. 增量分析

增量分析这一步的目标是，对比上一个版本的文件列表，输出差异。那上一个版本的文件列表是如何存储的呢？

其实是存储在了 {projectDir}/lavender-plugin/apk/previous.json 下，在第一次运行该任务时，就会把当前版本的 Apk 文件列表存储至此。

##### 3. 可视化

使用一个独立的 Kotlin JS 工程，来渲染 json 生成 html。

不过在 Lavender 中，是直接内置了模版 html。