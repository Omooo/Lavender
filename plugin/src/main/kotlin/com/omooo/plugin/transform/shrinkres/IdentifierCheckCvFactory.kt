package com.omooo.plugin.transform.shrinkres

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import com.omooo.plugin.util.isRClass
import com.omooo.plugin.util.isSystemClass
import org.objectweb.asm.ClassVisitor

/**
 * Author: Omooo
 * Date: 2022/12/09
 * Desc: resources.getIdentifier 调用检查
 */
abstract class IdentifierCheckCvFactory :
    AsmClassVisitorFactory<InstrumentationParameters.None> {

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        if (classContext.currentClassData.isRClass()
            || classContext.currentClassData.isSystemClass()
        ) {
            return nextClassVisitor
        }
        return IdentifierCheckClassNode(nextClassVisitor)
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        return true
    }
}