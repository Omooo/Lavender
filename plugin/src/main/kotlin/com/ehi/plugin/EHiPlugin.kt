package com.ehi.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.ehi.plugin.spi.VariantProcessor
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.ArtifactRepository
import java.net.URI
import java.util.*

/**
 * Author: Omooo
 * Date: 2019/9/27
 * Version: v0.1.0
 * Desc: Plugin
 */
class EHiPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        println(
            """
            /********************************/
            /********** EHi Plugin **********/
            /********************************/
        """.trimIndent()
        )

        project.repositories.maven {
            it.url = URI("http://192.168.9.230:8081/repository/app-releases/")
        }
        project.dependencies.add("api", "com.ehi.plugin:annotation:0.1.1")

        when {
            project.plugins.hasPlugin("com.android.application") -> project.extensions.getByType(
                AppExtension::class.java
            ).let { android ->
                project.afterEvaluate {
                    ServiceLoader.load(VariantProcessor::class.java, javaClass.classLoader)
                        .toList().let { processes ->
                            android.applicationVariants.forEach { variant ->
                                processes.forEach {
                                    it.process(variant)
                                }
                            }
                        }
                }
            }

            project.plugins.hasPlugin("com.android.library") -> project.extensions.getByType(
                LibraryExtension::class.java
            ).let { android ->
                project.afterEvaluate {
                    ServiceLoader.load(VariantProcessor::class.java, javaClass.classLoader)
                        .toList().let { processes ->
                            android.libraryVariants.forEach { variant ->
                                processes.forEach {
                                    it.process(variant)
                                }
                            }
                        }

                }
            }
        }
    }
}