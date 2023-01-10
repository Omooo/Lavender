package com.omooo.plugin.transform.invoke

import com.android.build.api.instrumentation.*
import com.omooo.plugin.util.isRClass
import com.omooo.plugin.util.isSystemClass
import org.objectweb.asm.ClassVisitor

/**
 * Author: Omooo
 * Date: 2022/11/5
 * Desc: 通用 ClassVisitorFactory
 */
abstract class InvokeCheckCvFactory : AsmClassVisitorFactory<InvokeCheckParams> {

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        if (classContext.currentClassData.isRClass()
            || classContext.currentClassData.isSystemClass()
        ) {
            return nextClassVisitor
        }
        return InvokeCheckClassNode(nextClassVisitor, parameters.get())
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        return true
    }
}