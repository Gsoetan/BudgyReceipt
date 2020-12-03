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

    @Query("SELECT date FROM Overview")
    public List<String> getDates(); // used to just get all dates

    @Query("SELECT total FROM Overview WHERE id = :id")
    public String getTotal(long id); // used to get specified total by their id

    @Query("SELECT date FROM Overview WHERE id = :id")
    public String getDate(long id); // used to get specified date by their id

    @Query("SELECT tag FROM Overview WHERE id = :id")
    public String getTag(long id); // used to get specified date by their id

    @Query("SELECT id FROM Overview WHERE date = :date")
    public List<Integer> getDateID(String date); // used for matching dates. This is the first step to using the query above

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insertOverview(Overview overview);

    @Update
    public void updateOverview(Overview overview);

    @Delete
    public void deleteOverview(Overview overview);

}
