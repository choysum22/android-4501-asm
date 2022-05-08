package com.example.mathgame;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

public class StartButtonActivity extends AppCompatActivity implements OnDownloadFinishListener {

    public static Activity startBtnActivity;
    Button bt_start, bt_history;
    LinearLayout ly_timer;
    TextView tv_timer, tv_title;
    MyAsyncTask task = null;
    CountDownTimer cTimer = null;       // timer
    MediaPlayer mp;
    static SQLiteDatabase db;
    String sql;
    static String[] result;
    public static Intent starterIntent;

    public void updateDownloadResult(String[] result) {
        this.result = result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        starterIntent = getIntent();

        bt_start = findViewById(R.id.bt_start);
        bt_history = findViewById(R.id.bt_history);
        tv_timer = findViewById(R.id.tv_timer);
        tv_title = findViewById(R.id.tv_title);
        ly_timer = findViewById(R.id.ly_timer);

        // set sound effect
        mp = MediaPlayer.create(this,R.raw.count_down_beep);

        // set visibility
        refresh();

        startBtnActivity = this;
    }

    // handle start button click
    public void onStartClick(View v) {
        // set visibility
        bt_start.setVisibility(View.GONE);
        bt_history.setVisibility(View.GONE);
        tv_title.setVisibility(View.GONE);
        tv_timer.setVisibility(View.VISIBLE);
        ly_timer.setVisibility(View.VISIBLE);

        // fetch first question
        task = new MyAsyncTask();
        new MyAsyncTask(this).execute();

        // set-up database
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Context c = getApplicationContext();
                    File outFile = c.getDatabasePath("MathGameDB");
                    String outFileName = outFile.getPath();

                    // create database
                    db = SQLiteDatabase.openDatabase(outFileName, null, SQLiteDatabase.CREATE_IF_NECESSARY);
                    sql = "DROP TABLE IF EXISTS QuestionsLog;";
                    db.execSQL(sql);
                    sql = "CREATE TABLE IF NOT EXISTS GamesLog ('gameID' INTEGER PRIMARY KEY AUTOINCREMENT, playDate date, playTime time, duration double, correctCount int, wrongCount int);";
                    db.execSQL(sql);
                    sql = "CREATE TABLE QuestionsLog ('questionID' INTEGER PRIMARY KEY AUTOINCREMENT, question text, answer text, yourAnswer text);";
                    db.execSQL(sql);
                    db.close();

                } catch (SQLException e) {
                    Toast.makeText(StartButtonActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }).start();

        // start timer on count down
        startTimer();

    }

    // handle history button click - 3sec count down
    public void onHistoryClick(View v) {
        //initiate database if user is new player
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Context c = getApplicationContext();
                    File outFile = c.getDatabasePath("MathGameDB");
                    String outFileName = outFile.getPath();

                    // create database
                    db = SQLiteDatabase.openDatabase(outFileName, null, SQLiteDatabase.CREATE_IF_NECESSARY);
                    sql = "CREATE TABLE IF NOT EXISTS GamesLog ('gameID' INTEGER PRIMARY KEY AUTOINCREMENT, playDate date, playTime time, duration double, correctCount int, wrongCount int);";
                    db.execSQL(sql);
                    db.close();
                } catch (SQLException e) {
                    Toast.makeText(StartButtonActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }).start();

        // open record activity
        Intent i = new Intent(getApplicationContext(), RecordActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    // start timer count down function
    void startTimer() {
        cTimer = new CountDownTimer(2999, 1000) {
            public void onTick(long millisUntilFinished) {
                mp.start();
                tv_timer.setText(String.valueOf(Math.round(millisUntilFinished / 1000) + 1));
            }

            public void onFinish() {
                // end timer
                cancelTimer();

                // call main question answering activity
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
            }
        };
        cTimer.start();
    }


    //cancel timer
    void cancelTimer() {
        if (cTimer != null)
            cTimer.cancel();
    }

    public static String[] getQuestion() {
        return result;
    }

    // set visibility
    public void refresh() {
        bt_start.setVisibility(View.VISIBLE);
        bt_history.setVisibility(View.VISIBLE);
        tv_title.setVisibility(View.VISIBLE);
        tv_timer.setVisibility(View.GONE);
        ly_timer.setVisibility(View.GONE);
    }

    // get current time and date on start for record keeping
    public static Date getCurrentDateTime() {
        return Calendar.getInstance().getTime();
    }


}
