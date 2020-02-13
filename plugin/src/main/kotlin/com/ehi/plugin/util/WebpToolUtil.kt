package com.ehi.plugin.util

import com.ehi.plugin.bean.WebpToolBean

/**
 * Created by Omooo
 * Date: 2020-02-13
 * Desc:
 */
class WebpToolUtil {

    companion object {

        fun cmd(cmd: String, params: String) {
            val system = System.getProperty("os.name")
            val cmdStr = when (system) {
                "Windows" ->
                    "${WebpToolBean.getToolsDirPath()}/windows/$cmd $params"
                "Mac OS X" ->
                    "${WebpToolBean.getToolsDirPath()}/mac/$cmd $params"
                else -> ""
            }
            if (cmd == "") {
                println("Cwebp can't support this system.")
                return
            }
            outputMessage(cmdStr)
        }

        private fun outputMessage(cmdStr: String) {
            val process = Runtime.getRuntime().exec(cmdStr)
            process.waitFor()
        }

    }
}