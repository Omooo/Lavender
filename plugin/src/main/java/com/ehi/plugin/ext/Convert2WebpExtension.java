package com.ehi.plugin.ext;

import java.util.Arrays;

/**
 * Author: Omooo
 * Date: 2020/2/11
 * Version: v0.1.1
 * Desc: Convert2WebpTask 配置
 */
public class Convert2WebpExtension {

    public boolean enableWhenRelease = false;
    public boolean isCheckSize = true;
    public String[] whiteList = new String[]{};
    public String cwebpToolsDir = "";

    public boolean isEnableWhenRelease() {
        return enableWhenRelease;
    }

    public void setEnableWhenRelease(boolean enableWhenRelease) {
        this.enableWhenRelease = enableWhenRelease;
    }

    public boolean isCheckSize() {
        return isCheckSize;
    }

    public void setCheckSize(boolean checkSize) {
        isCheckSize = checkSize;
    }

    public String[] getWhiteList() {
        return whiteList;
    }

    public void setWhiteList(String[] whiteList) {
        this.whiteList = whiteList;
    }

    public String getCwebpToolsDir() {
        return cwebpToolsDir;
    }

    public void setCwebpToolsDir(String cwebpToolsDir) {
        this.cwebpToolsDir = cwebpToolsDir;
    }

    @Override
    public String toString() {
        return "Convert2WebpExtension{" +
                "enableWhenRelease=" + enableWhenRelease +
                ", isCheckSize=" + isCheckSize +
                ", whiteList=" + Arrays.toString(whiteList) +
                ", cwebpToolsDir='" + cwebpToolsDir + '\'' +
                '}';
    }
}
