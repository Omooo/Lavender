package com.omooo.plugin.util

import com.omooo.plugin.bean.WebpToolBean

/**
 * Created by Omooo
 * Date: 2020-02-13
 * Desc:
 */
class WebpToolUtil {

    companion object {

        /**
         * 执行 cwebp 命令压缩图片
         */
        fun cmd(cmd: String, params: String) {
            val cmdStr = when {
                isWindows() ->
                    "${WebpToolBean.getToolsDirPath()}/windows/$cmd $params"
                isMac() ->
                    "${WebpToolBean.getToolsDirPath()}/mac/$cmd $params"
                isLinux() ->
                    "${WebpToolBean.getToolsDirPath()}/linux/$cmd $params"
                else -> ""
            }
            if (cmd == "") {
                println("Cwebp can't support this system.")
                return
            }
            Runtime.getRuntime().exec(cmdStr).waitFor()
        }

        private fun isLinux(): Boolean {
            return System.getProperty("os.name").startsWith("Linux")
        }

        private fun isMac(): Boolean {
            return System.getProperty("os.name").startsWith("Mac OS")
        }

        private fun isWindows(): Boolean {
            return System.getProperty("os.name").toLowerCase().contains("win")
        }

    }
}