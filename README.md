---
Lavender
---

概述：

| 功能                         | 使用                      | 输出                        | 主要实现类            |
| ---------------------------- | ------------------------- | --------------------------- | --------------------- |
| 输出 app 及其依赖的 aar 权限 | ./gradlew listPermissions | projectDir/permissions.json | ListPermissionTask    |
| 重复资源监测                 | ./gradlew repeatRes       | projectDir/repeatRes.json   | RepeatResDetectorTask |
| 资源压缩（png2webp）         | 打包时自动执行            | 简单日志输出                | Convert2WebpTask      |

#### ListPermissionTask

原理是参考 ProcessApplicationManifest#730 的实现：

```
// This includes the dependent libraries.
task.manifests = variantScope.getArtifactCollection(RUNTIME_CLASSPATH, ALL, MANIFEST);
```

根据注释可知，这是拿到的是包含第三方依赖的，所以我们构造处理 VariantScope 就好了。

#### RepeatResDetectorTask

基础使用：

```
./gradlew repeatRes
```

这只会扫描 drawable- 目录里面的资源，也就是图片资源。

但是实际发现，drawable 目录下竟然也有重复的文件，这里都是一些 shape、selector，所以如果你也想扫描 drawable 下的文件，可以使用：

```
./gradlew -Pall=true repeatRes
```

实现原理：

扫描指定目录下的文件，生成 MD5 值比对即可。

效果：

v6.3.2 减少包体积 133kb。

#### Convert2WebpTask

可以在打包前（比如 assembleDebug、assembleRelease）自动去转化所有的 png 图片，包括第三方依赖库里面的。该 Task 的执行时机其实是依赖于 MergeResources Task。

同时支持检查大图片，图片大小可配置。

```groovy
convert2WebpConfig{
    enableWhenDebug true
    maxSize 1024*1024 // 1M
    whiteList ["xxx.png","xxx.png"]
    //...
}
```

使用的是 cwebp 转化工具，这个东东放到了项目的根目录下的 /tools/cwebp 目录下了。

相关参数见：[https://developers.google.com/speed/webp/docs/cwebp](https://developers.google.com/speed/webp/docs/cwebp)

该 Task 的核心是怎么拿到所有的 res 资源呢，其实也很简单，就是一个 Gradle API，看下下面参考库的文档即可。

该 Task 参考自 [https://github.com/smallSohoSolo/McImage](https://github.com/smallSohoSolo/McImage) ，因为我们的 minSDK = 19，所以我去掉了一些代码。
