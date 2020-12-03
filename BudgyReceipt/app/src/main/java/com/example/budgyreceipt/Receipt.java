package com.example.budgyreceipt;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Receipt {

    // id will be auto generated
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;

    @NonNull
    @ColumnInfo(name = "merchant")
    private String mMerchant;

    @NonNull
    @ColumnInfo(name = "total")
    private String mTotal;

    // adding updated time to possibly make changes to receipts easier to track in the long-term
    @ColumnInfo(name = "updated")
    private long mUpdateTime;

    public Receipt() { // empty constructor
        mMerchant = "Merchant";
        mTotal = "0.00";
        mUpdateTime = System.currentTimeMillis();
    }

    public Receipt(@NonNull String merchant) {
        mMerchant = merchant;
        mTotal = "0.00";
        mUpdateTime = System.currentTimeMillis();
    }

    public long getId() { return mId; }

    public void setId(long id) { mId = id; }

    public String getMerchant() { return mMerchant; }

    public void setMerchant(String merchant) { mMerchant = merchant; }

    public String getTotal() { return mTotal; }

    public void setTotal(String total) { mTotal = total; }

    public long getUpdateTime() {
        return mUpdateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.mUpdateTime = updateTime;
    }


    @NonNull
    @Override
    public String toString() {
        return "Receipt{" +
                "mId=" + mId +
                ", mMerchant=" + mMerchant +
                ", mTotal=" + mTotal +
                ", mUpdateTime=" + mUpdateTime + "}";
    }

}
