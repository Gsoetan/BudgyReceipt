package com.example.budgyreceipt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.navigation.NavigationView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

import static com.example.budgyreceipt.MainCalculations.stringParse;

public class MainActivity extends AppCompatActivity {

    private Button clickme;
    private DrawerLayout draw;
    private ActionBarDrawerToggle test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        draw = (DrawerLayout)findViewById(R.id.drawer_layout);
        draw.closeDrawers();
        test = new ActionBarDrawerToggle(this, draw, R.string.Open, R.string.Close);
        test.setDrawerIndicatorEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        draw.addDrawerListener(test);
        test.syncState();

        NavigationView nav_view = (NavigationView)findViewById(R.id.nav_view);
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
                if (id == R.id.nav_receipt){
                    startActivity(new Intent( MainActivity.this, OverviewActivity.class));
                } if (id == R.id.nav_home){
                    startActivity(new Intent( MainActivity.this, MainActivity.class));
                }
                return true;
            }
        });
        // Switch from Main to OverviewActivity
        clickme = (Button)findViewById(R.id.home);
        clickme.setOnClickListener(new View.OnClickListener() {
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

        return test.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

}