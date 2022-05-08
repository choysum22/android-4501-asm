package com.example.mathgame;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyAsyncTask extends AsyncTask<String, Integer, String[]> {

    // declare variable
    private OnDownloadFinishListener listener;
    private final String TAG = "?";
    private final String jsonUrl = "https://2vtyxazuaa.execute-api.us-east-1.amazonaws.com/default/ITP4501AssignmentAPI";
    private String question[] = new String[2];

    // constructor
    public MyAsyncTask() {

    }

    // listener
    public MyAsyncTask(OnDownloadFinishListener listener) {
        this.listener = listener;
    }

    @Override
    protected String[] doInBackground(String... strings) {
        // use input stream to get data from url
        InputStream inputStream = null;
        String result = "";
        try {
            //fetch json from url
            URL url = new URL(jsonUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();

            inputStream = con.getInputStream();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = buffer.readLine()) != null) {
                result += line;
            }

            Log.d(TAG, "DEBUG");
            inputStream.close();

            //convert to data
            JSONObject jObj = new JSONObject(result);
            String question = jObj.getString("question");
            int ans = jObj.getInt("answer");

            this.question[0] = question;
            this.question[1] = String.valueOf(ans);
        } catch (Exception e) {
            this.question[0] = e.getMessage();
            this.question[1] = e.getMessage();
        }

        return this.question;
    }

    // get data on finish
    @Override
    protected void onPostExecute(String[] s) {
        super.onPostExecute(s);
        listener.updateDownloadResult(s);
    }
}
