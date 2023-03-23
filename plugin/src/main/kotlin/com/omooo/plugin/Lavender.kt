package com.omooo.plugin

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.omooo.plugin.bean.CwebpCompressExtension
import com.omooo.plugin.bean.InvokeCheckExtension
import com.omooo.plugin.spi.VariantProcessor
import com.omooo.plugin.transform.invoke.InvokeCheckCvFactory
import com.omooo.plugin.transform.systrace.SystraceCvFactory
import com.omooo.plugin.util.TransformReporter
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
            project.extensions.create("invokeCheckConfig", InvokeCheckExtension::class.java)

        project.extensions.create("compressWebpConfig", CwebpCompressExtension::class.java)

        TransformReporter.deleteTransformReporterDir()
        val androidExtension = project.extensions.getByType(AndroidComponentsExtension::class.java)
        androidExtension.onVariants { variant ->
            if (invokeExtension.enable()) {
                variant.instrumentation.transformClassesWith(
                    InvokeCheckCvFactory::class.java,
                    InstrumentationScope.ALL
                ) {
                    it.methodList = invokeExtension.getMethodList()
                    it.packageList = invokeExtension.getPackageList()
                    it.constantsList = invokeExtension.constantsList.toList()
                    it.fieldList = invokeExtension.getFieldList()
                }
            }
//            variant.instrumentation.transformClassesWith(
//                SystraceCvFactory::class.java,
//                InstrumentationScope.ALL
//            ) {}
//            variant.instrumentation.transformClassesWith(
//                UnusedAssetsCheckClassVisitorFactory::class.java,
//                InstrumentationScope.ALL
//            ) {
//                it.assetsFilePath = "${project.projectDir.parent}/assets.json"
//            }
//            variant.instrumentation.transformClassesWith(
//                IdentifierCheckCvFactory::class.java,
//                InstrumentationScope.ALL
//            ) {}
            variant.instrumentation.setAsmFramesComputationMode(FramesComputationMode.COMPUTE_FRAMES_FOR_ALL_CLASSES)
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