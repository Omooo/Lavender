package com.omooo.plugin

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.omooo.plugin.bean.CwebpCompressExtension
import com.omooo.plugin.bean.PrintInvokeExtension
import com.omooo.plugin.spi.VariantProcessor
import com.omooo.plugin.transform.CommonClassVisitorFactory
import com.omooo.plugin.transform.assets.UnusedAssetsCheckClassVisitorFactory
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.*

/**
 * Author: Omooo
 * Date: 2019/9/27
 * Version: v0.1.0
 * Desc: Plugin
 */
class Lavender : Plugin<Project> {

    override fun apply(project: Project) {
        println("apply plugin: 'Lavender'")
        project.extensions.findByName("android")
            ?: throw GradleException("$project is not an Android project.")

        val invokeExtension =
            project.extensions.create("invokeCheckConfig", PrintInvokeExtension::class.java)

        project.extensions.create("compressWebpConfig", CwebpCompressExtension::class.java)

        val androidExtension = project.extensions.getByType(AndroidComponentsExtension::class.java)
        androidExtension.onVariants { variant ->
            variant.instrumentation.transformClassesWith(
                CommonClassVisitorFactory::class.java,
                InstrumentationScope.ALL
            ) {
                it.methodList = invokeExtension.methodList
                it.packageList = invokeExtension.packageList
            }
            variant.instrumentation.transformClassesWith(
                UnusedAssetsCheckClassVisitorFactory::class.java,
                InstrumentationScope.ALL
            ) {
                it.assetsFilePath = "${project.projectDir.parent}/assets.json"
            }
            variant.instrumentation.setAsmFramesComputationMode(FramesComputationMode.COPY_FRAMES)
        }

        val variantProcessorList =
            ServiceLoader.load(VariantProcessor::class.java, javaClass.classLoader).toList()
        if (project.state.executed) {
            project.registerTask(variantProcessorList)
        } else {
            project.afterEvaluate {
                project.registerTask(variantProcessorList)
            }
        }
    }

    /**
     * 注册 Task
     */
    private fun Project.registerTask(processors: List<VariantProcessor>) {
        when (val android = project.extensions.getByType(BaseExtension::class.java)) {
            is AppExtension -> android.applicationVariants.all { variant ->
                processors.forEach { processor ->
                    processor.process(project, variant)
                }
            }
            is LibraryExtension -> android.libraryVariants.all { variant ->
                processors.forEach { processor ->
                    processor.process(project, variant)
                }
            }
            else -> throw GradleException("$project does not have AppExtension or LibraryExtension.")
        }
    }

}