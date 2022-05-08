package com.example.mathgame;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.MediaParser;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnDownloadFinishListener {

    // declare variable
    EditText et_ans;
    TextView tv_result, tv_question, tv_count;
    Button bt_submit, bt_exit, bt_continue;
    LinearLayout ly_result;
    MyAsyncTask task = null;
    String[] result_first;
    FetchedQuestions result;
    LinkedList<FetchedQuestions> fetchedQuestionsList;
    MediaPlayer mp_right, mp_wrong, mp_right_l, mp_wrong_l;
    SQLiteDatabase db;
    private long startTime = 0;
    private long questionStartTime = 0;
    private double finishTime = 0.0;
    private int correctCount = 0;
    private int wrongCount = 0;
    private int totalCount = 0;

    // score variables
    private double score = 0;
    private final double avgMaxtime = 15; // The player has to completed the question within 15 seconds
    private final double questionScore = 50; // The player will be awarded of 50 points per remaining second

    public static Intent MainActivityIntent;
    public static Activity MainActivity;

    public void updateDownloadResult(String result[]) {
        this.result_first = result;
        FetchedQuestions fQuestion = new FetchedQuestions(result[0], result[1]);
        fetchedQuestionsList.add(fQuestion);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivityIntent = getIntent();

        fetchedQuestionsList = new LinkedList<>();

        MainActivity = this;

        et_ans = findViewById(R.id.et_ans);
        tv_result = findViewById(R.id.tv_result);
        ly_result = findViewById(R.id.ly_result);
        tv_question = findViewById(R.id.tv_question);
        tv_count = findViewById(R.id.tv_count);
        bt_submit = findViewById(R.id.bt_submit);
        bt_exit = findViewById(R.id.bt_exit);
        bt_continue = findViewById(R.id.bt_continue);

        //sound effect
        mp_right = MediaPlayer.create(this,R.raw.right_short);
        mp_wrong = MediaPlayer.create(this,R.raw.fail_short);
        mp_right_l = MediaPlayer.create(this,R.raw.right_long);
        mp_wrong_l = MediaPlayer.create(this,R.raw.fail_long);

        // reset variable
        correctCount = 0;
        wrongCount = 0;
        totalCount = 0;
        score = 0;
        result = null;

        // element visibility
        bt_exit.setVisibility(View.GONE);
        bt_continue.setVisibility(View.GONE);
        tv_result.setVisibility(View.GONE);
        ly_result.setVisibility(View.GONE);

        // start timer
        startTime = System.currentTimeMillis();
        questionStartTime = System.currentTimeMillis();

        db = StartButtonActivity.db;

        //get question and ans
        result_first = StartButtonActivity.getQuestion();
        FetchedQuestions fQuestion = new FetchedQuestions(result_first[0], result_first[1]);
        fetchedQuestionsList.add(fQuestion);
        fetchQuestion();

        // set first question
        result = fetchedQuestionsList.removeFirst();
        tv_question.setText("Question: " + result.getQuestion());
        tv_result.setText(result.getAnswer());


        // show keyboard
        showKb();
    }

    // handle submit button click function
    public void submitOnClick(View v) {
        // hide keyboard
        hideKb(v);
        // player answer
        String p_answer = et_ans.getText().toString();

        try {
            // handle empty input
            if (!p_answer.matches("")) {

                // if true, display correct answer and color, vice versa
                if (p_answer.matches(result.getAnswer())) {
                    mp_right.start();
                    tv_result.setText("Answer Correct : " + result.getAnswer());
                    tv_result.setTextColor(Color.parseColor("#6CF763"));
                    tv_result.setVisibility(View.VISIBLE);
                    ly_result.setVisibility(View.VISIBLE);
                    et_ans.setVisibility(View.GONE);
                    // add score if answered correct answer
                    score += (Math.max(0, avgMaxtime - questionTime(System.currentTimeMillis())) * (double)questionScore);
                    correctCount++;
                } else {
                    mp_wrong.start();
                    tv_result.setText("Answer Incorrect : " + result.getAnswer());
                    tv_result.setTextColor(Color.parseColor("#F77163"));
                    tv_result.setVisibility(View.VISIBLE);
                    ly_result.setVisibility(View.VISIBLE);
                    et_ans.setVisibility(View.GONE);
                    // score += 0;
                    wrongCount++;
                }
                totalCount++;
                tv_count.setText("Total count: " + totalCount);

                // insert ans and question into database
                InsertQuestionThread insertQuestionThread = new InsertQuestionThread(result.getQuestion(), result.getAnswer(), p_answer);
                insertQuestionThread.start();

                et_ans.setText("");

                // button visibility
                bt_submit.setVisibility(View.GONE);
                bt_exit.setVisibility(View.VISIBLE);
                bt_continue.setVisibility(View.VISIBLE);

                // fetch next question
                fetchQuestion();
                result = fetchedQuestionsList.removeFirst();

            } else {
                Toast.makeText(this, "Input Required", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    // handle exit button click function
    public void exitOnclick(View v) {
        try {
            // hide keyboard
            hideKb(v);
            // end timer
            finishTime = finishTime();

            if (wrongCount > correctCount) {
                mp_wrong_l.start();
            } else {
                mp_right_l.start();
            }

            // reset color
            tv_result.setTextColor(Color.parseColor("#B0BAC8"));

            // insert record into database
            InsertRecordThread insertRecordThread = new InsertRecordThread(finishTime, correctCount, wrongCount);
            insertRecordThread.start();

            Intent i = new Intent(this, ResultActivity.class);
            i.putExtra("finishTime",finishTime);
            i.putExtra("wrongCount",wrongCount);
            i.putExtra("correctCount",correctCount);
            i.putExtra("totalCount",totalCount);
            i.putExtra("score",score);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // handle continue button click function
    public void continueOnclick(View v) {

        try {
            // element visibility
            et_ans.setVisibility(View.VISIBLE);
            bt_submit.setVisibility(View.VISIBLE);
            bt_exit.setVisibility(View.GONE);
            bt_continue.setVisibility(View.GONE);
            tv_result.setVisibility(View.GONE);
            ly_result.setVisibility(View.GONE);

            // reset question timer to 0 for new question
            questionStartTime = System.currentTimeMillis();

            // display next question
            tv_result.setText("");
            tv_question.setText("Question: " + result.getQuestion());

            // show keyboard
            showKb();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // calculate elapsed time per question used for score calculation
    private double questionTime(long questionTime){
        long eTime = questionTime - questionStartTime;
        double elapsedTime = (eTime) / 1000.0;
        return elapsedTime;
    }

    // calculate elapsed time
    public double finishTime() {
        long finishTime = System.currentTimeMillis();
        long eTime = finishTime - startTime;
        double elapsedTime = (eTime) / 1000.0;
        return elapsedTime;
    }

    // focus answer text box for soft-keyboard to show up
    public void showKb() {
        et_ans.requestFocus();
        et_ans.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.showSoftInput(et_ans, 0);
            }
        }, 300);
    }

    // clear focus and hide soft-keyboard
    public void hideKb(View v) {
        et_ans.clearFocus();
        InputMethodManager imm = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
    }

    // fetch question from json in MyAsyncTask.java
    public void fetchQuestion() {
        task = new MyAsyncTask();
        new MyAsyncTask(this).execute();
    }

    // restart game on back button pressed
    @Override
    public void onBackPressed() {

        // finish previous start button activity
        StartButtonActivity.startBtnActivity.finish();
        overridePendingTransition(0, 0);
        finish();
        overridePendingTransition(0, 0);

        // start new button activity to restart the app
        Intent i = StartButtonActivity.starterIntent;
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    // insert question and ans into db
    private class InsertQuestionThread extends Thread {
        String question, answer, p_answer;

        // constructor
        InsertQuestionThread(String question, String answer, String p_answer) {
            this.question = question;
            this.answer = answer;
            this.p_answer = p_answer;
        }

        @Override
        public void run() {
            // initiate database
            Context c = getApplicationContext();
            File outFile = c.getDatabasePath("MathGameDB");
            String outFileName = outFile.getPath();
            db = SQLiteDatabase.openDatabase(outFileName, null, SQLiteDatabase.CREATE_IF_NECESSARY);
            // insert record
            db.execSQL("INSERT INTO QuestionsLog (question, answer, yourAnswer) values (\"" + question + "\"," + answer + "," + p_answer + ");");
            db.close();
        }
    }

    // insert record into db
    private class InsertRecordThread extends Thread {
        double finishTime;
        int correctCount, wrongCount;

        // constructor
        InsertRecordThread(double finishTime, int correctCount, int wrongCount) {
            this.finishTime = finishTime;
            this.correctCount = correctCount;
            this.wrongCount = wrongCount;
        }

        @Override
        public void run() {
            // initiate database
            Context c = getApplicationContext();
            File outFile = c.getDatabasePath("MathGameDB");
            String outFileName = outFile.getPath();

            // get date and time
            Date current = StartButtonActivity.getCurrentDateTime();
            SimpleDateFormat sDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String shortDate = sDate.format(current);   // date
            SimpleDateFormat sTime = new SimpleDateFormat("HH:mm:ss aa", Locale.getDefault());
            String shortTime = sTime.format(current);   // time

            // insert ans and question into database
            db = SQLiteDatabase.openDatabase(outFileName, null, SQLiteDatabase.CREATE_IF_NECESSARY);
            db.execSQL("INSERT INTO GamesLog (playDate,playTime,duration, correctCount, wrongCount) " +
                    "values (\'" + shortDate + "\',\'" + shortTime + "\'," +
                    finishTime + "," + correctCount + "," + wrongCount + ");");
            db.close();
        }
    }

}
