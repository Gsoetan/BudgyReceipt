package com.example.budgyreceipt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // start the main activity after this page shows up
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}