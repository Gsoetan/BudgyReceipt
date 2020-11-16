package com.example.budgyreceipt;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface OverviewDao {
    @Query("SELECT * FROM Overview WHERE id = :id")
    public Overview getOverview(long id);

    @Query("SELECT * FROM Overview WHERE receiptId = :receiptId ORDER BY id")
    public List<Overview> getOverviews(long receiptId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insertOverview(Overview overview);

    @Update
    public void updateOverview(Overview overview);

    @Delete
    public void deleteOverview(Overview overview);

}
