package com.omooo.plugin.scan

import com.omooo.plugin.util.green
import com.omooo.plugin.util.red
import groovy.json.JsonOutput
import org.gradle.api.Project
import org.gradle.api.internal.GradleInternal
import org.gradle.internal.operations.*
import org.gradle.internal.operations.trace.BuildOperationTrace

/**
 * Author: Omooo
 * Date: 2024/4/24
 * Desc:
 */
internal class BuildScan {

    fun scan(project: Project) {
        (project.gradle as? GradleInternal)?.services?.get(BuildOperationListenerManager::class.java)
            ?.addListener(object : BuildOperationListener {
                override fun started(
                    buildOperation: BuildOperationDescriptor,
                    startEvent: OperationStartEvent
                ) {
                    // ignore
                }

                override fun progress(
                    operationIdentifier: OperationIdentifier,
                    progressEvent: OperationProgressEvent
                ) {
                    // ignore
                }

                override fun finished(
                    buildOperation: BuildOperationDescriptor,
                    finishEvent: OperationFinishEvent
                ) {
                    val duration = finishEvent.endTime - finishEvent.startTime
                    val details = BuildOperationTrace.toSerializableModel(buildOperation.details)
                    val result = BuildOperationTrace.toSerializableModel(finishEvent.result)
                    println("""
                        ${green("Finished: ${buildOperation.name}")}
                            ids: ${buildOperation.id} ${buildOperation.parentId}
                            displayName: ${buildOperation.displayName}
                            progressDisplayName: ${buildOperation.progressDisplayName}
                            metadata: ${buildOperation.metadata}
                            duration: $duration
                            details: ${JsonOutput.toJson(details)}
                            result: ${JsonOutput.toJson(result)}
                    """.trimIndent())
                }

            })
    }
}