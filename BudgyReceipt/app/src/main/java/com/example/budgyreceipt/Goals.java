package com.example.budgyreceipt;


import androidx.appcompat.app.AppCompatActivity;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;


import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import android.view.View;

import android.widget.Button;
import android.widget.DatePicker;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.text.DateFormat;
import java.util.Calendar;

public class Goals extends AppCompatActivity {
    private static final String TAG = "Goals";
    public static final String USER_PREF = "USER_PREF" ;
    public static final String KEY_NAME = "KEY_NAME";
    public static final String KEY_DATE = "KEY_DATE";
    SharedPreferences sp;
    private EditText mGoalText;
    private TextView mDisplayDate1;
    private DatePickerDialog.OnDateSetListener mDateSetListener1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);
        mGoalText = (EditText) findViewById(R.id.eGoal);
        sp = getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);


        mDisplayDate1 =  findViewById(R.id.tvDate);
        mDisplayDate1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        Goals.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener1,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        TextView textViewDate = findViewById(R.id.currDate);
        textViewDate.setText(currentDate);

        mDateSetListener1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG,"onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date = month + "/" + day + "/" + year;
                mDisplayDate1.setText(date);
            }
        };



    }
    public void save(View v) {
        String name  = mGoalText.getText().toString();

        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_NAME, name);
        editor.commit();

        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
    }

    public void show(View v) {
        StringBuilder str = new StringBuilder();
        if (sp.contains(KEY_NAME)) {
            mGoalText.setText(sp.getString(KEY_NAME, ""));
        }
    }

    public void clear(View v) {
        mGoalText.setText("");
    }
}