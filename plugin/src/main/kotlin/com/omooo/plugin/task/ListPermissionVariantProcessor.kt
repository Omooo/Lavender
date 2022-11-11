package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.omooo.plugin.spi.VariantProcessor
import com.google.auto.service.AutoService

/**
 * Author: Omooo
 * Date: 2019/9/27
 * Version: v0.1.0
 * Desc: 注册 ListPermissionTask
 * @see ListPermissionTask
 */
@AutoService(VariantProcessor::class)
class ListPermissionVariantProcessor : VariantProcessor {
    override fun process(variant: BaseVariant) {
//        val variantData = (variant as ApplicationVariantImpl).variantData
//
//        val tasks = variantData.scope.globalScope.project.tasks
//        val listPermission = tasks.findByName("listPermissions") ?: tasks.create("listPermissions")
//        tasks.create(
//            "list${variant.name.capitalize()}Permissions",
//            ListPermissionTask::class.java
//        ) {
//            it.variant = variant
//            // 如果闭包返回 false，则不能重用此任务的以前输出，并且将执行该任务
//            // 这意味着任务已经过期，不会从构建缓存加载任何输出
//            it.outputs.upToDateWhen { false }
//        }.also {
//            listPermission.dependsOn(it)
//        }
    }

}