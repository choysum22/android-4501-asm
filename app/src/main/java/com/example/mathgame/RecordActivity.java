package com.example.mathgame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.mathgame.databinding.ActivityRecordBinding;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class RecordActivity extends AppCompatActivity {

    // declare binding
    ActivityRecordBinding binding;
    // declare db
    SQLiteDatabase db;
    String sql;
    Cursor cursor = null;
    float data[] = new float[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // open thread
        query();
    }


    // query question data from database and fill listview
    public void query() {
        binding.listview.setVisibility(View.GONE);
        binding.button2.setVisibility(View.INVISIBLE);
        // delay of 1500 millis
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                QueryThreadTask queryThreadTask = new QueryThreadTask(RecordActivity.this);
                queryThreadTask.execute();
            }
        }, 1800);
    }

    // async task to get data to fill listview
    private class QueryThreadTask extends AsyncTask<Integer, Integer, ArrayList<Record>> {
        private WeakReference<RecordActivity> activityWeakReference;

        // prevent memory leak
        QueryThreadTask(RecordActivity activity) {
            activityWeakReference = new WeakReference<RecordActivity>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            RecordActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            binding.listview.setVisibility(View.GONE);
            binding.loadingPanel.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Record> doInBackground(Integer... integers) {
            ArrayList<Record> recordArrayList = null;
            Context c = getApplicationContext();
            File outFile = c.getDatabasePath("MathGameDB");
            String outFileName = outFile.getPath();

            db = SQLiteDatabase.openDatabase(outFileName, null, SQLiteDatabase.OPEN_READONLY);

            // record list
            recordArrayList = new ArrayList<>();

            cursor = db.rawQuery("SELECT * FROM GamesLog ORDER BY gameID DESC", null);
            while (cursor.moveToNext()) {
                int gid = cursor.getInt(cursor.getColumnIndex("gameID"));
                String playDate = cursor.getString(cursor.getColumnIndex("playDate"));
                String playTime = cursor.getString(cursor.getColumnIndex("playTime"));
                double duration = cursor.getDouble(cursor.getColumnIndex("duration"));
                int correctCount = cursor.getInt(cursor.getColumnIndex("correctCount"));
                int wrongCount = cursor.getInt(cursor.getColumnIndex("wrongCount"));
                Record r = new Record(gid, playDate, playTime, duration, correctCount, wrongCount);
                recordArrayList.add(r);
            }
            db.close();
            return recordArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<Record> values) {
            super.onPostExecute(values);

            RecordActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            // set adapter
            RecordListAdapter listAdapter = new RecordListAdapter(RecordActivity.this, values);
            binding.listview.setAdapter(listAdapter);
            binding.listview.setClickable(true);

            // onclick listener for listview item
            binding.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent i = new Intent(RecordActivity.this, PieChartActivity.class);
                    i.putExtra("correctCount", values.get(position).correctCount);
                    i.putExtra("wrongCount", values.get(position).wrongCount);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });

            // ui changes
            binding.listview.setVisibility(View.VISIBLE);
            binding.button2.setVisibility(View.VISIBLE);
            binding.loadingPanel.setVisibility(View.GONE);
            binding.title.setText(R.string.past_record);
        }
    }

    // handle return button click
    public void onReturnClick(View v) {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    // handle reset button click
    public void onResetClick(View v) {
        // alert dialog for confirmation on resetting record
        AlertDialog.Builder resetAlert = new AlertDialog.Builder(this);
        resetAlert.setTitle("Reset Record");
        resetAlert.setMessage("Are you sure you want to reset all record");
        // reset
        resetAlert.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //initiate database if user is new player
                        Context c = getApplicationContext();
                        File outFile = c.getDatabasePath("MathGameDB");
                        String outFileName = outFile.getPath();

                        // delete database
                        db = SQLiteDatabase.openDatabase(outFileName, null, SQLiteDatabase.CREATE_IF_NECESSARY);
                        sql = "DROP TABLE IF EXISTS GamesLog;";
                        db.execSQL(sql);
                        db.close();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // finish main activity
                                if (MainActivity.MainActivity != null) {
                                    MainActivity.MainActivity.finish();
                                    overridePendingTransition(0, 0);
                                    finish();
                                    overridePendingTransition(0, 0);
                                }

                                // finish start button activity
                                if (StartButtonActivity.startBtnActivity != null) {
                                    StartButtonActivity.startBtnActivity.finish();
                                    overridePendingTransition(0, 0);
                                    finish();
                                    overridePendingTransition(0, 0);
                                }

                                // close activity
                                finish();
                                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                                // start start button activity
                                Intent i = StartButtonActivity.starterIntent;
                                startActivity(i);
                                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                            }
                        });
                    }
                }).start();

            }
        });

        // cancel
        resetAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
            }
        });

        resetAlert.show();
    }

    // return on back button pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
