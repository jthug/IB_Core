package com.lianer.core.wallet.bean;
import java.util.List;

/**
 * 交易记录响应数据实体类
 * @author allison
 */
public class TxRecordReponseBean {

    private String status;
    private String message;
    private List<TxRecordBean> result;
    public void setStatus(String status) {
         this.status = status;
     }
     public String getStatus() {
         return status;
     }

    public void setMessage(String message) {
         this.message = message;
     }
     public String getMessage() {
         return message;
     }

    public void setResult(List<TxRecordBean> result) {
         this.result = result;
     }
     public List<TxRecordBean> getResult() {
         return result;
     }

}