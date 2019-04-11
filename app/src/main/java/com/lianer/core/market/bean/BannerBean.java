package com.lianer.core.market.bean;

/**
 * 轮播图实体类
 *
 * @author allison
 */
public class BannerBean {

    private String id;
    private String position;
    private String createTime;
    private BannerInfo cn;
    private BannerInfo en;

    public String getId() {
        return id;
    }

    public String getPosition() {
        return position;
    }

    public String getCreateTime() {
        return createTime;
    }

    public BannerInfo getCn() {
        return cn;
    }

    public BannerInfo getEn() {
        return en;
    }
}

