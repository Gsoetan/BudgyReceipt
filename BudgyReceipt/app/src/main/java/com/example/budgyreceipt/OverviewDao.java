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
    Overview getOverview(long id);

    @Query("SELECT * FROM Overview WHERE receiptId = :receiptId ORDER BY id")
    List<Overview> getOverviews(long receiptId);

    @Query("SELECT date FROM Overview")
    List<String> getDates(); // used to just get all dates

    @Query("SELECT total FROM Overview WHERE id = :id")
    String getTotal(long id); // used to get specified total by their id

    @Query("SELECT date FROM Overview WHERE id = :id")
    String getDate(long id); // used to get specified date by their id

    @Query("SELECT tag FROM Overview WHERE id = :id")
    String getTag(long id); // used to get specified date by their id

    @Query("SELECT id FROM Overview WHERE date = :date")
    List<Integer> getDateID(String date); // used for matching dates. This is the first step to using the query above

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertOverview(Overview overview);

    @Update
    void updateOverview(Overview overview);

    @Delete
    void deleteOverview(Overview overview);

}
