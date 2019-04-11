package com.lianer.core.contract.bean;

import java.util.List;

/**
 * 借币合约单数据请求对象
 *
 * @author allison
 */
public class ContractDynamicRequest {

    private List<String> specificStatusList;
    private int count;
    private int offset;
    private String borrowAddressOrinvestmentAddress;

    public void setContractStatusList(List<String> contractStatusList) {
        this.specificStatusList = contractStatusList;
    }

    public List<String> getContractStatusList() {
        return specificStatusList;
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

    public String getBorrowAddressOrinvestmentAddress() {
        return borrowAddressOrinvestmentAddress;
    }

    public void setBorrowAddressOrinvestmentAddress(String borrowAddressOrinvestmentAddress) {
        this.borrowAddressOrinvestmentAddress = borrowAddressOrinvestmentAddress;
    }
}