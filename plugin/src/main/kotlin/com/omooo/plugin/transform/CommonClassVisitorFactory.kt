package com.omooo.plugin.transform

import com.android.build.api.instrumentation.*
import com.omooo.plugin.util.isRClass
import com.omooo.plugin.util.isSystemClass
import org.gradle.api.provider.Property
import org.objectweb.asm.ClassVisitor

/**
 * Author: Omooo
 * Date: 2022/11/5
 * Desc: 通用 ClassVisitorFactory
 */
abstract class CommonClassVisitorFactory : AsmClassVisitorFactory<CheckParams> {

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        if (classContext.currentClassData.isRClass()
            || classContext.currentClassData.isSystemClass()
        ) {
            return nextClassVisitor
        }
        return BaseClassNode(nextClassVisitor, parameters.get())
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        return true
    }
}