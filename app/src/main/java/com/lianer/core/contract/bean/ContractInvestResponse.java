package com.lianer.core.contract.bean;

import java.io.Serializable;

public class ContractInvestResponse implements Serializable{
    /**
     * 标识
     */
    private int id;
    /**
     * 合约地址
     */
    private String contractAddress = "";
    /**
     * 投资人地址
     */
    private String borrowerAddress = "";
    /**
     * 数量
     */
    private String borrowerAmount = "";
    /**
     * 投资哈希
     */
    private String borrowerHash = "";
    /**
     * 状态
     */
    private String status = "";
    /**
     * 投资时间
     */
    private String datetime = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getBorrowerAddress() {
        return borrowerAddress;
    }

    public void setBorrowerAddress(String borrowerAddress) {
        this.borrowerAddress = borrowerAddress;
    }

    public String getBorrowerAmount() {
        return borrowerAmount;
    }

    public void setBorrowerAmount(String borrowerAmount) {
        this.borrowerAmount = borrowerAmount;
    }

    public String getBorrowerHash() {
        return borrowerHash;
    }

    public void setBorrowerHash(String borrowerHash) {
        this.borrowerHash = borrowerHash;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    @Override
    public String toString() {
        return "ContractInvestResponse{" +
                "id=" + id +
                ", contractAddress='" + contractAddress + '\'' +
                ", borrowerAddress='" + borrowerAddress + '\'' +
                ", borrowerAmount='" + borrowerAmount + '\'' +
                ", borrowerHash='" + borrowerHash + '\'' +
                ", status='" + status + '\'' +
                ", datetime='" + datetime + '\'' +
                '}';
    }
}