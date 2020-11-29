package com.example.budgyreceipt;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import static com.example.budgyreceipt.MainCalculations.dateCalc;
import static com.example.budgyreceipt.MainCalculations.getUniqueIds;
import static com.example.budgyreceipt.MainCalculations.getDatesSorted;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Trends extends AppCompatActivity {

    private TextView fromDate, toDate, test;
    private Button dateCalculatorBtn;
    private List<List<String>> dates_w_totals;
    private String dayDiff;
    private int year, month, day;

    private GraphView lineGraph, barGraph;

    private ReceiptDatabase mReceiptDb;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trends);

        fromDate = findViewById(R.id.dateFrom);
        toDate = findViewById(R.id.dateTo);

        test = findViewById(R.id.test); // can remove later

        dateCalculatorBtn = findViewById(R.id.calcDate);
        lineGraph = (GraphView) findViewById(R.id.line_graph);
        barGraph = (GraphView) findViewById(R.id.bar_graph);

        fromDate.setText("11/28/2013"); // remove
        toDate.setText("11/28/2020"); // remove

//            LineGraphSeries<DataPoint> lineSeries = new LineGraphSeries<>(new DataPoint[] {
//                    new DataPoint(0, 1),
//                    new DataPoint(1, 5),
//                    new DataPoint(2, 3),
//                    new DataPoint(3, 2),
//                    new DataPoint(4, 6)
//            });
//            lineGraph.addSeries(lineSeries);
//            lineSeries.setColor(Color.MAGENTA);
//            lineSeries.setTitle("Example Trend Line");
//            lineSeries.setThickness(8);

        mReceiptDb = ReceiptDatabase.getInstance(getApplicationContext());

        dateCalculatorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    calcDates();
                    setLineGraph();
                    //initGraph(lineGraph);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                dayDiff = "10";

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

    public void initGraph(GraphView graph) {
        // generate Dates
        Calendar calendar = Calendar.getInstance();
        Date d1 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d2 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d3 = calendar.getTime();

        // you can directly pass Date objects to DataPoint-Constructor
        // this will convert the Date to double via Date#getTime()
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(d1, 1),
                new DataPoint(d2, 5),
                new DataPoint(d3, 3)
        });
        graph.addSeries(series);

        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(graph.getContext()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(4);

        // set manual x bounds to have nice steps
        graph.getViewport().setMinX(d1.getTime());
        graph.getViewport().setMaxX(d3.getTime());
        graph.getViewport().setXAxisBoundsManual(true);

        // as we use dates as labels, the human rounding to nice readable numbers
        // is not necessary
        graph.getGridLabelRenderer().setHumanRounding(false);
    }

    private void setLineGraph() throws ParseException {
        final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        final SimpleDateFormat sdf2 = new SimpleDateFormat("MM/yyyy", Locale.ENGLISH);

        DataPoint[] dataPoints = new DataPoint[dates_w_totals.size()];
        for (int i = 0; i < dates_w_totals.size(); i++) {
            List<String> temp = dates_w_totals.get(i);
            Date date = sdf.parse(temp.get(0));
            DataPoint point = new DataPoint(date.getTime(), Double.parseDouble(temp.get(1)));
            dataPoints[i] = point;
        }

        LineGraphSeries<DataPoint> lineSeries;
        lineSeries = new LineGraphSeries<>(dataPoints); // error starts here. For some reason the dates are repeating and aren't in order

        lineGraph.addSeries(lineSeries);
//        lineGraph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(lineGraph.getContext()));
        lineGraph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX){
                    return sdf2.format(new Date((long)value));
                } else {
                    return super.formatLabel(value, false);
                }
            }
        });
        lineGraph.getGridLabelRenderer().setNumHorizontalLabels(4);
        lineGraph.getGridLabelRenderer().setNumVerticalLabels(4);

//        List<String> firstIndex = dates_w_totals.get(0);
//        Date min = sdf.parse(firstIndex.get(0));
//        List<String> lastIndex = dates_w_totals.get(dates_w_totals.size()-1);
//        Date max = sdf.parse(lastIndex.get(0));
//
//        lineGraph.getViewport().setMinX(min.getTime());
//        lineGraph.getViewport().setMaxX(max.getTime());
//        lineGraph.getViewport().setXAxisBoundsManual(true);
        //lineGraph.getGridLabelRenderer().setHumanRounding(false);
//
//        lineSeries.setColor(Color.MAGENTA);
//        lineSeries.setTitle("Example Trend Line");
//        lineSeries.setThickness(8);
    }

    private void calcDates() throws ParseException { // this will return a list of dates from the database
//        ContentValues dates_w_totals = new ContentValues();
        String startDate = fromDate.getText().toString();
        String endDate = toDate.getText().toString();

        List<String> dates = mReceiptDb.overviewDao().getDates();
        List<String> dates_in_period = dateCalc(startDate, endDate, dates);
        List<Integer> overviewIds = new ArrayList<>();
        for (String date: dates_in_period) {
            overviewIds.addAll(mReceiptDb.overviewDao().getDateID(date));
        }
        overviewIds = getUniqueIds(overviewIds); // will be used to get the totals now

        List<Double> totals = new ArrayList<>();
        dates_w_totals = new ArrayList<>(); // nested list to hold totals with their respective dates
        for (int id:overviewIds) {
            List<String> temp = new ArrayList<>();
            totals.add(Double.parseDouble(mReceiptDb.overviewDao().getTotal((long) id))); // grab the double value of the total at the specified id
            temp.add(mReceiptDb.overviewDao().getDate((long) id));
            temp.add(mReceiptDb.overviewDao().getTotal((long) id));
            dates_w_totals.add(temp);
        }

        dates_w_totals = getDatesSorted(dates_w_totals); // gets all the dates with their totals and sorts them based off date (also combine any common date's totals)


        /*So this is what needs to happen next:
        * 1. We need to take the dates in period and
        * run it in a for loop so that we can grab the
        * indexes of all the overviews we want to access
        *
        * 2. We need to take those indexes and then grab
        * the totals to calculate
        *
        * 3. Need to figure how to make it so the graph's
        * x-axis is the dates and the y-axis is the amounts
        *
        * This will solve the first graph's problem*/
    }
}

