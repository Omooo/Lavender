package com.omooo.plugin.transform.invoke

import com.omooo.plugin.transform.BaseClassNode
import com.omooo.plugin.util.TransformReporter
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode

/**
 * Author: Omooo
 * Date: 2022/11/6
 * Desc: 检测方法调用
 */
internal class InvokeCheckClassNode(
    classVisitor: ClassVisitor,
    private val params: InvokeCheckParams
) : BaseClassNode(classVisitor) {

    override fun transform() {
        methods.forEach { methodNode ->
            methodNode.instructions.filterIsInstance<MethodInsnNode>().forEach { insnNode ->
                params.packageList.filter {
                    !name.startsWith(it) && insnNode.owner.startsWith(it)
                }.forEach {
                    report(it.replace("/", "."), methodNode.getMethodPlainText())
                }
                params.methodList.filter {
                    // owner 是必须要有的，name 和 desc 可有可无
                    insnNode.owner == it.first
                            && (if (it.second.isNotEmpty()) insnNode.name == it.second else true)
                            && (if (it.third.isNotEmpty()) insnNode.desc == it.third else true)
                }.map {
                    if (it.second.isNotEmpty()) "${it.first.replace("/", ".")}#${it.second}${it.third}"
                    else it.first.replace("/", ".")
                }.forEach {
                    report(it, methodNode.getMethodPlainText())
                }
            }

            // 常量检测
            if (params.constantsList.isNotEmpty()) {
                methodNode.instructions.filterIsInstance<LdcInsnNode>().filter {
                    params.constantsList.contains(it.cst)
                }.forEach {
                    report(it.cst.toString(), methodNode.getMethodPlainText())
                }
            }

            // 字段检测
            if (params.fieldList.isNotEmpty()) {
                methodNode.instructions.filterIsInstance<FieldInsnNode>().forEach { fieldNode ->
                    params.fieldList.filter {
                        it.first == fieldNode.owner && it.second == fieldNode.name && it.third == fieldNode.desc
                    }.map {
                        "${it.first.replace("/", ".")}.${it.second}:${it.third}"
                    }.forEach {
                        report(it, methodNode.getMethodPlainText())
                    }
                }
            }
        }
    }

    /**
     * 获取方法调用的文本描述
     *
     * ag: android.widget.Toast#show()V
     */
    private fun MethodNode.getMethodPlainText(): String {
        return "${this@InvokeCheckClassNode.name.replace("/", ".")}#${name}${desc}"
    }

    /**
     * 输出报告
     *
     * @param key 待检测的 method/package
     * @param text 调用方
     */
    private fun report(key: String, text: String) {
        TransformReporter.writeJsonLineByLine(REPORTER_FILE_NAME, key, text)
    }

}

/** 输出报告的文件名 */
private const val REPORTER_FILE_NAME = "invokeCheckReport.json"