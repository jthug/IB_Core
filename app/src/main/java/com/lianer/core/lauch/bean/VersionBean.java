package com.lianer.core.lauch.bean;

/**
 * 版本信息
 *
 * @author allison
 */
public class VersionBean {

    /**
     * 数据id
     */
    String id;

    /**
     * 版本名称
     */
    String versionName;

    /**
     * 版本号
     */
    String versionCode;

    /**
     * 是否强制更新  0不强制，1强制
     */
    String forceUpdate;

    /**
     * 下载地址
     */
    String url;

    /**
     * 介绍国际化
     */
    LogLanguages logLanguages;

    /**
     * 设备类型  iOS、Android
     */
    String deviceType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(String forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LogLanguages getLogLanguages() {
        return logLanguages;
    }

    public void setLogLanguages(LogLanguages logLanguages) {
        this.logLanguages = logLanguages;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public class LogLanguages {
        /**
         * 中文
         */
        String cn;

        /**
         * 英文
         */
        String en;

        public String getCn() {
            return cn;
        }

        public void setCn(String cn) {
            this.cn = cn;
        }

        public String getEn() {
            return en;
        }

        public void setEn(String en) {
            this.en = en;
        }
    }

}
