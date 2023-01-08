package com.omooo.plugin.transform.systrace

import com.omooo.plugin.bean.ASM_VERSION
import com.omooo.plugin.transform.BaseClassVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter
import java.lang.reflect.Modifier

/**
 * Author: Omooo
 * Date: 2022/12/28
 * Desc:
 */
internal class SystraceClassVisitor(classVisitor: ClassVisitor) : BaseClassVisitor(classVisitor) {

    var traceClassFlag = false

    var className = ""

    override fun visit(
        version: Int,
        access: Int,
        name: String,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        traceClassFlag = !Modifier.isAbstract(access) && !Modifier.isInterface(access)
                && !Modifier.isNative(access) && 0 == (access and Opcodes.ACC_ANNOTATION)
        className = name
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        if (!traceClassFlag || Modifier.isAbstract(access) || Modifier.isInterface(access)
            || Modifier.isNative(access)
            || name == "<init>" || name == "<clinit>"
        ) {
            return super.visitMethod(access, name, descriptor, signature, exceptions)
        }
        val methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        return InternalMethodVisitor(className, methodVisitor, access, name, descriptor)
    }

    class InternalMethodVisitor(
        private val className: String,
        methodVisitor: MethodVisitor,
        access: Int,
        private val methodName: String,
        descriptor: String
    ) :
        AdviceAdapter(ASM_VERSION, methodVisitor, access, methodName, descriptor) {

        override fun onMethodEnter() {
            println("植入成功_: $className#$methodName")
            val sectionName = "$className#$methodName".let {
                if (it.length > 127) "${it.substring(0, 124)}..." else it
            }
            mv.visitLdcInsn(sectionName)
            mv.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "android/os/Trace",
                "beginSection",
                "(Ljava/lang/String;)V",
                false
            )
            super.onMethodEnter()
        }

        override fun onMethodExit(opcode: Int) {
            mv.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "android/os/Trace",
                "endSection",
                "()V",
                false
            )
            super.onMethodExit(opcode)
        }

    }
}