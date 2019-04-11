package com.lianer.core.contract.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 消息中心item实体类
 */
@Entity
public class MessageCenterBean {

    @Id(autoincrement = true)
    private Long id;

    Long contractId;

    /**
     * 交易生成时间
     */
    String txCreateTime;

    /**
     * 交易类型 0：转账交易 1：合约交易
     */
    int txType;

    /**
     * 交易状态值 0：转出 1：部署合约 2：转入抵押 3：取回抵押 4：投资合约 5：还款 6：获取抵押 7:存入 8取出
     */
    int txStatusValue;

    /**
     * 打包状态 0：打包中 1：打包成功 2：打包失败
     */
    int packingStatus;

    /**
     * 交易hash
     */
    String txHash;

    /**
     * 转账金额
     */
    String transferAmount;

    /**
     * 转账token类型
     */
    String tokenType;

    /**
     * 转账接收钱包地址
     */
    String targetAddress;

    /**
     * 是否发布到市场
     */
    boolean isPublish;

    /**
     * 合约地址
     */
    String contractAddress;







    @Generated(hash = 1651071882)
    public MessageCenterBean(Long id, Long contractId, String txCreateTime,
            int txType, int txStatusValue, int packingStatus, String txHash,
            String transferAmount, String tokenType, String targetAddress,
            boolean isPublish, String contractAddress) {
        this.id = id;
        this.contractId = contractId;
        this.txCreateTime = txCreateTime;
        this.txType = txType;
        this.txStatusValue = txStatusValue;
        this.packingStatus = packingStatus;
        this.txHash = txHash;
        this.transferAmount = transferAmount;
        this.tokenType = tokenType;
        this.targetAddress = targetAddress;
        this.isPublish = isPublish;
        this.contractAddress = contractAddress;
    }

    @Generated(hash = 1640666476)
    public MessageCenterBean() {
    }







    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getTxCreateTime() {
        return txCreateTime;
    }

    public void setTxCreateTime(String txCreateTime) {
        this.txCreateTime = txCreateTime;
    }

    public int getTxType() {
        return txType;
    }

    public void setTxType(int txType) {
        this.txType = txType;
    }

    public int getTxStatusValue() {
        return txStatusValue;
    }

    public void setTxStatusValue(int txStatusValue) {
        this.txStatusValue = txStatusValue;
    }

    public int getPackingStatus() {
        return packingStatus;
    }

    public void setPackingStatus(int packingStatus) {
        this.packingStatus = packingStatus;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "MessageCenterBean{" +
                "id=" + id +
                ", contractId='" + contractId + '\'' +
                ", txCreateTime='" + txCreateTime + '\'' +
                ", txType=" + txType +
                ", packingStatus=" + packingStatus +
                ", txHash='" + txHash + '\'' +
                '}';
    }

    public String getTransferAmount() {
        return this.transferAmount;
    }

    public void setTransferAmount(String transferAmount) {
        this.transferAmount = transferAmount;
    }

    public String getTokenType() {
        return this.tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getTargetAddress() {
        return this.targetAddress;
    }

    public void setTargetAddress(String targetAddress) {
        this.targetAddress = targetAddress;
    }

    public boolean getIsPublish() {
        return this.isPublish;
    }

    public void setIsPublish(boolean isPublish) {
        this.isPublish = isPublish;
    }

    public String getContractAddress() {
        return this.contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }
}
