package com.example.budgyreceipt;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import static com.example.budgyreceipt.MainCalculations.dateCalc;

import java.text.ParseException;
import java.util.Calendar;

public class Trends extends AppCompatActivity{

    private TextView fromDate, toDate, test;
    private Button dateCalculatorBtn;
    private String dayDiff;
    private int year, month, day;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trends);

        fromDate = findViewById(R.id.dateFrom);
        toDate = findViewById(R.id.dateTo);

        test = findViewById(R.id.test); // can remove later

        dateCalculatorBtn = findViewById(R.id.calcDate);
        GraphView linegraph = (GraphView) findViewById(R.id.line_graph);

            LineGraphSeries<DataPoint> lineSeries = new LineGraphSeries<>(new DataPoint[] {
                    new DataPoint(0, 1),
                    new DataPoint(1, 5),
                    new DataPoint(2, 3),
                    new DataPoint(3, 2),
                    new DataPoint(4, 6)
            });
            linegraph.addSeries(lineSeries);
            lineSeries.setColor(Color.MAGENTA);
            lineSeries.setTitle("Example Trend Line");
            lineSeries.setThickness(8);

            dateCalculatorBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dayDiff = dateCalc(fromDate.getText().toString(), toDate.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    test.setText(dayDiff);
                }
            });

            fromDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar cal = Calendar.getInstance();
                    year = cal.get(Calendar.YEAR);
                    month = cal.get(Calendar.MONTH);
                    day = cal.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(Trends.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int day) {
                            month = month + 1;
                            String date = month + "/" + day + "/" + year;
                            fromDate.setText(date);
                        }
                    }, year, month, day);
                    datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    datePickerDialog.show();
                }
            });

            toDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar cal = Calendar.getInstance();
                    year = cal.get(Calendar.YEAR);
                    month = cal.get(Calendar.MONTH);
                    day = cal.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(Trends.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int day) {
                            month = month + 1;
                            String date = month + "/" + day + "/" + year;
                            toDate.setText(date);
                        }
                    }, year, month, day);
                    datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    datePickerDialog.show();
                }
            });

        }
    }

