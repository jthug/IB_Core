package com.lianer.core.market.bean;

import com.lianer.core.base.BaseBean;

import java.util.List;

/**
 * 抵押资产相应数据

 * @author allison
 */
public class MortgagedAssetsResponse extends BaseBean{

    List<MortgagedfAssetsBean> data;

    public List<MortgagedfAssetsBean> getData() {
        return data;
    }

    public void setData(List<MortgagedfAssetsBean> data) {
        this.data = data;
    }
}
