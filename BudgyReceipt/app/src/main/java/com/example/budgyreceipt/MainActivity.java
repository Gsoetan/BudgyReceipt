package com.example.budgyreceipt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class MainActivity extends AppCompatActivity {

    Button add_item_button, button1;
    DatabaseHelper overviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView trial = (ListView) findViewById(R.id.trial);

        overviews = new DatabaseHelper(this);

        ArrayList<String> titles = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();
        ArrayList<String> totals = new ArrayList<>();
        ArrayList<String> subtotals = new ArrayList<>();
        ArrayList<String> tags = new ArrayList<>();
        ArrayList<String> payments = new ArrayList<>();
        // ArrayList<String> photos = new ArrayList<>();
        Cursor data = overviews.getListContents();


        while(data.moveToNext()){
            titles.add(data.getString(1));
            dates.add(data.getString(2));
            totals.add(data.getString(3));
            subtotals.add(data.getString(4));
            tags.add(data.getString(5));
            payments.add(data.getString(6));
            // titles.add(data.getString(7));

            ListAdapter listTitles = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,titles);
            trial.setAdapter(listTitles);
        }


        // Switch from Main to OverviewActivity
        add_item_button = (Button)findViewById(R.id.addItem);
        add_item_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, OverviewActivity.class));
            }
            });

        button1 = (Button) findViewById(R.id.B1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, OverviewActivity.class));
            }
        });
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
}