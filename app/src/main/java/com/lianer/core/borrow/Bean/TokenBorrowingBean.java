package com.lianer.core.borrow.Bean;

/**
 * 资产-币种信息
 *
 * @author allison
 */
public class TokenBorrowingBean {
    //代币名称
    private String tokenName;

    //代币简称
    private String tokenSymbol;

    //代币介绍
    private String projectInfo;

    //代币地址
    private String tokenAddress;

    //图片logo地址
    private String tokenLogo;

    //代币信息网站
    private String tokenSite;

    //代币类型
    private String tokenType;

    //折扣率
    private String discountRate;

    //均价
    private String exchangeRate;

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public String getTokenSymbol() {
        return tokenSymbol;
    }

    public void setTokenSymbol(String tokenSymbol) {
        this.tokenSymbol = tokenSymbol;
    }

    public String getProjectInfo() {
        return projectInfo;
    }

    public void setProjectInfo(String projectInfo) {
        this.projectInfo = projectInfo;
    }

    public String getTokenAddress() {
        return tokenAddress;
    }

    public void setTokenAddress(String tokenAddress) {
        this.tokenAddress = tokenAddress;
    }

    public String getTokenLogo() {
        return tokenLogo;
    }

    public void setTokenLogo(String tokenLogo) {
        this.tokenLogo = tokenLogo;
    }

    public String getTokenSite() {
        return tokenSite;
    }

    public void setTokenSite(String tokenSite) {
        this.tokenSite = tokenSite;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(String discountRate) {
        this.discountRate = discountRate;
    }

    public String getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(String exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public TokenBorrowingBean(String tokenName, String tokenSymbol, String projectInfo, String tokenAddress, String tokenLogo, String tokenSite, String tokenType, String discountRate, String exchangeRate) {
        this.tokenName = tokenName;
        this.tokenSymbol = tokenSymbol;
        this.projectInfo = projectInfo;
        this.tokenAddress = tokenAddress;
        this.tokenLogo = tokenLogo;
        this.tokenSite = tokenSite;
        this.tokenType = tokenType;
        this.discountRate = discountRate;
        this.exchangeRate = exchangeRate;
    }

    @Override
    public String toString() {
        return "TokenBorrowingBean{" +
                "tokenName='" + tokenName + '\'' +
                ", tokenSymbol='" + tokenSymbol + '\'' +
                ", projectInfo='" + projectInfo + '\'' +
                ", tokenAddress='" + tokenAddress + '\'' +
                ", tokenLogo='" + tokenLogo + '\'' +
                ", tokenSite='" + tokenSite + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", discountRate=" + discountRate +
                ", exchangeRate=" + exchangeRate +
                '}';
    }
}