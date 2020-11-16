package com.example.budgyreceipt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

// This is also the receipt activity that will store all of the user created receipt entities
public class MainActivity extends AppCompatActivity implements ReceiptFragment.OnReceiptCreatedListener {

    private ReceiptDatabase mReceiptdb;
    private ReceiptAdapter mReceiptAdapter;
    private RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mReceiptdb = ReceiptDatabase.getInstance(getApplicationContext());

        mRecyclerView = findViewById(R.id.receiptRecyclerView);
        RecyclerView.LayoutManager gridLayoutManager =
                new GridLayoutManager(getApplicationContext(), 1);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        //maybe add layout manager for horizontal bars? idk yet

        updateView();
    }

    @Override
    public void onReceiptCreated(String receiptMerchant) {
        if (receiptMerchant.length() > 0) {
            Receipt receipt = new Receipt(receiptMerchant);
            long receiptId = mReceiptdb.receiptDao().insertReceipt(receipt);
            receipt.setId(receiptId);

            updateView();
            Toast.makeText(this, "Added " + receiptMerchant, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateView() {
        mReceiptAdapter = new ReceiptAdapter(getReceipts());
        mRecyclerView.setAdapter(mReceiptAdapter);
    }

    public void addReceiptClick(View view) { // adds a new fragment and receipt
        FragmentManager manager = getSupportFragmentManager();
        ReceiptFragment frag = new ReceiptFragment();
        frag.show(manager, "receiptFragment");
    }

    private List<Receipt> getReceipts() { return mReceiptdb.receiptDao().getReceiptsNew(); } // load the receipts into the activity based off newest additions

    private class ReceiptHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {

        private Receipt mReceipt;
        private TextView mText_merchant, mText_total, mText_date;

        public ReceiptHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.recycler_view_items, parent, false));
            itemView.setOnClickListener(this);
            mText_merchant = itemView.findViewById(R.id.merchant_text);
            mText_total = itemView.findViewById(R.id.total_text);
            mText_date = itemView.findViewById(R.id.date_text);
        }

        public void bind(Receipt receipt) { // make the fragment
            mReceipt = receipt;
            mText_merchant.setText(receipt.getMerchant());
            mText_total.setText("$" + receipt.getTotal());
            // TODO: mText_date.setText();
        }

        @Override
        public void onClick(View v) {
            // Start the Overview activity with the selected receipt
            Intent intent = new Intent(MainActivity.this, OverviewActivity.class);
            intent.putExtra(OverviewActivity.EXTRA_RECEIPT_ID, mReceipt.getId());
            startActivity(intent);
        }
    }

    private class ReceiptAdapter extends RecyclerView.Adapter<ReceiptHolder> {

        private List<Receipt> mReceiptList;

        public ReceiptAdapter(List<Receipt> receipts) { mReceiptList = receipts; }

        @NonNull
        @Override
        public ReceiptHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new ReceiptHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(ReceiptHolder holder, int position) {
            holder.bind(mReceiptList.get(position));
        }

        @Override
        public int getItemCount() { return mReceiptList.size(); }
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