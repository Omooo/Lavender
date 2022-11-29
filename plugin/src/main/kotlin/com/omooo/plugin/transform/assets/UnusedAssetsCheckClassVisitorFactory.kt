package com.omooo.plugin.transform.assets

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import com.omooo.plugin.util.isRClass
import com.omooo.plugin.util.isSystemClass
import org.objectweb.asm.ClassVisitor

/**
 * Author: Omooo
 * Date: 2022/11/17
 * Desc: 未使用 Assets 检测注册 Factory
 */
abstract class UnusedAssetsCheckClassVisitorFactory :
    AsmClassVisitorFactory<UnusedAssetsParams> {

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        if (classContext.currentClassData.isRClass()
            || classContext.currentClassData.isSystemClass()
        ) {
            return nextClassVisitor
        }
        return UnusedAssetsCheckClassNode(nextClassVisitor, parameters.get())
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        return true
    }
}