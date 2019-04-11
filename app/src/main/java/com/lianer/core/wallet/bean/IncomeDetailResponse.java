package com.lianer.core.wallet.bean;

import java.util.List;

public class IncomeDetailResponse {

    private List<IncomeDetailBean> data;
    private String code;
    private String msg;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<IncomeDetailBean> getData() {
        return data;
    }

    public void setData(List<IncomeDetailBean> data) {
        this.data = data;
    }

}