package com.example.budgyreceipt;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Receipt.class, Overview.class}, version = 1)
public abstract class ReceiptDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "receipts.db";

    private static ReceiptDatabase mReceiptDatabase;

    public static ReceiptDatabase getInstance(Context context) {
        if (mReceiptDatabase == null) {
            mReceiptDatabase = Room.databaseBuilder(context, ReceiptDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
            mReceiptDatabase.addStarterData();
        }

        return mReceiptDatabase;
    }

    public abstract receiptDao();
    public abstract overviewDao();

    private void addStarterData() {}
}
