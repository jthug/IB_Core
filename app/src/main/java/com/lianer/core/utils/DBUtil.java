package com.lianer.core.utils;

import com.lianer.common.utils.KLog;
import com.lianer.core.app.NestApp;
import com.lianer.core.contract.bean.ContractBean;
import com.lianer.core.contract.bean.MessageCenterBean;
import com.lianer.core.greendao.gen.ContractBeanDao;
import com.lianer.core.greendao.gen.DaoSession;
import com.lianer.core.greendao.gen.MessageCenterBeanDao;
import org.greenrobot.greendao.query.Query;
import java.util.List;

/**
 * 数据库操作类
 */
public class DBUtil {

    /**
     * 新增消息数据
     *
     * @param hash          交易hash
     * @param txType        交易类型
     * @param txStatusValue 交易状态值
     */
    public static void insertMessageToDB(Long contractId,String hash, int txType, int txStatusValue,String transferAmount,String tokenType,String targetAddress,boolean isPublish,String contractAddress) {
        MessageCenterBean messageCenterBean = new MessageCenterBean();
        messageCenterBean.setContractId(contractId);
        messageCenterBean.setTxCreateTime(String.valueOf(System.currentTimeMillis()/1000));
        messageCenterBean.setTxHash(hash);
        messageCenterBean.setPackingStatus(0);
        messageCenterBean.setTxType(txType);
        messageCenterBean.setTxStatusValue(txStatusValue);
        messageCenterBean.setTransferAmount(transferAmount);
        messageCenterBean.setTokenType(tokenType);
        messageCenterBean.setTargetAddress(targetAddress);
        messageCenterBean.setIsPublish(isPublish);
        messageCenterBean.setContractAddress(contractAddress);
        DBUtil.insert(messageCenterBean);
    }

    /**
     //删除操作
     MessageCenterBean messageCenterBean = new MessageCenterBean();
     messageCenterBean.setId(2L);
     DBUtil.delete(messageCenterBean);
     */

    /**
     * 插入数据
     */
    public static void insert(MessageCenterBean messageCenterBean) {
        DaoSession daoSession = ((NestApp) NestApp.getInstance()).getDaoSession();
        MessageCenterBeanDao messageCenterBeanDao = daoSession.getMessageCenterBeanDao();
        Long isInsertSuccess = messageCenterBeanDao.insert(messageCenterBean);
        KLog.i("是否增加成功: " + isInsertSuccess);
        KLog.i("Inserted new message, ID: " + messageCenterBean.getId());

//        queryMessageAll();
    }

    /**
     * msg删除数据
     */
    public static void delete(MessageCenterBean messageCenterBean) {
        DaoSession daoSession = ((NestApp) NestApp.getInstance()).getDaoSession();
        MessageCenterBeanDao messageCenterBeanDao = daoSession.getMessageCenterBeanDao();
        messageCenterBeanDao.delete(messageCenterBean);
        KLog.i("Delete a message, ID: " + messageCenterBean.getId());

//        queryMessageAll();
    }

    /**
     * msg更新数据
     */
    public static void update(MessageCenterBean messageCenterBean) {
        DaoSession daoSession = ((NestApp) NestApp.getInstance()).getDaoSession();
        MessageCenterBeanDao messageCenterBeanDao = daoSession.getMessageCenterBeanDao();
        messageCenterBeanDao.update(messageCenterBean);
        KLog.i("Update a message, ID: " + messageCenterBean.getId());

//        queryMessageAll();
    }

    /**
     * msg分页查询数据
     */
    public static List<MessageCenterBean> queryByOffset(int offset) {
        DaoSession daoSession = ((NestApp) NestApp.getInstance()).getDaoSession();
        MessageCenterBeanDao messageCenterBeanDao = daoSession.getMessageCenterBeanDao();
        Query<MessageCenterBean> messageCenterBeanQuery = messageCenterBeanDao.queryBuilder().orderDesc(MessageCenterBeanDao.Properties.TxCreateTime).offset(offset * 20).limit(20).build();
        List<MessageCenterBean> messageCenterBeanList = messageCenterBeanQuery.list();
        for (MessageCenterBean messageCenterBean : messageCenterBeanList) {
            KLog.i("Query message, a message：" + messageCenterBean.toString());
        }

        return messageCenterBeanList;
    }

    /**
     * msg查询所有数据
     */
    public static List<MessageCenterBean> queryMessageAll() {
        DaoSession daoSession = ((NestApp) NestApp.getInstance()).getDaoSession();
        MessageCenterBeanDao messageCenterBeanDao = daoSession.getMessageCenterBeanDao();
        Query<MessageCenterBean> messageCenterBeanQuery = messageCenterBeanDao.queryBuilder().orderDesc(MessageCenterBeanDao.Properties.TxCreateTime).build();
        List<MessageCenterBean> messageCenterBeanList = messageCenterBeanQuery.list();
        for (MessageCenterBean messageCenterBean : messageCenterBeanList) {
            KLog.i("Query message, a message：" + messageCenterBean.toString());
        }

        return messageCenterBeanList;
    }

    /**
     * msg根据hash查询数据
     */
    public static MessageCenterBean queryByTxHash(String txHash) {
        DaoSession daoSession = ((NestApp) NestApp.getInstance()).getDaoSession();
        MessageCenterBeanDao messageCenterBeanDao = daoSession.getMessageCenterBeanDao();
        Query<MessageCenterBean> messageCenterBeanQuery = messageCenterBeanDao.queryBuilder()
                .where(MessageCenterBeanDao.Properties.TxHash.eq(txHash)).build();
        List<MessageCenterBean> messageCenterBeanList = messageCenterBeanQuery.list();
        for (MessageCenterBean messageCenterBean : messageCenterBeanList) {
            KLog.i("Query message, a message：" + messageCenterBean.toString());
        }

        return messageCenterBeanList.get(0);
    }

    /**
     * msg查询打包中数据
     */
    public static List<MessageCenterBean> queryPackingData() {
        DaoSession daoSession = ((NestApp) NestApp.getInstance()).getDaoSession();
        MessageCenterBeanDao messageCenterBeanDao = daoSession.getMessageCenterBeanDao();
        Query<MessageCenterBean> messageCenterBeanQuery = messageCenterBeanDao.queryBuilder()
                .where(MessageCenterBeanDao.Properties.PackingStatus.eq(0))
                .orderDesc(MessageCenterBeanDao.Properties.TxCreateTime).build();
        List<MessageCenterBean> messageCenterBeanList = messageCenterBeanQuery.list();
        for (MessageCenterBean messageCenterBean : messageCenterBeanList) {
            KLog.i("Query message, a message：" + messageCenterBean.toString());
        }

        return messageCenterBeanList;
    }


    /**
     * contract插入数据
     */
    public static Long insert(ContractBean contractBean) {
        DaoSession daoSession = ((NestApp) NestApp.getInstance()).getDaoSession();
        ContractBeanDao contractBeanDao = daoSession.getContractBeanDao();
        Long isInsertSuccess = contractBeanDao.insert(contractBean);
        KLog.i("是否增加成功: " + isInsertSuccess);
        KLog.i("Inserted new message, ID: " + contractBean.getContractId());
//        queryContracts();
        return contractBean.getContractId();
    }

    /**
     *  contract更新数据
     */
    public static void update(ContractBean contractBean) {
        DaoSession daoSession = ((NestApp) NestApp.getInstance()).getDaoSession();
        ContractBeanDao contractBeanDao = daoSession.getContractBeanDao();
        contractBeanDao.update(contractBean);
        KLog.i("update a message, ID: " + contractBean.getContractId());
//        queryContracts();
    }


    /**
     *  contract删除数据
     */
    public static void delete(ContractBean contractBean) {
        DaoSession daoSession = ((NestApp) NestApp.getInstance()).getDaoSession();
        ContractBeanDao contractBeanDao = daoSession.getContractBeanDao();
        contractBeanDao.delete(contractBean);
        KLog.i("delete a message, ID: " + contractBean.getContractId());
//        queryContracts();
    }

    /**
     * contract查询数据
     */
    public static List<ContractBean> queryContracts() {
        DaoSession daoSession = ((NestApp) NestApp.getInstance()).getDaoSession();
        ContractBeanDao contractBeanDao = daoSession.getContractBeanDao();
        Query<ContractBean> contractBeanQuery = contractBeanDao.queryBuilder().orderDesc(ContractBeanDao.Properties.ContractId).build();
        List<ContractBean> contractBeanBeanList = contractBeanQuery.list();
        for (ContractBean contractBean : contractBeanBeanList) {
            KLog.i("Query Contract：" + contractBean.toString());
        }
        return contractBeanBeanList;
    }


    /**
     * contract通过ContractId查询数据
     * @param id
     * @return
     */
    public static ContractBean queryContractById(long id) {
        DaoSession daoSession = ((NestApp) NestApp.getInstance()).getDaoSession();
        ContractBeanDao contractBeanDao = daoSession.getContractBeanDao();
        Query<ContractBean> contractBeanQuery = contractBeanDao.queryBuilder().where(ContractBeanDao.Properties.ContractId.eq(id)).build();
        List<ContractBean> contractBeanBeanList = contractBeanQuery.list();
        for (ContractBean contractBean : contractBeanBeanList) {
            if( contractBean.getContractId() == id){
                return contractBean;
            }
        }
        return null;
    }

    /**
     * contract通过contractAddress查询数据
     * @param contractAddress
     * @return
     */
    public static boolean queryContractByContractAddress(String contractAddress) {
        DaoSession daoSession = ((NestApp) NestApp.getInstance()).getDaoSession();
        ContractBeanDao contractBeanDao = daoSession.getContractBeanDao();
        Query<ContractBean> contractBeanQuery = contractBeanDao.queryBuilder().orderDesc(ContractBeanDao.Properties.ContractId).build();
        List<ContractBean> contractBeanBeanList = contractBeanQuery.list();
        for (ContractBean contractBean : contractBeanBeanList) {
            if(contractBean.getContractAddress().equalsIgnoreCase(contractAddress)){
                return true;
            }
        }

        return false;
    }

    public static ContractBean queryContractByAddress(String contractAddress){
        DaoSession daoSession = ((NestApp) NestApp.getInstance()).getDaoSession();
        ContractBeanDao contractBeanDao = daoSession.getContractBeanDao();
        Query<ContractBean> contractBeanQuery = contractBeanDao.queryBuilder().orderDesc(ContractBeanDao.Properties.ContractId).build();
        List<ContractBean> contractBeanBeanList = contractBeanQuery.list();
        for (ContractBean contractBean : contractBeanBeanList) {
            if(contractBean.getContractAddress().equalsIgnoreCase(contractAddress)){
                return contractBean;
            }
        }

        return null;
    }

    /**
     * contract分页查询数据
     */
    public static List<ContractBean> queryContractsByOffset(int offset) {
        DaoSession daoSession = ((NestApp) NestApp.getInstance()).getDaoSession();
        ContractBeanDao contractBeanDao = daoSession.getContractBeanDao();
        Query<ContractBean> contractBeanQuery = contractBeanDao.queryBuilder().orderDesc(ContractBeanDao.Properties.ContractId).offset(offset * 20).limit(20).build();
        List<ContractBean> contractBeanBeanList = contractBeanQuery.list();
        KLog.i(" Contract：" + contractBeanBeanList.size());
        for (ContractBean contractBean : contractBeanBeanList) {
            KLog.i("Query Contract：" + contractBean.toString());
        }
        return contractBeanBeanList;
    }

}
