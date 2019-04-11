package com.lianer.core.invest.bean;

import java.util.List;

/**
 * 首页合约单数据请求对象
 *
 * @author allison
 */
public class ContractHomeRequest {

    private List<String> contractStatusList;
    private List<String> mortgageAssetsTypeList;
    private int count;
    private int offset;
    private String orderby;

    public void setContractStatusList(List<String> contractStatusList) {
        this.contractStatusList = contractStatusList;
    }

    public List<String> getContractStatusList() {
        return contractStatusList;
    }

    public List<String> getMortgageAssetsTypeList() {
        return mortgageAssetsTypeList;
    }

    public void setMortgageAssetsTypeList(List<String> mortgageAssetsTypeList) {
        this.mortgageAssetsTypeList = mortgageAssetsTypeList;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }

    public String getOrderby() {
        return orderby;
    }

    public void setOrderby(String orderby) {
        this.orderby = orderby;
    }
}