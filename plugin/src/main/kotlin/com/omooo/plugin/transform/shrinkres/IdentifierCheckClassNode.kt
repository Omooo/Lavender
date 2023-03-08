package com.omooo.plugin.transform.shrinkres

import com.omooo.plugin.transform.BaseClassNode
import com.omooo.plugin.util.TransformReporter
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode

/**
 * Author: Omooo
 * Date: 2022/12/9
 * Desc: resources.getIdentifier 调用检查
 */
internal class IdentifierCheckClassNode(classVisitor: ClassVisitor) : BaseClassNode(classVisitor) {

    override fun transform() {
        methods.filter {
            it.name == "onCreate"
        }.forEach { methodNode ->
            methodNode.instructions.forEachIndexed { index, insnNode ->
                // 从 Resources#getIdentifier 调用点开始往前找到第一个 Idc 指令
                // 直到遇到 getResources() 调用点结束
                if (insnNode.isInvokeGetIdentifier()) {
                    report(methodNode, "")
                    for (i in index - 1 downTo 0) {
                        val insn = methodNode.instructions[i]
                        if (insn is LdcInsnNode && insn.cst is String) {
                            report(methodNode, insn.cst.toString())
                        }
//                        if (insn.isInvokeGetResources()) {
//                            break
//                        }
                    }
                }
            }
        }
    }

    /**
     * 上报
     *
     * @param methodNode [MethodNode] 调用点
     * @param resName assets 文件名
     */
    private fun report(methodNode: MethodNode, resName: String) {
        TransformReporter.writeJsonLineByLine("resIdentifier.json", "$name#${methodNode.name}${methodNode.desc}", resName)
    }

    private fun AbstractInsnNode.isInvokeGetIdentifier(): Boolean {
        return this is MethodInsnNode
                && opcode == Opcodes.INVOKEVIRTUAL
                && owner == OWNER_RESOURCES
                && name == METHOD_NAME_GET_IDENTIFIER
                && desc == METHOD_DESC_GET_IDENTIFIER
    }

    private fun AbstractInsnNode.isInvokeGetResources(): Boolean {
        return this is MethodInsnNode
                && opcode == Opcodes.INVOKEVIRTUAL
                && name == METHOD_NAME_GET_RESOURCES
                && desc == METHOD_DESC_GET_RESOURCES
    }
}

private const val OWNER_RESOURCES = "android/content/res/Resources"
private const val METHOD_NAME_GET_IDENTIFIER = "getIdentifier"
private const val METHOD_DESC_GET_IDENTIFIER =
    "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I"

private const val METHOD_NAME_GET_RESOURCES = "getResources"
private const val METHOD_DESC_GET_RESOURCES = "()Landroid/content/res/Resources;"
