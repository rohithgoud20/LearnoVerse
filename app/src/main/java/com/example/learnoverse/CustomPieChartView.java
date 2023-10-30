package com.example.learnoverse;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

public class CustomPieChartView extends View {

    private Paint paint;
    private float newGoalCount = 0;
    private float inProgressCount = 0;

    public CustomPieChartView(Context context) {
        super(context);
        init();
    }

    public CustomPieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
    }

    public void setChartData(float newGoalCount, float inProgressCount) {
        this.newGoalCount = newGoalCount;
        this.inProgressCount = inProgressCount;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float total = newGoalCount + inProgressCount;
        float newGoalSweepAngle = (newGoalCount / total) * 360;
        float inProgressSweepAngle = (inProgressCount / total) * 360;

        float centerX = getWidth() / 2;
        float centerY = getHeight() / 2;
        float radius = Math.min(centerX, centerY) * 0.8f;

//        int customPastelBlueColor = ContextCompat.getColor(context, R.color.blue);
//        int customPastelGreenColor = ContextCompat.getColor(context, R.color.green);
        int customPastelBlueColor = getResources().getColor(R.color.blue);
        int customPastelGreenColor = getResources().getColor(R.color.green);

        // Draw "New Goals" slice
        paint.setColor(customPastelBlueColor);
        canvas.drawArc(centerX - radius, centerY - radius, centerX + radius, centerY + radius, 0, newGoalSweepAngle, true, paint);

        // Draw "In Progress" slice
        paint.setColor(customPastelGreenColor);
        canvas.drawArc(centerX - radius, centerY - radius, centerX + radius, centerY + radius, newGoalSweepAngle, inProgressSweepAngle, true, paint);
    }


}
