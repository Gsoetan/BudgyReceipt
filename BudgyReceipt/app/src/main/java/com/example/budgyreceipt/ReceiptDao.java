package com.example.budgyreceipt;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ReceiptDao {
    @Query("SELECT * FROM Receipt WHERE id = :id")
    public Receipt getReceipt(long id);

    @Query("SELECT * FROM Receipt ORDER BY merchant")
    public List<Receipt> getReceipts();

    @Query("SELECT * FROM Receipt ORDER BY updated DESC")
    public List<Receipt> getReceiptsNew(); //gets newer first

    @Query("SELECT * FROM Receipt ORDER BY updated ASC")
    public List<Receipt> getReceiptsOld(); //gets older first

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insertReceipt(Receipt receipt);

    @Update
    public void updateReceipt(Receipt receipt);

    @Delete
    public void deleteReceipt(Receipt receipt);
}
