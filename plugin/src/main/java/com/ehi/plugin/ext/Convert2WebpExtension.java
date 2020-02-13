package com.ehi.plugin.ext;

import java.util.Arrays;

/**
 * Author: Omooo
 * Date: 2020/2/11
 * Version: v0.1.1
 * Desc: Convert2WebpTask 配置
 */
public class Convert2WebpExtension {

    public boolean enableWhenDebug = false;
    public boolean isCheckSize = true;
    public String[] whiteList = new String[]{};
    public String[] bigImageWhiteList = new String[]{};
    public String cwebpToolsDir = "";
    public float maxSize = 500 * 1024;

    public boolean isEnableWhenDebug() {
        return enableWhenDebug;
    }

    public void setEnableWhenDebug(boolean enableWhenDebug) {
        this.enableWhenDebug = enableWhenDebug;
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

    public String[] getBigImageWhiteList() {
        return bigImageWhiteList;
    }

    public void setBigImageWhiteList(String[] bigImageWhiteList) {
        this.bigImageWhiteList = bigImageWhiteList;
    }

    public String getCwebpToolsDir() {
        return cwebpToolsDir;
    }

    public void setCwebpToolsDir(String cwebpToolsDir) {
        this.cwebpToolsDir = cwebpToolsDir;
    }

    public float getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(float maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public String toString() {
        return "Convert2WebpExtension{" +
                "enableWhenDebug=" + enableWhenDebug +
                ", isCheckSize=" + isCheckSize +
                ", whiteList=" + Arrays.toString(whiteList) +
                ", bigImageWhiteList=" + Arrays.toString(bigImageWhiteList) +
                ", cwebpToolsDir='" + cwebpToolsDir + '\'' +
                ", maxSize=" + maxSize +
                '}';
    }
}
