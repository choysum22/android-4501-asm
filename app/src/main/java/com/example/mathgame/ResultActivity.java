package com.example.mathgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class ResultActivity extends AppCompatActivity {

    TextView tv_totalCount, tv_percentage, tv_result_correct, tv_result_wrong, tv_timer, tv_avg, tv_score;
    ProgressBar stats_wrong;       // pie chart
    private int correctCount, wrongCount, totalCount;
    private double finishTime, wrongPercentage, correctPercentage, score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            correctCount = extras.getInt("correctCount");
            wrongCount = extras.getInt("wrongCount");
            totalCount = extras.getInt("totalCount");
            score = extras.getDouble("score");
            finishTime = extras.getDouble("finishTime");
        }

        stats_wrong = findViewById(R.id.stats_wrong);
        tv_totalCount = findViewById(R.id.tv_totalCount);
        tv_percentage = findViewById(R.id.tv_number_of_answers);
        tv_result_correct = findViewById(R.id.tv_result_correct);
        tv_result_wrong = findViewById(R.id.tv_result_wrong);
        tv_timer = findViewById(R.id.tv_timer);
        tv_avg = findViewById(R.id.tv_avg);
        tv_score = findViewById(R.id.tv_score);

        tv_totalCount.setText("Total no. of Questions: " + totalCount);
        tv_result_correct.setText("Correct: " + correctCount);
        tv_result_wrong.setText("Incorrect: " + wrongCount);
        tv_timer.setText("Total Time: " + finishTime + "s");
        tv_avg.setText("Average Time per Question: " + String.format("%.2f", (finishTime / totalCount)) + "s");
        // final score = score * correct percentage
        tv_score.setText("Score: "+ (int)(score*((double)correctCount/(double)totalCount)));

        // start thread
        setViewValues();
    }

    // view values to show result
    private void setViewValues() {
        calculatePercentages(wrongCount, correctCount, totalCount);

        // delay of 650 millis
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ProgressBarAsyncTask progressBarAsyncTask = new ProgressBarAsyncTask(ResultActivity.this);
                progressBarAsyncTask.execute((int) (correctPercentage * 100));
            }
        }, 650);
    }

    // thread for progress bar animation
    private class ProgressBarAsyncTask extends AsyncTask<Integer, Integer, Void> {
        private WeakReference<ResultActivity> activityWeakReference;

        // prevent memory leak
        ProgressBarAsyncTask(ResultActivity activity) {
            activityWeakReference = new WeakReference<ResultActivity>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ResultActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            tv_percentage.setText("0.00%");
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            for (int i = 0; i <= integers[0]; i++) {
                publishProgress(i);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            ResultActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            // changes progress bar
            activity.stats_wrong.setProgress(values[0]);
            tv_percentage.setText(String.format("%.2f", (double) values[0]) + "%");
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            ResultActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            // set final percentage
            tv_percentage.setText(String.format("%.2f", correctPercentage * 100) + "%");
        }
    }

    // calculate percentages
    private void calculatePercentages(int wrongCount, int correctCount, int totalCount) {
        wrongPercentage = (double) wrongCount / (double) totalCount;    // percentage of wrong answer/total count
        correctPercentage = (double) correctCount / (double) totalCount;    // percentage of correct answer/total count
    }

    // handle retry button click function
    public void retryOnClick(View v) {
        // finish start button activity
        StartButtonActivity.startBtnActivity.finish();
        overridePendingTransition(0, 0);
        MainActivity.MainActivity.finish();
        overridePendingTransition(0, 0);
        finish();
        overridePendingTransition(0, 0);


        // start start button activity
        Intent i = StartButtonActivity.starterIntent;
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    // handle history button click function
    public void historyOnClick(View v) {
        // open record activity
        Intent i = new Intent(getApplicationContext(), RecordActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    // handle question button click function
    public void questionOnClick(View v) {
        // open question activity
        Intent i = new Intent(getApplicationContext(), QuestionActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    // restart game on back button pressed
    @Override
    public void onBackPressed() {

        // finish previous start button activity
        StartButtonActivity.startBtnActivity.finish();
        overridePendingTransition(0, 0);
        MainActivity.MainActivity.finish();
        overridePendingTransition(0, 0);
        finish();
        overridePendingTransition(0, 0);

        // start new button activity to restart the app
        Intent i = StartButtonActivity.starterIntent;
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}