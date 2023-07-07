---
输出 App 依赖 AAR 下的 assets 资源
---

| 功能                   | 使用                 | 输出                   | 主要实现类     |
| ---------------------- | -------------------- | ---------------------- | -------------- |
| 列出所有的 assets 资源 | ./gradlew listAssets | projectDir/assets.json | ListAssetsTask |

#### 一、如何使用

在接入 Lavender 的 Application 工程中，直接运行：

```JSON
./gradlew listAssets
```

该任务会在终端输出：

```JSON
> Task :app-startup:listAssets
*********************************************
********** -- ListAssetsTask -- *************
******* -- projectDir/assets.json -- ********
*********************************************
Total assets size: 15839513 bytes.
```

同时会在项目的根目录输出 assets.json 报告，示例如下：

```JSON
{
    "xxx:xxx:5.13.1-SNAPSHOT": [    // AAR 名称
        {
            "fileName": "/.../assets/mockhome.json",   // assets 资源名 
            "size": 18909                              // assets 大小，单位字节
        },
        // ...
    ],
    // ...
}
```