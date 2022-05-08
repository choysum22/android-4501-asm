package com.example.mathgame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;


public class RecordListAdapter extends ArrayAdapter<Record> {

    // list adapter
    public RecordListAdapter(Context context, ArrayList<Record> recordArrayList) {

        super(context, R.layout.list_item_record, recordArrayList);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Record record = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_record, parent, false);
        }

        // find element by id
        TextView tv_gid = convertView.findViewById(R.id.tv_gid);
        TextView tv_datetime = convertView.findViewById(R.id.tv_datetime);
        TextView tv_duration = convertView.findViewById(R.id.tv_duration);
        TextView tv_correctCount = convertView.findViewById(R.id.tv_correctCount);
        TextView tv_wrongCount = convertView.findViewById(R.id.tv_wrongCount);

        // set text to item
        tv_gid.setText("Attempt: " + record.gameID);
        tv_datetime.setText("Date: " + record.date + " | Time: " + record.time);
        tv_duration.setText("Duration: " + record.duration + "s");
        tv_correctCount.setText("Correct Count: " + record.correctCount);
        tv_wrongCount.setText("Wrong Count: " + record.wrongCount);

        return convertView;
    }
}
