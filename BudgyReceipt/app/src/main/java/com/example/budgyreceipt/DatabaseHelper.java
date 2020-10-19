package com.example.budgyreceipt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private SQLiteDatabase db = this.getWritableDatabase();

    static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "itemEntries.db";
    public static final String TABLE_NAME = "itemEntries_data";
    public static final String COL1 = "ID";
    public static final String COL2 = "Title";
    public static final String COL3 = "Date";
    public static final String COL4 = "Total";
    public static final String COL5 = "Sub-total";
    public static final String COL6 = "Tag";
    public static final String COL7 = "Payment";
    // public static final String COL8 = "photo";

    public static final String CREATE_DATABASE = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL2 + " TEXT, " + //title
            COL3 + " TEXT, " + //date
            COL4 + " TEXT, " + //total
            COL5 + " TEXT, " + //sub-total
            COL6 + " TEXT, " + //tag
            COL7 + " TEXT);"; //payment
    //add photo to query later

    public DatabaseHelper (Context context) { super(context, DATABASE_NAME, null, DATABASE_VERSION); } // https://stackoverflow.com/questions/28665039/android-database-sqlite-sqliteexception-near-syntax-error-code-1-while/28665290

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData (String title, String date, String total, String subtotal, String tag, String payment) { // add photo later
        // SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, title);
        contentValues.put(COL3, date);
        contentValues.put(COL4, total);
        contentValues.put(COL5, subtotal);
        contentValues.put(COL6, tag);
        contentValues.put(COL7, payment);
        // contentValues.put(COL8, photo);

        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }

    }

    public Cursor getListContents() {
        // SQLiteDatabase db = this.getWritableDatabase();
        // Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        String[] colValues = new String[] {COL2, COL3, COL4, COL5, COL6, COL7};
        Cursor data = db.query(TABLE_NAME, colValues, null, null, null, null, null);
        return data;
    }
}
