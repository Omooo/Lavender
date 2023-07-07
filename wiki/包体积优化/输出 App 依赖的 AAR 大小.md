---
输出 App 依赖的 AAR 大小
---

| 功能          | 使用                  | 输出                    | 主要实现类      |
| ------------- | --------------------- | ----------------------- | --------------- |
| 输出 AAR 大小 | ./gradlew listAarSize | projectDir/aarSize.json | ListAarSizeTask |

#### 一、如何使用

在接入 Lavender 的 Application 工程中，直接运行：

```JSON
./gradlew listAarSize
```

该任务会在项目的 根目录输出一个 aarSize.json 文件（默认按照 AAR 大小倒序排序），类似如下：

```JSON
{
    // key: AAR 名称；value: AAR 大小，单位 kb
    "com.xxx.1:5.11.0-SNAPSHOT": "24524kb",
    "com.xxx.2:1.0.14": "23769kb",
    "com.xxx.3:8.7.10102": "22101kb",
    // ...
}    
```