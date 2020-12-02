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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import static com.example.budgyreceipt.MainCalculations.dateCalc;
import static com.example.budgyreceipt.MainCalculations.getUniqueIds;
import static com.example.budgyreceipt.MainCalculations.getDatesSorted;
import static com.example.budgyreceipt.MainCalculations.getTagsSorted;
import static com.example.budgyreceipt.MainCalculations.getSortedDates;
import static com.example.budgyreceipt.MainCalculations.getDateFormatted;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Trends extends AppCompatActivity {

    private TextView fromDate, toDate, test;
    private Button dateCalculatorBtn;
    private List<List<String>> dates_w_totals, tags_w_totals;
    private List<String> dates_in_order;
    private int year, month, day;

    private LineChart lineGraph;
    private BarChart barGraph;

    private ReceiptDatabase mReceiptDb;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trends);

        fromDate = findViewById(R.id.dateFrom);

        String currentDate = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).format(new Date());
        toDate = findViewById(R.id.dateTo);
        toDate.setText(currentDate);

        dateCalculatorBtn = findViewById(R.id.calcDate);
        lineGraph = (LineChart) findViewById(R.id.line_graph);
        barGraph = (BarChart) findViewById(R.id.bar_graph);

        mReceiptDb = ReceiptDatabase.getInstance(getApplicationContext());

        dateCalculatorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    setLineGraph();
                    setBarGraph();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
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

    private void setBarGraph() throws ParseException {
        calcDates(false);
        final List<String> res_tags = Arrays.asList(getResources().getStringArray(R.array.tags));

        barGraph.setDrawBarShadow(false);
        barGraph.setDrawValueAboveBar(true);
        barGraph.setDrawGridBackground(true);
        Description description = new Description();
        description.setText("");
        barGraph.setDescription(description);

        List<BarEntry> barEntries = new ArrayList<>();
        for (int i = 0; i < tags_w_totals.size(); i++) {
            List<String> temp = tags_w_totals.get(i);
            int temp_index = res_tags.indexOf(temp.get(0));
            barEntries.add(new BarEntry(temp_index, (float) Double.parseDouble(temp.get(1))));
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Spending Categories");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        BarData data = new BarData(barDataSet);
        data.setBarWidth(0.6f);

        barGraph.setData(data);

        XAxis xAxis = barGraph.getXAxis();
        xAxis.setValueFormatter(new XAxisFormatter(res_tags));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setCenterAxisLabels(false);

        barGraph.invalidate();

    }

    public class XAxisFormatter extends ValueFormatter {
        private List<String> xValues;

        public XAxisFormatter(List<String> values){
            this.xValues = values;
        }

        @Override
        public String getFormattedValue(float value) {
            return xValues.get((int) value); // where the error stems from
        }
    }

    public class XAxisFormatterLine extends ValueFormatter {
        private List<String> xValues;

        public XAxisFormatterLine(List<String> values){
            this.xValues = values;
        }

        @Override
        public String getFormattedValue(float value) {
            Date date = new Date((long) value);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/yy", Locale.ENGLISH);
            return sdf.format(date);
        }
    }

    private void setLineGraph() throws ParseException {
        calcDates(true);
        final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);

        lineGraph.setTouchEnabled(true);
        lineGraph.setPinchZoom(true);

        XAxis xAxis = lineGraph.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(315f);
        xAxis.setGranularity(1);

        Description description = new Description();
        description.setText("Months & Years");
        lineGraph.setDescription(description);
        xAxis.setValueFormatter(new XAxisFormatterLine(dates_in_order));

        List<Entry> lineEntries = new ArrayList<>();
        for (int i = 0; i < dates_w_totals.size(); i++) {
            List<String> temp = dates_w_totals.get(i);
            Date date = sdf.parse(temp.get(0));
            lineEntries.add(new BarEntry(date.getTime(), (float) Double.parseDouble(temp.get(1))));
        }

        LineDataSet lineDataSet = new LineDataSet(lineEntries, "Spending in period");
        lineDataSet.setLineWidth(4f);
        LineData lineData = new LineData(lineDataSet);
        lineGraph.setData(lineData);

        lineGraph.invalidate();
    }

    private void calcDates(Boolean isLine) throws ParseException { // this will return a list of dates from the database
        String startDate = fromDate.getText().toString();
        String endDate = toDate.getText().toString();

        List<String> dates = mReceiptDb.overviewDao().getDates();
        List<String> dates_in_period = dateCalc(startDate, endDate, dates);
        List<Integer> overviewIds = new ArrayList<>();
        for (String date: dates_in_period) {
            overviewIds.addAll(mReceiptDb.overviewDao().getDateID(date));
        }
        overviewIds = getUniqueIds(overviewIds); // will be used to get the totals now

        dates_w_totals = new ArrayList<>(); // nested list to hold totals with their respective dates
        tags_w_totals = new ArrayList<>(); // nested list to hold all tags with their respective totals and dates
        dates_in_order = new ArrayList<>();
        for (int id:overviewIds) {
            List<String> temp = new ArrayList<>();
            if (isLine) {
                dates_in_order.add(mReceiptDb.overviewDao().getDate((long) id));
                temp.add(mReceiptDb.overviewDao().getDate((long) id));
                temp.add(mReceiptDb.overviewDao().getTotal((long) id));
                dates_w_totals.add(temp);
            } else {
                temp.add(mReceiptDb.overviewDao().getTag((long) id));
                temp.add(mReceiptDb.overviewDao().getTotal((long) id));
                tags_w_totals.add(temp);
            }
        }

        if (isLine) { // gets all the dates with their totals and sorts them based off date (also combine any common date's totals)
            dates_w_totals = getDatesSorted(dates_w_totals);
            dates_in_order = getSortedDates(dates_in_order);
        }
        else { tags_w_totals = getTagsSorted(tags_w_totals); }
    }
}

