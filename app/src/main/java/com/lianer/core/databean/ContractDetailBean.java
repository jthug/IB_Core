package com.lianer.core.databean;

import java.util.List;

public class ContractDetailBean {


    /**
     * code : 200
     * msg : 请求成功
     * data : [{"borrowerAddress":"0x6ee2049926af3a5275febefc1896612641738067","investorAddress":"0x0000000000000000000000000000000000000000","amount":"1000000000000000000","cycle":"86400","interest":"1","mortgage":"25002500000000000000","needMortgage":"25002500000000000000","tokenAddress":"0x27868e4e9190fc7957c5c4c65815d79ac7e4eadc","investmentTime":"0","expire":"1000100000000000000","expiryTime":"0","state":"0","createTime":"1555055485","endTime":"0"}]
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
         * borrowerAddress : 0x6ee2049926af3a5275febefc1896612641738067
         * investorAddress : 0x0000000000000000000000000000000000000000
         * amount : 1000000000000000000
         * cycle : 86400
         * interest : 1
         * mortgage : 25002500000000000000
         * needMortgage : 25002500000000000000
         * tokenAddress : 0x27868e4e9190fc7957c5c4c65815d79ac7e4eadc
         * investmentTime : 0
         * expire : 1000100000000000000
         * expiryTime : 0
         * state : 0
         * createTime : 1555055485
         * endTime : 0
         */

        private String borrowerAddress;
        private String investorAddress;
        private String amount;
        private String cycle;
        private String interest;
        private String mortgage;
        private String needMortgage;
        private String tokenAddress;
        private String investmentTime;
        private String expire;
        private String expiryTime;
        private String state;
        private String createTime;
        private String endTime;

        public String getBorrowerAddress() {
            return borrowerAddress;
        }

        public void setBorrowerAddress(String borrowerAddress) {
            this.borrowerAddress = borrowerAddress;
        }

        public String getInvestorAddress() {
            return investorAddress;
        }

        public void setInvestorAddress(String investorAddress) {
            this.investorAddress = investorAddress;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getCycle() {
            return cycle;
        }

        public void setCycle(String cycle) {
            this.cycle = cycle;
        }

        public String getInterest() {
            return interest;
        }

        public void setInterest(String interest) {
            this.interest = interest;
        }

        public String getMortgage() {
            return mortgage;
        }

        public void setMortgage(String mortgage) {
            this.mortgage = mortgage;
        }

        public String getNeedMortgage() {
            return needMortgage;
        }

        public void setNeedMortgage(String needMortgage) {
            this.needMortgage = needMortgage;
        }

        public String getTokenAddress() {
            return tokenAddress;
        }

        public void setTokenAddress(String tokenAddress) {
            this.tokenAddress = tokenAddress;
        }

        public String getInvestmentTime() {
            return investmentTime;
        }

        public void setInvestmentTime(String investmentTime) {
            this.investmentTime = investmentTime;
        }

        public String getExpire() {
            return expire;
        }

        public void setExpire(String expire) {
            this.expire = expire;
        }

        public String getExpiryTime() {
            return expiryTime;
        }

        public void setExpiryTime(String expiryTime) {
            this.expiryTime = expiryTime;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }
    }
}
