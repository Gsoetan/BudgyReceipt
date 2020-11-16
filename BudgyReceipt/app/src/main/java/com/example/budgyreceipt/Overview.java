package com.example.budgyreceipt;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = Receipt.class, parentColumns = "id", childColumns = "receiptId", onDelete = CASCADE))
public class Overview {
    // id will be auto generated
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;

    @NonNull
    @ColumnInfo(name = "receiptId")
    private long mReceiptId;

    // will need to add a query to get the merchant name from the receipt class/table

    @NonNull
    @ColumnInfo(name = "date")
    private String mDate;

    @NonNull
    @ColumnInfo(name = "total")
    private String mTotal;

    @NonNull
    @ColumnInfo(name = "subtotal")
    private String mSubtotal;

    @NonNull
    @ColumnInfo(name = "tag")
    private String mTag;

    @NonNull
    @ColumnInfo(name = "payment")
    private String mPayment;

    //photo bitmap to come later

    public long getId() { return mId; }

    public void setId(long id) { mId = id; }

    public long getReceiptId() { return mReceiptId; }

    public void setReceiptId(long receiptId) { mReceiptId = receiptId; }

    public String getDate() { return mDate; }

    public void setDate(String date) { mDate = date; }

    public String getTotal() { return mTotal; }

    public void setTotal(String total) { mTotal = total; }

    public String getSubtotal() { return mSubtotal; }

    public void setSubtotal(String subtotal) { mSubtotal = subtotal; }

    public String getTag() { return mTag; }

    public void setTag(String tag) { mTag = tag; }

    public String getPayment() { return mPayment; }

    public void setPayment(String payment) { mPayment = payment; }

    @NonNull
    @Override
    public String toString() {
        return "Overview{" +
                "mId=" + mId +
                ", mReceiptId=" + mReceiptId +
                ", mDate=" + mDate +
                ", mTotal=" + mTotal +
                ", mSubtotal=" + mSubtotal +
                ", mTag=" + mTag +
                ", mPayment=" + mPayment + "}";
    }

}
