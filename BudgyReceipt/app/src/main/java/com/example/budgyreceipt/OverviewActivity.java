package com.example.budgyreceipt;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class OverviewActivity extends AppCompatActivity {

    private final int REQUEST_CODE_UPDATE_ENTRY = 1;

    private ReceiptDatabase mReceiptDb;
    private long mReceiptId;
    private List<Overview> mOverviewList;
    private TextView mTitle, mDate, mTotal, mSubTotal, mPayment, mTag;
    private ImageView mPhoto;
    private FloatingActionButton edit;

    public static final String EXTRA_RECEIPT_ID = "com.example.budgyreceipt.receipt_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        // MainActivity should've provided the receipt id of the overview to display
        Intent intent = getIntent();
        mReceiptId = intent.getLongExtra(EXTRA_RECEIPT_ID, -1);

        // get the entry data for this overview
        getEntryData();

        mTitle = findViewById(R.id.merchant);
        mDate = findViewById(R.id.date);
        mTotal = findViewById(R.id.total);
        mSubTotal = findViewById(R.id.subTotal);
        mPayment = findViewById(R.id.payment);
        mPhoto = findViewById(R.id.photo);
        mTag = findViewById(R.id.tag); //https://developer.android.com/guide/topics/ui/controls/spinner.html

        edit = findViewById(R.id.editText);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editOverview();
            }
        });

    }

    private void getEntryData() {
        mReceiptDb = ReceiptDatabase.getInstance(getApplicationContext());
        mOverviewList = mReceiptDb.overviewDao().getOverviews(mReceiptId);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mOverviewList.size() == 0) { // just checking that there is an entry in the database for this receipt or if empty
            mTitle.setText(mReceiptDb.receiptDao().getReceipt(mReceiptId).getMerchant());
            showReceiptContents(false);
        } else {
            mTitle.setText(mReceiptDb.receiptDao().getReceipt(mReceiptId).getMerchant());
            showReceiptContents(true);
        }
    }

    private void showReceiptContents(boolean showContents) {
        if (showContents == true) {
            mDate.setText(mReceiptDb.overviewDao().getOverview(mReceiptId).getDate());
            mTotal.setText(mReceiptDb.overviewDao().getOverview(mReceiptId).getTotal());
            mSubTotal.setText(mReceiptDb.overviewDao().getOverview(mReceiptId).getSubtotal());
            mTag.setText(mReceiptDb.overviewDao().getOverview(mReceiptId).getTag());
            mPayment.setText(mReceiptDb.overviewDao().getOverview(mReceiptId).getPayment());
        } else {
            Overview overview = new Overview();
            overview.setDate("empty");
            overview.setTotal("empty");
            overview.setSubtotal("empty");
            overview.setTag("Groceries");
            overview.setPayment("empty");
            overview.setReceiptId(mReceiptId);
            mReceiptDb.overviewDao().insertOverview(overview);
            showReceiptContents(true);
//            mDate.setText("");
//            mTotal.setText("");
//            mSubTotal.setText("");
//            mTag.setText("");
//            mPayment.setText("");
        }
    }

    private void editOverview() {
        Intent intent = new Intent(this, OverviewEditActivity.class);
        intent.putExtra(EXTRA_RECEIPT_ID, mReceiptId);
        getEntryData();
        long overviewId = mOverviewList.get(0).getId();
        intent.putExtra(OverviewEditActivity.EXTRA_OVERVIEW_ID, overviewId);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_ENTRY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_UPDATE_ENTRY) {

            // get updated overview
            long overviewId = data.getLongExtra(OverviewEditActivity.EXTRA_OVERVIEW_ID, -1);
            Overview updatedOverview = mReceiptDb.overviewDao().getOverview(overviewId);

            Overview currentOverview = mOverviewList.get(0);
            currentOverview.setDate(updatedOverview.getDate());
            currentOverview.setTotal(updatedOverview.getTotal());
            currentOverview.setSubtotal(updatedOverview.getSubtotal());
            currentOverview.setTag(updatedOverview.getTag());
            currentOverview.setPayment(updatedOverview.getPayment());
            // grab img

            Toast.makeText(this, "Entries updated", Toast.LENGTH_SHORT).show();
        }
    }
}