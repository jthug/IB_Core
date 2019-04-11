package com.lianer.core.contract.bean;

/**
 * 资产-币种信息
 */
public class TokenMortgageBean {
    //代币地址
    private String tokenAddress;
    //代币icon
    private String tokenImage;
    //代币类型
    private String tokenNum;
    //均价
    private String tokenPrice;

    private int id;
    //代币缩写
    private String tokenAbbreviation;
    //折扣率
    private String tokenDiscountRate;

    public String getTokenAddress() {
        return tokenAddress;
    }

    public void setTokenAddress(String tokenAddress) {
        this.tokenAddress = tokenAddress;
    }

    public String getTokenImage() {
        return tokenImage;
    }

    public void setTokenImage(String tokenImage) {
        this.tokenImage = tokenImage;
    }

    public String getTokenNum() {
        return tokenNum;
    }

    public void setTokenNum(String tokenNum) {
        this.tokenNum = tokenNum;
    }

    public String getTokenPrice() {
        return tokenPrice;
    }

    public void setTokenPrice(String tokenPrice) {
        this.tokenPrice = tokenPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTokenAbbreviation() {
        return tokenAbbreviation;
    }

    public void setTokenAbbreviation(String tokenAbbreviation) {
        this.tokenAbbreviation = tokenAbbreviation;
    }

    public String getTokenDiscountRate() {
        return tokenDiscountRate;
    }

    public void setTokenDiscountRate(String tokenDiscountRate) {
        this.tokenDiscountRate = tokenDiscountRate;
    }

    public TokenMortgageBean(String tokenAddress, String tokenImage, String tokenNum, String tokenPrice, int id, String tokenAbbreviation, String tokenDiscountRate) {
        this.tokenAddress = tokenAddress;
        this.tokenImage = tokenImage;
        this.tokenNum = tokenNum;
        this.tokenPrice = tokenPrice;
        this.id = id;
        this.tokenAbbreviation = tokenAbbreviation;
        this.tokenDiscountRate = tokenDiscountRate;
    }

    @Override
    public String toString() {
        return "TokenMortgageBean{" +
                "tokenAddress='" + tokenAddress + '\'' +
                ", tokenImage='" + tokenImage + '\'' +
                ", tokenNum='" + tokenNum + '\'' +
                ", tokenPrice='" + tokenPrice + '\'' +
                ", id=" + id +
                ", tokenAbbreviation='" + tokenAbbreviation + '\'' +
                ", tokenDiscountRate='" + tokenDiscountRate + '\'' +
                '}';
    }
}