package com.example.budgyreceipt;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    private TextView oTitleEt, oDateEt, oTotalEt, oSubTotalEt, oPaymentEt, tag;
    private ImageView oPhotoIv;
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
        mReceiptDb = ReceiptDatabase.getInstance(getApplicationContext());
        mOverviewList = mReceiptDb.overviewDao().getOverviews(mReceiptId);

        oTitleEt = findViewById(R.id.merchant);
        oDateEt = findViewById(R.id.date);
        oTotalEt = findViewById(R.id.total);
        oSubTotalEt = findViewById(R.id.subTotal);
        oPaymentEt = findViewById(R.id.payment);
        oPhotoIv = findViewById(R.id.photo);
        tag = findViewById(R.id.tag); //https://developer.android.com/guide/topics/ui/controls/spinner.html

        edit = findViewById(R.id.editText);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editOverview();
            }
        });

    }

    private void editOverview() {
        Intent intent = new Intent(this, OverviewEditActivity.class);
        intent.putExtra(EXTRA_RECEIPT_ID, mReceiptId);
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