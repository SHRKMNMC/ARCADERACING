package com.example.arcaderacing.racing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class RoadView extends View {

    private Paint roadPaint;
    private Paint linePaint;

    private int offset = 0;
    private int speed = 40;  // velocidad base de la carretera

    // Factor global de frenado (1 = normal, <1 = frenando)
    private float globalFactor = 1f;

    public RoadView(Context context, AttributeSet attrs) {
        super(context, attrs);

        roadPaint = new Paint();
        roadPaint.setColor(Color.DKGRAY);

        linePaint = new Paint();
        linePaint.setColor(Color.WHITE);
        linePaint.setStrokeWidth(12);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        // Fondo gris oscuro
        canvas.drawRect(0, 0, width, height, roadPaint);

        // Líneas discontinuas
        int lineHeight = 120;
        int space = 420;

        float firstLineX  = width / 4f;
        float secondLineX = width / 2f;
        float thirdLineX  = 3 * width / 4f;

        for (int y = offset; y < height; y += lineHeight + space) {
            canvas.drawLine(firstLineX,  y, firstLineX,  y + lineHeight, linePaint);
            canvas.drawLine(secondLineX, y, secondLineX, y + lineHeight, linePaint);
            canvas.drawLine(thirdLineX,  y, thirdLineX,  y + lineHeight, linePaint);
        }

        // ---- ANIMACIÓN ----
        offset += speed * globalFactor;

        if (offset > lineHeight + space) {
            offset = 0;
        }

        invalidate(); // redibujar siempre
    }

    // --------------------------
    //     SETTERS EXTERNOS
    // --------------------------

    public void setGlobalSpeedFactor(float factor) {
        this.globalFactor = factor;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
