package com.lianer.core.contract.bean;

import java.util.List;

/**
 * 借币合约单数据请求对象
 * @author allison
 */
public class ContractBorrowRequest {

    private List<String> contractStatusList;
    private int count;
    private int offset;
    private String borrowAddress;
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

    public String getBorrowAddress() {
        return borrowAddress;
    }

    public void setBorrowAddress(String borrowAddress) {
        this.borrowAddress = borrowAddress;
    }
}