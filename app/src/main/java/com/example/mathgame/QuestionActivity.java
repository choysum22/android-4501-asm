package com.example.mathgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.example.mathgame.databinding.ActivityQuestionBinding;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class QuestionActivity extends AppCompatActivity {

    ActivityQuestionBinding binding;
    SQLiteDatabase db;
    Cursor cursor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // start thread
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                query();
            }
        }, 1200);
    }

    // query question data from database
    public void query() {
        QueryThreadTask queryThreadTask = new QueryThreadTask(QuestionActivity.this);
        queryThreadTask.execute();
    }

    // async task to get data to fill listview
    private class QueryThreadTask extends AsyncTask<Integer, Integer, ArrayList<Question>> {
        private WeakReference<QuestionActivity> activityWeakReference;

        // prevent memory leak
        QueryThreadTask(QuestionActivity activity) {
            activityWeakReference = new WeakReference<QuestionActivity>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            QuestionActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            binding.listview.setVisibility(View.GONE);
            binding.loadingPanel.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Question> doInBackground(Integer... integers) {
            ArrayList<Question> questionArrayList = null;
            try {
                Context c = getApplicationContext();
                File outFile = c.getDatabasePath("MathGameDB");
                String outFileName = outFile.getPath();

                db = SQLiteDatabase.openDatabase(outFileName, null, SQLiteDatabase.OPEN_READONLY);

                cursor = db.rawQuery("SELECT * FROM QuestionsLog", null);

                questionArrayList = new ArrayList<>();

                while (cursor.moveToNext()) {
                    int qid = cursor.getInt(cursor.getColumnIndex("questionID"));
                    String question = cursor.getString(cursor.getColumnIndex("question"));
                    int answer = cursor.getInt(cursor.getColumnIndex("answer"));
                    int yourAnswer = cursor.getInt(cursor.getColumnIndex("yourAnswer"));
                    Question q = new Question(qid, question, answer, yourAnswer);
                    questionArrayList.add(q);
                }
                db.close();


            } catch (SQLException e) {
                Toast.makeText(QuestionActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
            return questionArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<Question> values) {
            super.onPostExecute(values);

            QuestionActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            // set adapter
            QuestionListAdapter listAdapter = new QuestionListAdapter(QuestionActivity.this, values);
            binding.listview.setAdapter(listAdapter);
            binding.listview.setClickable(false);

            // ui changes
            binding.listview.setVisibility(View.VISIBLE);
            binding.loadingPanel.setVisibility(View.GONE);
            binding.title.setText(R.string.question_list_from_last_try);
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
