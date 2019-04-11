package com.lianer.core.wallet.bean;

/**
 * Author by JT
 * Create at 2019/3/1 10:28
 */
public class WalletListBean {
    private String icon;
    private String walletName;
    private String walletAddress;
    private boolean isSelect = false;

    public WalletListBean(boolean isSelect) {
        this.isSelect = isSelect;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }
}
