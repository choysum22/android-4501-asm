package com.example.mathgame;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;


public class QuestionListAdapter extends ArrayAdapter<Question> {

    // list adapter
    public QuestionListAdapter(Context context, ArrayList<Question> questionArrayList) {

        super(context, R.layout.list_item_question, questionArrayList);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Question question = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_question, parent, false);
        }

        // find element by id
        TextView tv_qid = convertView.findViewById(R.id.tv_qid);
        TextView tv_question = convertView.findViewById(R.id.tv_question);
        TextView tv_answer = convertView.findViewById(R.id.tv_answer);
        TextView tv_yourAnswer = convertView.findViewById(R.id.tv_yourAnswer);
        TextView tv_correctness = convertView.findViewById(R.id.tv_correctness);

        // set text to item
        tv_qid.setText("Question " + question.questionID);
        tv_question.setText("Question: " + question.question);
        tv_answer.setText("Answer: " + question.answer);
        tv_yourAnswer.setText("Your Answer: " + question.yourAnswer);
        tv_correctness.setText(question.correctness);
        if (question.correctness.equals("Correct")) {
            tv_correctness.setTextColor(Color.parseColor("#6CF763"));
        } else {
            tv_correctness.setTextColor(Color.parseColor("#F77163"));
        }

        return convertView;
    }
}
