---
无用 Assets 资源检测
---

无用 Assets 资源检测

| 功能                 | 使用                       | 输出                         | 主要实现类           |
| -------------------- | -------------------------- | ---------------------------- | -------------------- |
| 无用 Assets 资源检测 | ./gradlew listUnusedAssets | projectDir/unusedAssets.json | ListUnusedAssetsTask |

#### 一、如何使用

在接入 Lavender 的 Application 工程中，直接运行：

```JSON
./gradlew listUnusedAssets
```

该任务会输出：

```JSON
> Task :app-startup:listUnusedAssetsForNioRelease
*********************************************
********* -- ListUnusedAssetsTask -- ********
***** -- projectDir/unusedAssets.json -- ****
*********************************************
Total reduce size: 14251141 bytes.
Reporter: file:///xxx/nio/rootProjectDir/unusedAssets.json
```

并且会在项目的根目录输出 unusedAssets.json 报告，示例如下：

```JSON
{
    "xxx:xx:5.12.1": [    // AAR 名称
        "mockhome.json"                       // 该 AAR 下的无用 assets 资源
    ],
    "xxx:xx:2.6.5": [
        "checkbox_ok.json"
    ],
    // ...
}
```

1. ##### 白名单配置

   如果想配置白名单，则可以在项目的根目录下新增 {projectDir}/lavender-plugin/whitelist/assets.json，例如：

   ```JSON
   [
     "lottie/xxx.json",    // assets 资源名称
     "rule_action_layout.json",
     // ...
   ]
   ```

1. ##### 资源归属

   如何归属 AAR 是属于谁负责呢，可以同样在项目的根目录下 {projectDir}/lavender-plugin/ownership.yaml，例如：

   ```YAML
   omooo@yourcompany.com:
     - aar-name-sdk
     - xx-carinspect-sdk
     - xx-carwash-sdk
     - xx-common-lib
   
   xxx@yourcompany.com:
     - xxx-shadow-secret
   ```

#### 二、实现原理

实现原理类似于 [Matrix#UnusedAssetsTask](https://github.com/Tencent/matrix/blob/master/matrix/matrix-android/matrix-apk-canary/src/main/java/com/tencent/matrix/apk/model/task/UnusedAssetsTask.java)，该方案的是：搜索 smali 文件中引用字符串常量的指令，判断引用的字符串常量是否某个 assets 文件的名称。

而我们的做法是：

1. 收集所有的 assets 资源名称
2. 在 assembleRelease 后解析 resources.txt 文件，匹配出所有的引用字符串
3. 如果引用字符串中未包含的 assets 资源名称，即判定为未使用的 assets 资源

所以 该任务是依赖于 assembleReleaseTask。

其实思路都是来源于 AGP 的 [ResourceUsageAnalyzer.java](https://android.googlesource.com/platform/tools/base/+/studio-master-dev/build-system/gradle-core/src/main/java/com/android/build/gradle/tasks/ResourceUsageAnalyzer.java) 实现。