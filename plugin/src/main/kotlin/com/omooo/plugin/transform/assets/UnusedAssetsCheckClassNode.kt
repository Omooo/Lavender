package com.omooo.plugin.transform.assets

import com.omooo.plugin.task.ListAssetsTask.AssetFile
import com.omooo.plugin.transform.BaseClassNode
import com.omooo.plugin.util.writeJson
import com.omooo.plugin.util.writeToJson
import org.json.JSONObject
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode
import java.io.File

/**
 * Author: Omooo
 * Date: 2022/11/17
 * Desc: 未使用的 Assets 检测
 */
internal class UnusedAssetsCheckClassNode(
    classVisitor: ClassVisitor,
    private val params: UnusedAssetsParams
) :
    BaseClassNode(classVisitor) {

    /** 未使用的 assets 文件 */
    private var unusedAssetsFilePath: String? = null

    override fun transform() {
        methods.forEach { methodNode ->
            methodNode.instructions.forEachIndexed { index, insnNode ->
                // 从 AssetManager#open 调用点开始往前找到第一个 Idc 指令
                // 直到遇到 Context#getAssets 调用点结束
                if (insnNode.isInvokeOpen()) {
                    for (i in index - 1 downTo 0) {
                        val insn = methodNode.instructions[i]
                        if (insn is LdcInsnNode && insn.cst is String) {
                            report(methodNode, insn.cst.toString())
                            break
                        }
                        if (insn.isInvokeGetAssets()) {
                            break
                        }
                    }
                }
            }
        }

        // 所有 ldc 指令
        methods.flatMap {
            it.instructions
        }.apply {
            if (find { it.isInvokeOpen() } == null) {
                return
            }
            filterIsInstance<LdcInsnNode>().filter {
                it.cst is String
            }.apply {
                forEach {
                    writeJson("assetsUsed.json", it.cst.toString(), name)
                }
                val fileNameList = this.map { it.cst as String }
                val result = mutableMapOf<String, List<AssetFile>>()
                readAssetsMap().forEach {
                    result[it.key] = it.value.filter { assetFile ->
                        assetFile.fileName in fileNameList
                    }
                }
                result.filterValues {
                    it.isEmpty()
                }.writeToJson("${File(params.assetsFilePath).parent}/unused_assets.json")
            }
        }
    }

    private fun readAssetsMap(): Map<String, List<AssetFile>> {
        return runCatching {
            if (unusedAssetsFilePath.isNullOrEmpty()) {
                unusedAssetsFilePath = "${File(params.assetsFilePath).parent}/unused_assets.json"
                File(unusedAssetsFilePath!!).writeText(File(params.assetsFilePath).readText())
            }
            val jsonObj = JSONObject(File(unusedAssetsFilePath!!).readText())
            val map = mutableMapOf<String, List<AssetFile>>()
            jsonObj.keys().forEach {
                val jsonArray = jsonObj.getJSONArray(it)
                val assetsList = mutableListOf<AssetFile>()
                for (i in 0 until jsonArray.length()) {
                    jsonArray.getJSONObject(i).apply {
                        assetsList.add(
                            AssetFile(getString("fileName"), getLong("size"))
                        )
                    }
                }
                map[it] = assetsList
            }
            map
        }.getOrElse {
            println("UnusedAssetsCheckClassNode#readAssetsMap: ${it.message}")
            return emptyMap()
        }
    }

    /**
     * 上报
     *
     * @param methodNode [MethodNode] 调用点
     * @param assetsFileName assets 文件名
     */
    private fun report(methodNode: MethodNode, assetsFileName: String) {
        writeJson("assetsUse.json", assetsFileName, "$name#${methodNode.name}${methodNode.desc}")
    }

    /**
     * 是否是调用 AssetManager#open 方法
     *
     * @return true: 是
     */
    private fun AbstractInsnNode.isInvokeOpen(): Boolean {
        return this is MethodInsnNode
                && this.opcode == Opcodes.INVOKEVIRTUAL
                && this.owner == OWNER_ASSET_MANAGER
                && this.name == METHOD_NAME_OPEN
                && this.desc == METHOD_DESC_OPEN
    }

    /**
     * 是否是调用 Context#getAssets 方法
     * 不校验 owner，owner 可能是 Context 子类
     *
     * @return true: 是
     */
    private fun AbstractInsnNode.isInvokeGetAssets(): Boolean {
        return this is MethodInsnNode
                && this.opcode == Opcodes.INVOKEVIRTUAL
                && this.name == METHOD_NAME_GET_ASSETS
                && this.desc == METHOD_DESC_GET_ASSETS
    }
}

private const val OWNER_ASSET_MANAGER = "android/content/res/AssetManager"
private const val METHOD_NAME_OPEN = "open"
private const val METHOD_DESC_OPEN = "(Ljava/lang/String;)Ljava/io/InputStream;"

private const val METHOD_NAME_GET_ASSETS = "getAssets"
private const val METHOD_DESC_GET_ASSETS = "()Landroid/content/res/AssetManager;"

