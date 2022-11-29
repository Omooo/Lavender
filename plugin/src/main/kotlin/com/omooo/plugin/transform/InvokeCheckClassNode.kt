package com.omooo.plugin.transform

import com.omooo.plugin.util.toPlainText
import com.omooo.plugin.util.writeJson
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.tree.MethodInsnNode

/**
 * Author: Omooo
 * Date: 2022/11/6
 * Desc: 检测方法调用
 */
internal class InvokeCheckClassNode(
    classVisitor: ClassVisitor,
    private val params: CheckInvokeParams
) : BaseClassNode(classVisitor) {

    override fun transform() {
        if (params.methodList.isEmpty() && params.packageList.isEmpty()) {
            return
        }
        methods.forEach { methodNode ->
            methodNode.instructions.filterIsInstance<MethodInsnNode>().forEach { insnNode ->
                params.packageList.filter {
                    !name.startsWith(it) && insnNode.owner.startsWith(it)
                }.forEach {
                    val text = "$name#${methodNode.name}${methodNode.desc}"
                    report(it, text)
                }
                params.methodList.filter {
                    insnNode.toPlainText() == it
                }.forEach {
                    val text = "$name#${methodNode.name}${methodNode.desc}"
                    report(it, text)
                }
            }
        }
    }

    /**
     * 输出报告
     *
     * @param key 待检测的 method/package
     * @param text 调用方
     */
    private fun report(key: String, text: String) {
        writeJson(REPORTER_FILE_NAME, key, text)
    }

}

/** 输出报告的文件名 */
private const val REPORTER_FILE_NAME = "invokeCheckReporter.json"