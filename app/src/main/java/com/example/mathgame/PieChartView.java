package com.example.mathgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

public class PieChartView extends View {

    // declare variable
    private float wrongPercentage;
    private float correctPercentage;

    // constructor
    public PieChartView(Context context, int wrongContent, int correctCount) {
        super(context);
        setPercentage(wrongContent, correctCount);
    }

    // color
    int rColor[] = {getResources().getColor(R.color.colorRed), getResources().getColor(R.color.colorGreen)};
    float cDegree = 0;

    // draw pie chart using RectF
    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
        float data[] = {wrongPercentage, correctPercentage};
        Paint paint = new Paint();

        paint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < data.length; i++) {
            float drawDegree = data[i] * 360 / 100;

            paint.setColor(rColor[i]);
            RectF rec = new RectF(0, 0, getHeight(), getWidth());

            c.drawArc(rec, cDegree, drawDegree, true, paint);
            cDegree += drawDegree;
        }
        invalidate();
    }

    // percentage setter
    public void setPercentage(float wrongCount, float correctCount) {
        this.wrongPercentage = wrongCount / (wrongCount + correctCount) * 100;
        this.correctPercentage = correctCount / (wrongCount + correctCount) * 100;
    }


}

