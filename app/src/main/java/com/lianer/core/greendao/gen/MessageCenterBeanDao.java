package com.lianer.core.greendao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.lianer.core.contract.bean.MessageCenterBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "MESSAGE_CENTER_BEAN".
*/
public class MessageCenterBeanDao extends AbstractDao<MessageCenterBean, Long> {

    public static final String TABLENAME = "MESSAGE_CENTER_BEAN";

    /**
     * Properties of entity MessageCenterBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ContractId = new Property(1, Long.class, "contractId", false, "CONTRACT_ID");
        public final static Property TxCreateTime = new Property(2, String.class, "txCreateTime", false, "TX_CREATE_TIME");
        public final static Property TxType = new Property(3, int.class, "txType", false, "TX_TYPE");
        public final static Property TxStatusValue = new Property(4, int.class, "txStatusValue", false, "TX_STATUS_VALUE");
        public final static Property PackingStatus = new Property(5, int.class, "packingStatus", false, "PACKING_STATUS");
        public final static Property TxHash = new Property(6, String.class, "txHash", false, "TX_HASH");
        public final static Property TransferAmount = new Property(7, String.class, "transferAmount", false, "TRANSFER_AMOUNT");
        public final static Property TokenType = new Property(8, String.class, "tokenType", false, "TOKEN_TYPE");
        public final static Property TargetAddress = new Property(9, String.class, "targetAddress", false, "TARGET_ADDRESS");
        public final static Property IsPublish = new Property(10, boolean.class, "isPublish", false, "IS_PUBLISH");
        public final static Property ContractAddress = new Property(11, String.class, "contractAddress", false, "CONTRACT_ADDRESS");
    }


    public MessageCenterBeanDao(DaoConfig config) {
        super(config);
    }
    
    public MessageCenterBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"MESSAGE_CENTER_BEAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"CONTRACT_ID\" INTEGER," + // 1: contractId
                "\"TX_CREATE_TIME\" TEXT," + // 2: txCreateTime
                "\"TX_TYPE\" INTEGER NOT NULL ," + // 3: txType
                "\"TX_STATUS_VALUE\" INTEGER NOT NULL ," + // 4: txStatusValue
                "\"PACKING_STATUS\" INTEGER NOT NULL ," + // 5: packingStatus
                "\"TX_HASH\" TEXT," + // 6: txHash
                "\"TRANSFER_AMOUNT\" TEXT," + // 7: transferAmount
                "\"TOKEN_TYPE\" TEXT," + // 8: tokenType
                "\"TARGET_ADDRESS\" TEXT," + // 9: targetAddress
                "\"IS_PUBLISH\" INTEGER NOT NULL ," + // 10: isPublish
                "\"CONTRACT_ADDRESS\" TEXT);"); // 11: contractAddress
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"MESSAGE_CENTER_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, MessageCenterBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long contractId = entity.getContractId();
        if (contractId != null) {
            stmt.bindLong(2, contractId);
        }
 
        String txCreateTime = entity.getTxCreateTime();
        if (txCreateTime != null) {
            stmt.bindString(3, txCreateTime);
        }
        stmt.bindLong(4, entity.getTxType());
        stmt.bindLong(5, entity.getTxStatusValue());
        stmt.bindLong(6, entity.getPackingStatus());
 
        String txHash = entity.getTxHash();
        if (txHash != null) {
            stmt.bindString(7, txHash);
        }
 
        String transferAmount = entity.getTransferAmount();
        if (transferAmount != null) {
            stmt.bindString(8, transferAmount);
        }
 
        String tokenType = entity.getTokenType();
        if (tokenType != null) {
            stmt.bindString(9, tokenType);
        }
 
        String targetAddress = entity.getTargetAddress();
        if (targetAddress != null) {
            stmt.bindString(10, targetAddress);
        }
        stmt.bindLong(11, entity.getIsPublish() ? 1L: 0L);
 
        String contractAddress = entity.getContractAddress();
        if (contractAddress != null) {
            stmt.bindString(12, contractAddress);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, MessageCenterBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long contractId = entity.getContractId();
        if (contractId != null) {
            stmt.bindLong(2, contractId);
        }
 
        String txCreateTime = entity.getTxCreateTime();
        if (txCreateTime != null) {
            stmt.bindString(3, txCreateTime);
        }
        stmt.bindLong(4, entity.getTxType());
        stmt.bindLong(5, entity.getTxStatusValue());
        stmt.bindLong(6, entity.getPackingStatus());
 
        String txHash = entity.getTxHash();
        if (txHash != null) {
            stmt.bindString(7, txHash);
        }
 
        String transferAmount = entity.getTransferAmount();
        if (transferAmount != null) {
            stmt.bindString(8, transferAmount);
        }
 
        String tokenType = entity.getTokenType();
        if (tokenType != null) {
            stmt.bindString(9, tokenType);
        }
 
        String targetAddress = entity.getTargetAddress();
        if (targetAddress != null) {
            stmt.bindString(10, targetAddress);
        }
        stmt.bindLong(11, entity.getIsPublish() ? 1L: 0L);
 
        String contractAddress = entity.getContractAddress();
        if (contractAddress != null) {
            stmt.bindString(12, contractAddress);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public MessageCenterBean readEntity(Cursor cursor, int offset) {
        MessageCenterBean entity = new MessageCenterBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // contractId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // txCreateTime
            cursor.getInt(offset + 3), // txType
            cursor.getInt(offset + 4), // txStatusValue
            cursor.getInt(offset + 5), // packingStatus
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // txHash
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // transferAmount
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // tokenType
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // targetAddress
            cursor.getShort(offset + 10) != 0, // isPublish
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11) // contractAddress
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, MessageCenterBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setContractId(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setTxCreateTime(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setTxType(cursor.getInt(offset + 3));
        entity.setTxStatusValue(cursor.getInt(offset + 4));
        entity.setPackingStatus(cursor.getInt(offset + 5));
        entity.setTxHash(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setTransferAmount(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setTokenType(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setTargetAddress(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setIsPublish(cursor.getShort(offset + 10) != 0);
        entity.setContractAddress(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(MessageCenterBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(MessageCenterBean entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(MessageCenterBean entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
