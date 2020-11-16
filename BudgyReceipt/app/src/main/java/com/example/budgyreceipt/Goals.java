package com.example.budgyreceipt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.material.navigation.NavigationView;

public class Goals extends AppCompatActivity {

    private ImageButton clickme;
    private DrawerLayout draw;
    private ActionBarDrawerToggle test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);
        clickme = (ImageButton)findViewById(R.id.HButton);
        clickme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Goals.this, MainActivity.class));
            }
        });
    }
}