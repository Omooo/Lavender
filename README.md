---
EHiPlugin
---

概述：

| 功能                         | 使用                      | 输出                        | 主要实现类            |
| ---------------------------- | ------------------------- | --------------------------- | --------------------- |
| 输出 app 及其依赖的 aar 权限 | ./gradlew listPermissions | projectDir/permissions.json | ListPermissionTask    |
| 重复资源监测                 | ./gradlew repeatRes       | projectDir/repeatRes.json   | RepeatResDetectorTask |
|                              |                           |                             |                       |

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