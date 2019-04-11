package com.lianer.core.SmartContract;


public class IBContractStatues {
    //合约地址
    public String contractAddress;
    //合约状态
    public String contractState;
    //借币人地址
    public String borrowerAddress;
    //投资人地址
    public String investorAddress;
    //	借币数量
    public String amount;
    //周期
    public String cycle;
    //利率
    public String interest;
    //mortgage
    public String mortgage;
//    //手续费
//    public String serviceCharge;
    //需转入抵押资产
    public String needMortgage;
    //Token地址
    public String tokenAddress;
    //投资时间
    public String investmentTime;
    //结束时间
    public String endTime;
    //到期本息
    public String expire;
    //到期时间
    public String expiryTime;
    //创建时间
    public String createTime;

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getContractState() {
        return contractState;
    }

    public void setContractState(String contractState) {
        this.contractState = contractState;
    }

    public String getBorrowerAddress() {
        return borrowerAddress;
    }

    public void setBorrowerAddress(String borrowerAddress) {
        this.borrowerAddress = borrowerAddress;
    }

    public String getInvestorAddress() {
        return investorAddress;
    }

    public void setInvestorAddress(String investorAddress) {
        this.investorAddress = investorAddress;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getMortgage() {
        return mortgage;
    }

    public void setMortgage(String mortgage) {
        this.mortgage = mortgage;
    }

    public String getNeedMortgage() {
        return needMortgage;
    }

    public void setNeedMortgage(String needMortgage) {
        this.needMortgage = needMortgage;
    }

    public String getTokenAddress() {
        return tokenAddress;
    }

    public void setTokenAddress(String tokenAddress) {
        this.tokenAddress = tokenAddress;
    }

    public String getInvestmentTime() {
        return investmentTime;
    }

    public void setInvestmentTime(String investmentTime) {
        this.investmentTime = investmentTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getExpire() {
        return expire;
    }

    public void setExpire(String expire) {
        this.expire = expire;
    }

    public String getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(String expiryTime) {
        this.expiryTime = expiryTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "IBContractStatues{" +
                "contractAddress='" + contractAddress + '\'' +
                ", contractState='" + contractState + '\'' +
                ", borrowerAddress='" + borrowerAddress + '\'' +
                ", investorAddress='" + investorAddress + '\'' +
                ", amount='" + amount + '\'' +
                ", cycle='" + cycle + '\'' +
                ", interest='" + interest + '\'' +
                ", mortgage='" + mortgage + '\'' +
                ", needMortgage='" + needMortgage + '\'' +
                ", tokenAddress='" + tokenAddress + '\'' +
                ", investmentTime='" + investmentTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", expire='" + expire + '\'' +
                ", expiryTime='" + expiryTime + '\'' +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
