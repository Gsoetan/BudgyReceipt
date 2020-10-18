package com.example.budgyreceipt;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "itemEntries.db";
    public static final String TABLE_NAME = "itemEntries_data";
    public static final String COL1 = "ID";
    public static final String COL2 = "date";
    public static final String COL3 = "total";
    public static final String COL4 = "sub-total";
    public static final String COL5 = "tag";
    public static final String COL6 = "payment";
    public static final String COL7 = "photo";

    public DatabaseHelper (Context context) { super(context, DATABASE_NAME, null, 1); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + "date TEXT, total TEXT, subtotal TEXT, tag TEXT, payment TEXT, photo TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
    }
}
