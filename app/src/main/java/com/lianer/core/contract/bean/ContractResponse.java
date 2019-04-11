package com.lianer.core.contract.bean;

import java.io.Serializable;

public class ContractResponse implements Serializable{
    /**
     * 合约标识
     */
    private int contractId;
    /**
     * 合约地址
     */
    private String contractAddress = "";
    /**
     * 合约业务状态
     */
    private String contractStatus = "";
    /**
     * 抵押资产类型
     */
    private String mortgageAssetsType = "";
    /**
     * 抵押资产数量
     */
    private String mortgageAssetsAmount = "";
    /**
     * 借币类型（默认ETH）
     */
    private String borrowAssetsType = "";
    /**
     * 借币数量（默认ETH）
     */
    private String borrowAssetsAmount = "";
    /**
     * 利率
     */
    private int interestRate = 0;
    /**
     * 借币周期
     */
    private int timeLimit = 0;
    /**
     * 合约创建时间
     */
    private String contractCreateDate = "";
    /**
     * 合约生效时间
     */
    private String contractEffectDate = "";
    /**
     * 合约到期时间
     */
    private String contractExpireDate = "";
    /**
     * 还款时间
     */
    private String contractRepaymentDate = "";
    /**
     * 资产取回时间
     */
    private String contractTakebackDate = "";
    /**
     * 合约终止时间
     */
    private String contractTerminationDate = "";
    /**
     * 合约解散时间
     */
    private String contractDissolutionDate = "";
    /**
     * 投资人地址
     */
    private String investmentAddress = "";
    /**
     * 投资数量
     */
    private String investmentAmount = "";
    /**
     * 投资状态
     */
    private String investmentStatus = "";
    /**
     * 投资哈希
     */
    private String investmentHash = "";
    /**
     * 借款人地址
     */
    private String borrowAddress = "";
    /**
     * 发布前日该资产价格
     */
    private String mortgageAssetsPrice = "";
    /**
     * 发布前日折合ETH数量
     */
    private String priceToEth = "";
    /*
     * 折扣率
     */
    private String discountRate = "";
    /*
     * 手续费
     */
    private String poundage = "";
    /*
     * 实际到账
     */
    private String actualAmount = "";
    /*
     * 合约哈希地址
     */
    private String contractHash = "";
    /*
     * 到期本息
     */
    private String principalAndInterest = "";

    /*
     * 投资人信息
     */
    private ContractInvestResponse investment;

    public int getContractId() {
        return contractId;
    }

    public void setContractId(int contractId) {
        this.contractId = contractId;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(String contractStatus) {
        this.contractStatus = contractStatus;
    }

    public String getMortgageAssetsType() {
        return mortgageAssetsType;
    }

    public void setMortgageAssetsType(String mortgageAssetsType) {
        this.mortgageAssetsType = mortgageAssetsType;
    }

    public String getMortgageAssetsAmount() {
        return mortgageAssetsAmount;
    }

    public void setMortgageAssetsAmount(String mortgageAssetsAmount) {
        this.mortgageAssetsAmount = mortgageAssetsAmount;
    }

    public String getBorrowAssetsType() {
        return borrowAssetsType;
    }

    public void setBorrowAssetsType(String borrowAssetsType) {
        this.borrowAssetsType = borrowAssetsType;
    }

    public String getBorrowAssetsAmount() {
        return borrowAssetsAmount;
    }

    public void setBorrowAssetsAmount(String borrowAssetsAmount) {
        this.borrowAssetsAmount = borrowAssetsAmount;
    }

    public int getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(int interestRate) {
        this.interestRate = interestRate;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public String getContractCreateDate() {
        return contractCreateDate;
    }

    public void setContractCreateDate(String contractCreateDate) {
        this.contractCreateDate = contractCreateDate;
    }

    public String getContractEffectDate() {
        return contractEffectDate;
    }

    public void setContractEffectDate(String contractEffectDate) {
        this.contractEffectDate = contractEffectDate;
    }

    public String getContractExpireDate() {
        return contractExpireDate;
    }

    public void setContractExpireDate(String contractExpireDate) {
        this.contractExpireDate = contractExpireDate;
    }

    public String getContractRepaymentDate() {
        return contractRepaymentDate;
    }

    public void setContractRepaymentDate(String contractRepaymentDate) {
        this.contractRepaymentDate = contractRepaymentDate;
    }

    public String getContractTakebackDate() {
        return contractTakebackDate;
    }

    public void setContractTakebackDate(String contractTakebackDate) {
        this.contractTakebackDate = contractTakebackDate;
    }

    public String getContractTerminationDate() {
        return contractTerminationDate;
    }

    public void setContractTerminationDate(String contractTerminationDate) {
        this.contractTerminationDate = contractTerminationDate;
    }

    public String getContractDissolutionDate() {
        return contractDissolutionDate;
    }

    public void setContractDissolutionDate(String contractDissolutionDate) {
        this.contractDissolutionDate = contractDissolutionDate;
    }

    public String getInvestmentAddress() {
        return investmentAddress;
    }

    public void setInvestmentAddress(String investmentAddress) {
        this.investmentAddress = investmentAddress;
    }

    public String getInvestmentAmount() {
        return investmentAmount;
    }

    public void setInvestmentAmount(String investmentAmount) {
        this.investmentAmount = investmentAmount;
    }

    public String getInvestmentStatus() {
        return investmentStatus;
    }

    public void setInvestmentStatus(String investmentStatus) {
        this.investmentStatus = investmentStatus;
    }

    public String getInvestmentHash() {
        return investmentHash;
    }

    public void setInvestmentHash(String investmentHash) {
        this.investmentHash = investmentHash;
    }

    public String getBorrowAddress() {
        return borrowAddress;
    }

    public void setBorrowAddress(String borrowAddress) {
        this.borrowAddress = borrowAddress;
    }

    public String getMortgageAssetsPrice() {
        return mortgageAssetsPrice;
    }

    public void setMortgageAssetsPrice(String mortgageAssetsPrice) {
        this.mortgageAssetsPrice = mortgageAssetsPrice;
    }

    public String getPriceToEth() {
        return priceToEth;
    }

    public void setPriceToEth(String priceToEth) {
        this.priceToEth = priceToEth;
    }

    public String getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(String discountRate) {
        this.discountRate = discountRate;
    }

    public String getPoundage() {
        return poundage;
    }

    public void setPoundage(String poundage) {
        this.poundage = poundage;
    }

    public String getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(String actualAmount) {
        this.actualAmount = actualAmount;
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

    public ContractInvestResponse getInvestment() {
        return investment;
    }

    public void setInvestment(ContractInvestResponse investment) {
        this.investment = investment;
    }

    @Override
    public String toString() {
        return "ContractResponse{" +
                "contractId=" + contractId +
                ", contractAddress='" + contractAddress + '\'' +
                ", contractStatus='" + contractStatus + '\'' +
                ", mortgageAssetsType='" + mortgageAssetsType + '\'' +
                ", mortgageAssetsAmount='" + mortgageAssetsAmount + '\'' +
                ", borrowAssetsType='" + borrowAssetsType + '\'' +
                ", borrowAssetsAmount='" + borrowAssetsAmount + '\'' +
                ", interestRate=" + interestRate +
                ", timeLimit=" + timeLimit +
                ", contractCreateDate='" + contractCreateDate + '\'' +
                ", contractEffectDate='" + contractEffectDate + '\'' +
                ", contractExpireDate='" + contractExpireDate + '\'' +
                ", contractRepaymentDate='" + contractRepaymentDate + '\'' +
                ", contractTakebackDate='" + contractTakebackDate + '\'' +
                ", contractTerminationDate='" + contractTerminationDate + '\'' +
                ", contractDissolutionDate='" + contractDissolutionDate + '\'' +
                ", investmentAddress='" + investmentAddress + '\'' +
                ", investmentAmount='" + investmentAmount + '\'' +
                ", investmentStatus='" + investmentStatus + '\'' +
                ", investmentHash='" + investmentHash + '\'' +
                ", borrowAddress='" + borrowAddress + '\'' +
                ", mortgageAssetsPrice='" + mortgageAssetsPrice + '\'' +
                ", priceToEth='" + priceToEth + '\'' +
                ", discountRate='" + discountRate + '\'' +
                ", poundage='" + poundage + '\'' +
                ", actualAmount='" + actualAmount + '\'' +
                ", contractHash='" + contractHash + '\'' +
                ", principalAndInterest='" + principalAndInterest + '\'' +
                ", investment=" + investment +
                '}';
    }
}