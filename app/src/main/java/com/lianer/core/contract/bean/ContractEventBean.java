package com.lianer.core.contract.bean;

/**
 * 合约管理的事件传递类
 *
 * @author allison
 */
public class ContractEventBean {

    public static final int ADD_CONTRACT = 0;
    public static final int UPDATE_CONTRACT = 1;
    ContractBean contractBean;

    /**
     * 事件类型 0：增加数据 1：更新数据
     */
    int eventType;

    public ContractEventBean(ContractBean contractBean, int eventType) {
        this.contractBean = contractBean;
        this.eventType = eventType;
    }

    public ContractBean getContractBean() {
        return contractBean;
    }

    public void setContractBean(ContractBean contractBean) {
        this.contractBean = contractBean;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }
}
