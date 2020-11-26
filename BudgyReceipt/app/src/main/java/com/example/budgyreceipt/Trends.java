package com.example.budgyreceipt;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class Trends extends AppCompatActivity{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trends);
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
        }
    }

