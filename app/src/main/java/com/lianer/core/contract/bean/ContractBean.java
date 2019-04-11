package com.lianer.core.contract.bean;

import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 消息中心item实体类
 */
@Entity
public class ContractBean {

    @Id(autoincrement = true)
    /**
     * 合约标识
     */
    private Long contractId;
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
    //需转入抵押资产
    public String needMortgage;
    //Token地址
    public String tokenAddress;
    //到期本息
    public String expire;
    //还款时间
    public String endTime;
    //投资时间
    public String investmentTime;
    //创建时间
    public String createTime;
    //到期时间
    public String expiryTime;
    //手续费
    public String serviceCharge;
    //实际到账
    public String actualAccount;
    
    //合约类型  1：抵押eth   2:ERC20
    public String contractType;
    // 抵押资产地址
    public String borrowerToken;
    // 借币token 地址
    public String lenderToken;

    @Generated(hash = 437074344)
    public ContractBean(Long contractId, String contractAddress,
            String contractState, String borrowerAddress, String investorAddress,
            String amount, String cycle, String interest, String mortgage,
            String needMortgage, String tokenAddress, String expire, String endTime,
            String investmentTime, String createTime, String expiryTime,
            String serviceCharge, String actualAccount, String contractType,
            String borrowerToken, String lenderToken) {
        this.contractId = contractId;
        this.contractAddress = contractAddress;
        this.contractState = contractState;
        this.borrowerAddress = borrowerAddress;
        this.investorAddress = investorAddress;
        this.amount = amount;
        this.cycle = cycle;
        this.interest = interest;
        this.mortgage = mortgage;
        this.needMortgage = needMortgage;
        this.tokenAddress = tokenAddress;
        this.expire = expire;
        this.endTime = endTime;
        this.investmentTime = investmentTime;
        this.createTime = createTime;
        this.expiryTime = expiryTime;
        this.serviceCharge = serviceCharge;
        this.actualAccount = actualAccount;
        this.contractType = contractType;
        this.borrowerToken = borrowerToken;
        this.lenderToken = lenderToken;
    }

    @Generated(hash = 1096666658)
    public ContractBean() {
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

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

    public String getExpire() {
        return expire;
    }

    public void setExpire(String expire) {
        this.expire = expire;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getInvestmentTime() {
        return investmentTime;
    }

    public void setInvestmentTime(String investmentTime) {
        this.investmentTime = investmentTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(String expiryTime) {
        this.expiryTime = expiryTime;
    }

    public String getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(String serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public String getActualAccount() {
        return actualAccount;
    }

    public void setActualAccount(String actualAccount) {
        this.actualAccount = actualAccount;
    }


    public Long getContractId() {
        return this.contractId;
    }

    public String getContractType() {
        return this.contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getBorrowerToken() {
        return this.borrowerToken;
    }

    public void setBorrowerToken(String borrowerToken) {
        this.borrowerToken = borrowerToken;
    }

    public String getLenderToken() {
        return this.lenderToken;
    }

    public void setLenderToken(String lenderToken) {
        this.lenderToken = lenderToken;
    }


    @Override
    public String toString() {
        return "ContractBean{" +
                "contractId=" + contractId +
                ", contractAddress='" + contractAddress + '\'' +
                ", contractState='" + contractState + '\'' +
                ", borrowerAddress='" + borrowerAddress + '\'' +
                ", investorAddress='" + investorAddress + '\'' +
                ", amount='" + amount + '\'' +
                ", cycle='" + cycle + '\'' +
                ", interest='" + interest + '\'' +
                ", mortgage='" + mortgage + '\'' +
                ", needMortgage='" + needMortgage + '\'' +
                ", tokenAddress='" + tokenAddress + '\'' +
                ", expire='" + expire + '\'' +
                ", endTime='" + endTime + '\'' +
                ", investmentTime='" + investmentTime + '\'' +
                ", createTime='" + createTime + '\'' +
                ", expiryTime='" + expiryTime + '\'' +
                ", serviceCharge='" + serviceCharge + '\'' +
                ", actualAccount='" + actualAccount + '\'' +
                ", contractType='" + contractType + '\'' +
                ", borrowerToken='" + borrowerToken + '\'' +
                ", lenderToken='" + lenderToken + '\'' +
                '}';
    }
}