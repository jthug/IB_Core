package com.lianer.core.wallet.bean;

import android.widget.TextView;

public class WalletAssetsBean {

    private TextView textView;
    private String balance;

    public WalletAssetsBean() {
    }

    public WalletAssetsBean(TextView textView, String balance) {
        this.textView = textView;
        this.balance = balance;
    }

    public TextView getTextView() {
        return textView;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
