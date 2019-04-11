package com.lianer.core.market.bean;

import com.lianer.core.base.BaseBean;

import java.util.List;

/**
 * 合约市场响应数据
 *
 * @author allison
 */
public class MarketContractResponse extends BaseBean {

    private List<MarketContractBean> data;

    public List<MarketContractBean> getData() {
        return data;
    }

    public void setData(List<MarketContractBean> data) {
        this.data = data;
    }

}

