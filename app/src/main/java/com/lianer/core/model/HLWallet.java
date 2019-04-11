package com.lianer.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lianer.core.config.Constant;


import org.web3j.crypto.WalletFile;

public class HLWallet {

    public WalletFile walletFile;
    private String walletName = "ETH wallet";

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    @JsonIgnore
    public boolean isCurrent = false;

    public HLWallet() {

    }

    public HLWallet(WalletFile walletFile) {
        this.walletFile = walletFile;
    }

    public String getAddress(){
        return Constant.PREFIX_16 + this.walletFile.getAddress();
    }

    public WalletFile getWalletFile() {
        return walletFile;
    }

    public void setWalletFile(WalletFile walletFile) {
        this.walletFile = walletFile;
    }
}
