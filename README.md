---
EHiPlugin
---

概述：

| 功能                         | 使用                      | 输出                        | 主要实现类            |
| ---------------------------- | ------------------------- | --------------------------- | --------------------- |
| 输出 app 及其依赖的 aar 权限 | ./gradlew listPermissions | projectDir/permissions.json | ListPermissionTask    |
| 重复资源监测                 | ./gradlew repeatRes       | projectDir/repeatRes.json   | RepeatResDetectorTask |
|                              |                           |                             |                       |

