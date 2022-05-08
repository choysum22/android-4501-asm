package com.example.mathgame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PieChartActivity extends AppCompatActivity {

    // declare variable
    Button bt_return;
    RelativeLayout loadingPanel;
    int correctCount, wrongCount;
    LinearLayout ly;
    TextView tv_accuracy, tv_correct, tv_incorrect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);

        // get variable from last intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            correctCount = extras.getInt("correctCount");
            wrongCount = extras.getInt("wrongCount");
        }

        bt_return = findViewById(R.id.bt_return);
        loadingPanel = findViewById(R.id.loadingPanel);
        ly = findViewById(R.id.ly_canvas);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_correct = findViewById(R.id.tv_legend_correct);
        tv_incorrect = findViewById(R.id.tv_legend_incorrect);

        drawChart();

    }

    // start async task
    private void drawChart() {
        DrawAsyncTask task = new DrawAsyncTask();
        task.execute(wrongCount, correctCount);
    }

    // draw pie chart async task
    private class DrawAsyncTask extends AsyncTask<Integer, Integer, PieChartView> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // loading animations
            loadingPanel.setVisibility(View.VISIBLE);
            ly.setVisibility(View.INVISIBLE);
            tv_correct.setVisibility(View.INVISIBLE);
            tv_incorrect.setVisibility(View.INVISIBLE);
        }

        @Override
        protected PieChartView doInBackground(Integer... integers) {
            integers[0] = correctCount;
            integers[1] = wrongCount;
            // create view
            PieChartView pieChartView = new PieChartView(getBaseContext(), wrongCount, correctCount);
            // delay
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return pieChartView;
        }

        @Override
        protected void onPostExecute(PieChartView p) {
            super.onPostExecute(p);
            // add pie chart view into linear layout
            ly.addView(p);
            // set percentage
            String acc = String.format("%.2f", (double) correctCount / (correctCount + wrongCount) * 100);
            tv_accuracy.setText("Attempt Accuracy: " + acc + "%");

            // element visibility
            loadingPanel.setVisibility(View.INVISIBLE);
            ly.setVisibility(View.VISIBLE);
            tv_correct.setVisibility(View.VISIBLE);
            tv_incorrect.setVisibility(View.VISIBLE);
            tv_correct.setText("Correct: " + correctCount);
            tv_incorrect.setText("Incorrect: " + wrongCount);
        }
    }

    // handle return button click
    public void onReturnClick(View v) {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    // return on back button pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}