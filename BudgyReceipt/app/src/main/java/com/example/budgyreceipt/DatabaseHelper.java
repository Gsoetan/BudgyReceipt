package com.example.budgyreceipt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "itemEntries.db";
    public static final String TABLE_NAME = "itemEntries";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "Title";
    public static final String COLUMN_DATE = "Date";
    public static final String COLUMN_TOTAL = "Total";
    public static final String COLUMN_SUB_TOTAL = "Sub_total";
    public static final String COLUMN_TAG = "Tag";
    public static final String COLUMN_PAYMENT = "Payment";
    // public static final String COL8 = "photo";

    public static final String CREATE_DATABASE = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_TITLE + " TEXT, " + COLUMN_DATE + " TEXT, " + COLUMN_TOTAL + " TEXT, " + COLUMN_SUB_TOTAL + " TEXT, " + COLUMN_TAG + " TEXT, " + COLUMN_PAYMENT + " TEXT)";
    //add photo to query later

    public static final String DELETE_DATABASE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public DatabaseHelper (Context context) { super(context, DATABASE_NAME, null, DATABASE_VERSION); } // https://stackoverflow.com/questions/28665039/android-database-sqlite-sqliteexception-near-syntax-error-code-1-while/28665290

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_DATABASE);
        onCreate(db);
    }

    public boolean addData (String title, String date, String total, String subtotal, String tag, String payment) { // add photo later
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TITLE, title);
        contentValues.put(COLUMN_DATE, date);
        contentValues.put(COLUMN_TOTAL, total);
        contentValues.put(COLUMN_SUB_TOTAL, subtotal);
        contentValues.put(COLUMN_TAG, tag);
        contentValues.put(COLUMN_PAYMENT, payment);
        // contentValues.put(COL8, photo);

        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }

    }

    public Cursor getListContents() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        String[] colValues = new String[] {COLUMN_TITLE, COLUMN_DATE, COLUMN_TOTAL, COLUMN_SUB_TOTAL, COLUMN_TAG, COLUMN_PAYMENT};
        Cursor data = db.query(TABLE_NAME, colValues, null, null, null, null, null);
        return data;
    }
}
