package com.lianer.core.market.bean;


public class TransactionRecordResponse {

    private String  amount;
    private String  transactionValueTUSD;
    private String  transactionValueETH  ;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTransactionValueTUSD() {
        return transactionValueTUSD;
    }

    public void setTransactionValueTUSD(String transactionValueTUSD) {
        this.transactionValueTUSD = transactionValueTUSD;
    }

    public String getTransactionValueETH() {
        return transactionValueETH;
    }

    public void setTransactionValueETH(String transactionValueETH) {
        this.transactionValueETH = transactionValueETH;
    }
}
