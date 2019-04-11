package com.lianer.core.market.bean;

import com.lianer.core.base.BaseBean;

import java.util.List;

/**
 * banner请求响应数据
 *
 * @author allison
 */
public class BannerResponse extends BaseBean {

    private List<BannerBean> data;

    public List<BannerBean> getData() {
        return data;
    }

    public void setData(List<BannerBean> data) {
        this.data = data;
    }
}
