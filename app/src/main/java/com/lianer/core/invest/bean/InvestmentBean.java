package com.lianer.core.invest.bean;

public class InvestmentBean {

    private int id;
    private String contractAddress;
    private String borrowerAddress;
    private String borrowerAmount;
    private String borrowerHash;
    private String status;
    private String datetime;
    private String contractHash;
    private String principalAndInterest;

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

    public String getContractHash() {
        return contractHash;
    }

    public void setContractHash(String contractHash) {
        this.contractHash = contractHash;
    }

    public String getPrincipalAndInterest() {
        return principalAndInterest;
    }

    public void setPrincipalAndInterest(String principalAndInterest) {
        this.principalAndInterest = principalAndInterest;
    }

    @Override
    public String toString() {
        return "InvestmentBean{" +
                "id=" + id +
                ", contractAddress='" + contractAddress + '\'' +
                ", borrowerAddress='" + borrowerAddress + '\'' +
                ", borrowerAmount='" + borrowerAmount + '\'' +
                ", borrowerHash='" + borrowerHash + '\'' +
                ", status='" + status + '\'' +
                ", datetime='" + datetime + '\'' +
                ", contractHash='" + contractHash + '\'' +
                ", principalAndInterest='" + principalAndInterest + '\'' +
                '}';
    }
}
