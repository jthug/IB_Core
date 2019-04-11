package com.lianer.core.SmartContract;

import org.web3j.utils.Convert;

import java.math.BigInteger;


public class BaseContract {


    public BigInteger getGWei(String value) throws Exception {
        return Convert.toWei(value, Convert.Unit.GWEI).toBigInteger();
    }

    //EthValue
    public BigInteger getEth(String value) throws Exception {
        return Convert.toWei(value, Convert.Unit.ETHER).toBigInteger();
    }
}
