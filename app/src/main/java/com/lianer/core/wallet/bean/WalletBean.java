package com.lianer.core.wallet.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lianer.core.app.Constants;

import org.web3j.crypto.WalletFile;

import java.io.Serializable;

public class WalletBean implements Serializable{

    public WalletFile walletFile;

    @JsonIgnore
    public boolean isCurrent = false;

    public WalletBean() {

    }

    public WalletBean(WalletFile walletFile) {
        this.walletFile = walletFile;
    }

    public String getAddress(){
        return Constants.PREFIX_16 + this.walletFile.getAddress();
    }



}
