package com.lianer.core.market.bean;

/**
 * 抵押资产实体类
 *
 * @author allison
 */
public class MortgagedfAssetsBean {

    int id;

    /**
     * 代币地址
     */
    String tokenAddress;

    /**
     * 代币简称
     */
    String tokenAbbreviation;

    /**
     * 市场id
     */
    int marketId;

    /**
     * 代币编号
     */
    int tokenNum;

    /**
     * 代币选中状态
     */
    boolean isSelected;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTokenAddress() {
        return tokenAddress;
    }

    public void setTokenAddress(String tokenAddress) {
        this.tokenAddress = tokenAddress;
    }

    public String getTokenAbbreviation() {
        return tokenAbbreviation;
    }

    public void setTokenAbbreviation(String tokenAbbreviation) {
        this.tokenAbbreviation = tokenAbbreviation;
    }

    public int getMarketId() {
        return marketId;
    }

    public void setMarketId(int marketId) {
        this.marketId = marketId;
    }

    public int getTokenNum() {
        return tokenNum;
    }

    public void setTokenNum(int tokenNum) {
        this.tokenNum = tokenNum;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
