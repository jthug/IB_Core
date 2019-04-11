package com.lianer.core.market.bean;

/**
 * 市场合约实体类
 *
 * @author allison
 */
public class MarketContractBean {

    private String tokenAddress;
    private String amount;
    private String cycle;
    private String interest;
    private String mortgage;
    private String tokenValue;
    private String address;
    private String borrowAddress;
    private String lenderToken;
    private int mortgageRate;

    public String getTokenAddress() {
        return tokenAddress;
    }

    public void setTokenAddress(String tokenAddress) {
        this.tokenAddress = tokenAddress;
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

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getMortgageRate() {
        return mortgageRate;
    }

    public void setMortgageRate(int mortgageRate) {
        this.mortgageRate = mortgageRate;
    }

    public String getBorrowAddress() {
        return borrowAddress;
    }

    public void setBorrowAddress(String borrowAddress) {
        this.borrowAddress = borrowAddress;
    }

    public String getLenderToken() {
        return lenderToken;
    }

    public void setLenderToken(String lenderToken) {
        this.lenderToken = lenderToken;
    }

    @Override
    public String toString() {
        return "MarketContractBean{" +
                "tokenAddress='" + tokenAddress + '\'' +
                ", amount='" + amount + '\'' +
                ", cycle='" + cycle + '\'' +
                ", interest='" + interest + '\'' +
                ", mortgage='" + mortgage + '\'' +
                ", tokenValue='" + tokenValue + '\'' +
                ", address='" + address + '\'' +
                ", borrowAddress='" + borrowAddress + '\'' +
                ", lenderToken='" + lenderToken + '\'' +
                ", mortgageRate=" + mortgageRate +
                '}';
    }
}
