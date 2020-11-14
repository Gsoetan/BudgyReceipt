package com.example.budgyreceipt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ReceiptFragment.OnReceiptCreatedListener {

    Button add_item_button;
    private ReceiptDatabase mReceiptdb;
    private ReceiptAdapter mReceiptAdapter;
    private RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mReceiptdb = ReceiptDatabase.getInstance(getApplicationContext());

        mRecyclerView = findViewById(R.id.receiptRecyclerView);

        //maybe add layout manager for horizontal bars? idk yet

        mReceiptAdapter = new ReceiptAdapter(getReceipts()); // gotta set this
        mRecyclerView.setAdapter(mReceiptAdapter);


        // Switch from Main to OverviewActivity
        add_item_button = (Button)findViewById(R.id.addItem);
        add_item_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, OverviewActivity.class));
            }
            });
    }

    @Override
    public void onReceiptCreated(String receiptMerchant) {
        if (receiptMerchant.length() > 0) {
            Receipt receipt = new Receipt(receiptMerchant);
            long receiptId = mReceiptdb.receiptDao().insertReceipt(receipt);
            receipt.setId(receiptId);

            // TODO: add subject to recycler view
            // TODO: add toast to confirm addition of merchant
        }
    }

    //actionbar menu (settings)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //handle action bar item clicks (settings)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.weekly){
            Toast.makeText(this,"Weekly", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.monthly){
            Toast.makeText(this,"Monthly", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}