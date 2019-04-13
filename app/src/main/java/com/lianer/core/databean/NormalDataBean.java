package com.lianer.core.databean;

import java.math.BigInteger;
import java.util.List;

public class NormalDataBean {

    /**
     * code : 200
     * msg : 请求成功
     * data : [2]
     */

    private String code;
    private String msg;
    private List<String> data;

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

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
