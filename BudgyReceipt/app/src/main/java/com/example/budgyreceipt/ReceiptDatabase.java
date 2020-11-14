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

    public abstract ReceiptDao receiptDao();
    public abstract OverviewDao overviewDao();

    private void addStarterData() {

        // set up a few "dummy" entries
        if (receiptDao().getReceipts().size() == 0) {


            //this executes on a background thread
            runInTransaction(new Runnable() {
                @Override
                public void run() {
                    // first dummy receipt
                    Receipt receipt = new Receipt();
                    receipt.setMerchant("Walmart");
                    long receiptId = receiptDao().insertReceipt(receipt);

                    Overview overview = new Overview();
                    overview.setDate("11/13/2020");
                    overview.setTotal("98.81");
                    overview.setSubtotal("95.00");
                    overview.setTag("Groceries");
                    overview.setPayment("Credit Card");
                    overview.setReceiptId(receiptId);
                    overviewDao().insertOverview(overview);

                    // second dummy receipt
                    receipt = new Receipt();
                    receipt.setMerchant("Best Buy");
                    receiptId = receiptDao().insertReceipt(receipt);

                    overview = new Overview();
                    overview.setDate("11/27/2020");
                    overview.setTotal("507.41");
                    overview.setSubtotal("478.69");
                    overview.setTag("Entertainment");
                    overview.setPayment("Debit Card");
                    overview.setReceiptId(receiptId);
                    overviewDao().insertOverview(overview);
                }
            });
        }
    }
}
