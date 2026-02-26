package com.example.arcaderacing.racing;

import android.content.Context;
import android.os.Handler;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.arcaderacing.R;

import java.util.List;
import java.util.Random;

public class Obstacle {

    private ImageView sprite;
    private float[] lanes;
    private int screenHeight;

    private int baseSpeed = 25;
    protected int speed;

    private int laneIndex;

    private final Handler handler = new Handler();
    private final Random random = new Random();

    private Context context;
    private ConstraintLayout parent;

    // FLASH
    private boolean flashActive = false;
    private long flashEndTime = 0;
    private Obstacle frontDuringFlash = null;

    private static final int FLASH_DURATION_MS = 2000;
    private static final float FLASH_UP_SPEED = 55f;
    private static final float SAFETY_DISTANCE = 380f;

    public Obstacle(Context context,
                    ConstraintLayout parent,
                    float[] lanePositions,
                    int screenHeight,
                    int offsetY) {

        this.context = context;
        this.parent = parent;
        this.lanes = lanePositions;
        this.screenHeight = screenHeight;

        sprite = new ImageView(context);
        sprite.setImageResource(getRandomCarSprite());
        sprite.setLayoutParams(new ConstraintLayout.LayoutParams(210, 310));

        speed = baseSpeed + random.nextInt(10);

        parent.addView(sprite);

        sprite.post(() -> resetPosition(offsetY));

        startFalling();
    }

    private int getRandomCarSprite() {
        int r = random.nextInt(3);
        switch (r) {
            case 0: return R.drawable.car2;
            case 1: return R.drawable.car3;
            default: return R.drawable.car4;
        }
    }

    private void resetPosition(int startY) {

        laneIndex = random.nextInt(lanes.length);

        float spriteWidth = sprite.getWidth();
        float laneCenterX = lanes[laneIndex];

        float finalX = laneCenterX - (spriteWidth / 2f);

        sprite.setX(finalX);
        sprite.setY(startY);
    }

    private void startFalling() {
        handler.post(new Runnable() {
            @Override
            public void run() {

                float realSpeed = speed;

                if (flashActive) {

                    float myY = sprite.getY();

                    if (frontDuringFlash != null) {
                        float frontY = frontDuringFlash.getSprite().getY();
                        // delante en la carretera = más arriba = Y menor
                        // queremos quedarnos justo DETRÁS → un poco por debajo
                        float targetY = frontY + SAFETY_DISTANCE;

                        if (myY >= targetY) {
                            realSpeed = 0f; // ya está justo detrás
                        } else {
                            float desiredMove = -FLASH_UP_SPEED; // subir
                            float maxAllowedMove = targetY - myY; // positivo

                            // subir pero sin pasar del punto de seguridad
                            if (myY + desiredMove < targetY) {
                                realSpeed = Math.max(desiredMove, -maxAllowedMove);
                            } else {
                                realSpeed = desiredMove;
                            }
                        }
                    } else {
                        // sin coche delante → subir libremente
                        realSpeed = -FLASH_UP_SPEED;
                    }

                    if (System.currentTimeMillis() > flashEndTime) {
                        flashActive = false;
                        frontDuringFlash = null;
                    }

                } else {
                    // anti-solape normal solo cuando cae
                    if (context instanceof Racing) {

                        Racing main = (Racing) context;

                        for (Obstacle other : main.getObstacles()) {
                            if (other == Obstacle.this) continue;
                            if (other.laneIndex != laneIndex) continue;

                            float myY = sprite.getY();
                            float otherY = other.getSprite().getY();

                            // el que está "delante" cayendo es el que tiene Y mayor
                            if (otherY > myY) {

                                float predictedNextY = myY + realSpeed;

                                if (predictedNextY > otherY - SAFETY_DISTANCE) {
                                    realSpeed = Math.max(0,
                                            (otherY - SAFETY_DISTANCE - myY)
                                    );
                                }
                            }
                        }
                    }
                }

                sprite.setY(sprite.getY() + realSpeed);

                if (sprite.getY() > screenHeight) {
                    resetPosition(-600 - random.nextInt(600));
                }

                if (context instanceof Racing) {
                    ((Racing) context).checkCollision(sprite);
                }

                handler.postDelayed(this, 16);
            }
        });
    }

    // ACTIVAR FLASH: subir 2s, y si hay coche delante, quedarse justo detrás
    public boolean applyFlashEffect(List<Obstacle> all) {

        float myY = sprite.getY();
        Obstacle nearestFront = null;
        float maxY = -Float.MAX_VALUE;

        // coche "delante" de este obstáculo = mismo carril, Y menor pero lo más grande posible
        for (Obstacle o : all) {
            if (o == this) continue;
            if (o.laneIndex != laneIndex) continue;

            float oy = o.getSprite().getY();

            if (oy < myY && oy > maxY) {
                maxY = oy;
                nearestFront = o;
            }
        }

        frontDuringFlash = nearestFront;
        flashActive = true;
        flashEndTime = System.currentTimeMillis() + FLASH_DURATION_MS;

        return true;
    }

    public ImageView getSprite() { return sprite; }
    public int getLaneIndex() { return laneIndex; }
}
