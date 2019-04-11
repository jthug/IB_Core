package com.lianer.core.contract.bean;

import java.util.List;

/**
 * 投资合约单数据请求对象
 * @author allison
 */
public class ContractInvestRequest {

    private List<String> contractStatusList;
    private int count;
    private int offset;
    private String investmentAddress;
    public void setContractStatusList(List<String> contractStatusList) {
         this.contractStatusList = contractStatusList;
     }
     public List<String> getContractStatusList() {
         return contractStatusList;
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

    public String getInvestmentAddress() {
        return investmentAddress;
    }

    public void setInvestmentAddress(String investmentAddress) {
        this.investmentAddress = investmentAddress;
    }
}