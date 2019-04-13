package com.lianer.core.databean;

import java.util.List;

public class InfoDataBean {

    /**
     * code : 200
     * data : [{"gas":"2000000","blockHash":"0x3deb8faccb09eb38f02df65ef49e277601077690ea8e1e52f93151d20299a703","value":"0","to":"0xe7a973c0f3259b54065f1648d6fa3e093351951c","from":"0x6ee2049926af3a5275febefc1896612641738067","gasPrice":"18000000000","blockNumber":"2201828","nonce":"32"}]
     * msg : 请求成功
     */

    private String code;
    private String msg;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * gas : 2000000
         * blockHash : 0x3deb8faccb09eb38f02df65ef49e277601077690ea8e1e52f93151d20299a703
         * value : 0
         * to : 0xe7a973c0f3259b54065f1648d6fa3e093351951c
         * from : 0x6ee2049926af3a5275febefc1896612641738067
         * gasPrice : 18000000000
         * blockNumber : 2201828
         * nonce : 32
         */

        private String gas;
        private String blockHash;
        private String value;
        private String to;
        private String from;
        private String gasPrice;
        private String blockNumber;
        private String nonce;

        public String getGas() {
            return gas;
        }

        public void setGas(String gas) {
            this.gas = gas;
        }

        public String getBlockHash() {
            return blockHash;
        }

        public void setBlockHash(String blockHash) {
            this.blockHash = blockHash;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getGasPrice() {
            return gasPrice;
        }

        public void setGasPrice(String gasPrice) {
            this.gasPrice = gasPrice;
        }

        public String getBlockNumber() {
            return blockNumber;
        }

        public void setBlockNumber(String blockNumber) {
            this.blockNumber = blockNumber;
        }

        public String getNonce() {
            return nonce;
        }

        public void setNonce(String nonce) {
            this.nonce = nonce;
        }
    }
}
