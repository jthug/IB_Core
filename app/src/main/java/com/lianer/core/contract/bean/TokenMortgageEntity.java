package com.lianer.core.contract.bean;

import java.util.List;

public class TokenMortgageEntity {

    private List<TokenMortgageBean> data;
    private String msg;
    private String code;

    public List<TokenMortgageBean> getData() {
        return data;
    }

    public void setData(List<TokenMortgageBean> data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
