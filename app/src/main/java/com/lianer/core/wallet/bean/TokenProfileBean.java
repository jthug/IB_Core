package com.lianer.core.wallet.bean;

import android.support.annotation.NonNull;

import com.lianer.core.utils.SortUtil;

public class TokenProfileBean implements Comparable<TokenProfileBean>{
    private String symbol;

    private String address;

//    private Overview overview;

    private String email;

    private String website;

    private String whitepaper;

    private String state;

    private String published_on;

    private String decimals;

//    private Initial_price initial_price;

    private Links links;

    private String assets;//资产

    private boolean isChecked;//是否选中

    private String tokenBalance;//余额

    public String getTokenBalance() {
        return tokenBalance;
    }

    public void setTokenBalance(String tokenBalance) {
        this.tokenBalance = tokenBalance;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return this.address;
    }

//    public void setOverview(Overview overview) {
//        this.overview = overview;
//    }
//
//    public Overview getOverview() {
//        return this.overview;
//    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return this.email;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getWebsite() {
        return this.website;
    }

    public void setWhitepaper(String whitepaper) {
        this.whitepaper = whitepaper;
    }

    public String getWhitepaper() {
        return this.whitepaper;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return this.state;
    }

    public void setPublished_on(String published_on) {
        this.published_on = published_on;
    }

    public String getPublished_on() {
        return this.published_on;
    }

    public String getDecimals() {
        return decimals;
    }

    public void setDecimals(String decimals) {
        this.decimals = decimals;
    }

    //    public void setInitial_price(Initial_price initial_price) {
//        this.initial_price = initial_price;
//    }
//
//    public Initial_price getInitial_price() {
//        return this.initial_price;
//    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public Links getLinks() {
        return this.links;
    }

    public String getAssets() {
        return assets;
    }

    public void setAssets(String assets) {
        this.assets = assets;
    }

    @Override
    public int compareTo(@NonNull TokenProfileBean o) {
//        return symbol.compareTo(o.symbol);
        return SortUtil.getInstance().compareString(symbol, o.symbol);
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}