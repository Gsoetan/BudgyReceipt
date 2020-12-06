package com.example.budgyreceipt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.List;

// This is also the receipt activity that will store all of the user created receipt entities
public class MainActivity extends AppCompatActivity implements ReceiptFragment.OnReceiptCreatedListener {

    private ReceiptDatabase mReceiptdb;
    // For the recycler view
    private ReceiptAdapter mReceiptAdapter;
    private RecyclerView mRecyclerView;
    // For deletion of a receipt/recycler view item
    private Receipt selectedReceipt;
    private int selectedReceiptPos = RecyclerView.NO_POSITION;
    // Side drawer
    private DrawerLayout drawer;
    // Action variables. One for deletion and one for showing the drawer
    private ActionMode action = null;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawer = findViewById(R.id.drawer_layout);
        mRecyclerView = findViewById(R.id.receiptRecyclerView);

        // setup for the drawer
        drawer.closeDrawers();
        drawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.Open, R.string.Close);
        drawerToggle.setDrawerIndicatorEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // (singleton)
        mReceiptdb = ReceiptDatabase.getInstance(getApplicationContext());

        // get the recycler view and append it to a grid view with a single column
        RecyclerView.LayoutManager gridLayoutManager =
                new GridLayoutManager(getApplicationContext(), 1);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        drawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Creation of the listener for the drawer
        NavigationView nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.nav_about){
                    startActivity(new Intent( MainActivity.this, About.class));
                }
                if (id == R.id.nav_goals){
                    startActivity(new Intent( MainActivity.this, Goals.class));
                }
               if (id == R.id.nav_home){
                    startActivity(new Intent( MainActivity.this, MainActivity.class));
                }
                if (id == R.id.nav_trends){
                    startActivity(new Intent( MainActivity.this, Trends.class));
                }
                return true;
            }
        });
        // get the adapter and append it to the view
        mReceiptAdapter = new ReceiptAdapter(getReceipts(false));
        mRecyclerView.setAdapter(mReceiptAdapter);
    }

    @Override
    public void onReceiptCreated(String receiptMerchant) {
        if (receiptMerchant.length() > 0) {
            Receipt receipt = new Receipt(receiptMerchant); // create a new receipt
            long receiptId = mReceiptdb.receiptDao().insertReceipt(receipt); // inserts receipt into database and returns it's new id in database
            receipt.setId(receiptId); // set the receipt's object id as the id in the database
            mReceiptAdapter.addReceipt(receipt); // add receipt to the adapter for viewing
            Toast.makeText(this, "Added " + receiptMerchant, Toast.LENGTH_SHORT).show();
        }
    }

    public void addReceiptClick(View view) { // adds a new fragment and receipt
        FragmentManager manager = getSupportFragmentManager();
        ReceiptFragment frag = new ReceiptFragment();
        frag.show(manager, "receiptFragment");
    }

    private List<Receipt> getReceipts(Boolean isAsc) { // load the receipts into the activity based off user's choice (based on the updated time)
        if (isAsc){
            return mReceiptdb.receiptDao().getReceiptsOld(); // load older items first
        } else if (!isAsc){
            return mReceiptdb.receiptDao().getReceiptsNew(); // load newer items first
        }
        return mReceiptdb.receiptDao().getReceiptsNew(); // by default the newer items are loaded first
    }

    private class ReceiptHolder extends RecyclerView.ViewHolder implements  View.OnClickListener, View.OnLongClickListener {

        private Receipt mReceipt;
        private final TextView mText_merchant;
        private final TextView mText_total;
        private final TextView mText_date;
        private final ImageView mIcon;

        // inflates/initializes and shows each singular receipt
        public ReceiptHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.recycler_view_items, parent, false));
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mText_merchant = itemView.findViewById(R.id.merchant_text);
            mText_total = itemView.findViewById(R.id.total_text);
            mText_date = itemView.findViewById(R.id.date_text);
            mIcon = itemView.findViewById(R.id.receipt_icon);
        }

        // add the data to the receipt view
        public void bind(Receipt receipt, int pos) { // make the fragment
            mReceipt = receipt;
            mText_merchant.setText(receipt.getMerchant());
            mText_total.setText("Total: $" + receipt.getTotal());
            long index = receipt.getId();
            mText_date.setText(mReceiptdb.overviewDao().getDate(index));

            // for deletion: if the receipt is selected for deletion it'll be highlighted
            if (selectedReceiptPos == pos){
                mText_merchant.setBackgroundColor(Color.LTGRAY);
                mIcon.setBackgroundColor(Color.LTGRAY);
            }
            else {
                mText_merchant.setBackgroundColor(Color.WHITE);
                mIcon.setBackgroundColor(Color.WHITE);
            }
        }

        @Override
        public void onClick(View v) {
            // Start the Overview activity with the selected receipt
            Intent intent = new Intent(MainActivity.this, OverviewActivity.class);
            intent.putExtra(OverviewActivity.EXTRA_RECEIPT_ID, mReceipt.getId());
            startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v){
            // When an item is held down notify the view of which item it is and start the deletion menu
            if (action != null){ return false; }
            selectedReceipt = mReceipt;
            selectedReceiptPos = getAdapterPosition();
            mReceiptAdapter.notifyItemChanged(selectedReceiptPos);
            action = MainActivity.this.startActionMode(actionCallback);
            return true;
        }
    }

    private final ActionMode.Callback actionCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) { // shows the deletion menu
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.hold_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) { return false; }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.delete){ // if the trash icon is clicked then delete receipt from the database (will delete associated overview as well)
                mReceiptdb.receiptDao().deleteReceipt(selectedReceipt);
                mReceiptAdapter.removeReceipt(selectedReceipt);
                mode.finish(); // clear the menu when done
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) { // reverting back to the state of the program before anything was selected
            action = null;
            mReceiptAdapter.notifyItemChanged(selectedReceiptPos);
            selectedReceiptPos = RecyclerView.NO_POSITION;
        }
    };

    private class ReceiptAdapter extends RecyclerView.Adapter<ReceiptHolder> {

        private final List<Receipt> mReceiptList;

        public ReceiptAdapter(List<Receipt> receipts) {
            mReceiptList = receipts;
        }

        @NonNull
        @Override
        public ReceiptHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { // creates the viewGroup that will hold the singular receipts
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new ReceiptHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(ReceiptHolder holder, int position) {
            holder.bind(mReceiptList.get(position), position); //bind the receipts to the viewGroup
        }

        @Override
        public int getItemCount() {
            return mReceiptList.size();
        }

        public void addReceipt(Receipt receipt) { // adds to receipt list on page and animates the addition
            mReceiptList.add(0, receipt);
            notifyItemInserted(0);
            mRecyclerView.scrollToPosition(0);
        }

        public void removeReceipt(Receipt receipt) { // removes the receipt at the given position and animates it as well
            int receipt_position = mReceiptList.indexOf(receipt);
            if (receipt_position >= 0){
                mReceiptList.remove(receipt_position);
                notifyItemRemoved(receipt_position);
            }
        }
    }

    //actionbar menu (settings)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    //handle action bar item clicks (settings)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.ascending){
            Toast.makeText(this,"Ascending", Toast.LENGTH_SHORT).show();
            mReceiptAdapter = new ReceiptAdapter(getReceipts(true));
            mRecyclerView.setAdapter(mReceiptAdapter);
        }
        if (id == R.id.descending){
            Toast.makeText(this,"Descending", Toast.LENGTH_SHORT).show();
            mReceiptAdapter = new ReceiptAdapter(getReceipts(false));
            mRecyclerView.setAdapter(mReceiptAdapter);
        }

        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}