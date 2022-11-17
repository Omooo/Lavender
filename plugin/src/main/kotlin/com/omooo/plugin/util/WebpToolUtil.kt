package com.omooo.plugin.util

import com.omooo.plugin.bean.WebpToolBean

/**
 * Created by Omooo
 * Date: 2020-02-13
 * Desc:
 */
class WebpToolUtil {

    companion object {

        fun cmd(cmd: String, params: String) {
            val cmdStr = when (System.getProperty("os.name")) {
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