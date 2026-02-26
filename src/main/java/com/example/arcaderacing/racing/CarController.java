package com.example.arcaderacing.racing;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

public class CarController {

    private ImageView car;
    private float[] lanes;
    private int currentLane;

    private float speedFactor = 1f;

    private static final long ANIM_DURATION = 180;
    private static final float TILT_ANGLE = 12f;

    public CarController(ImageView car, float[] lanes, int startLane) {
        this.car = car;
        this.lanes = lanes;
        this.currentLane = startLane;

        // Centrar coche en carril inicial
        float targetX = lanes[currentLane] - car.getWidth() / 2f;
        car.setX(targetX);
    }

    public void moveLeft() {
        if (currentLane > 0) {
            currentLane--;
            animateToLane(-1); // -1 = movimiento hacia la izquierda
        }
    }

    public void moveRight() {
        if (currentLane < lanes.length - 1) {
            currentLane++;
            animateToLane(1); // 1 = movimiento hacia la derecha
        }
    }

    private void animateToLane(int direction) {

        float targetX = lanes[currentLane] - car.getWidth() / 2f;

        // Animación de desplazamiento lateral
        ObjectAnimator moveAnim = ObjectAnimator.ofFloat(car, "x", car.getX(), targetX);
        moveAnim.setDuration(ANIM_DURATION);
        moveAnim.setInterpolator(new DecelerateInterpolator());

        // Inclinación hacia el lado del movimiento
        float tilt = direction * TILT_ANGLE;
        ObjectAnimator tiltAnim = ObjectAnimator.ofFloat(car, "rotation", 0f, tilt);
        tiltAnim.setDuration(ANIM_DURATION / 2);

        // Volver a posición recta
        ObjectAnimator straightenAnim = ObjectAnimator.ofFloat(car, "rotation", tilt, 0f);
        straightenAnim.setDuration(ANIM_DURATION / 2);

        // Secuencia: inclinar → mover → enderezar
        AnimatorSet set = new AnimatorSet();
        set.play(moveAnim).with(tiltAnim);
        set.play(straightenAnim).after(tiltAnim);

        set.start();
    }

    public void setBraking(boolean braking) {
        speedFactor = braking ? 0.40f : 1f;
    }

    public float getSpeedFactor() {
        return speedFactor;
    }

    public int getCurrentLane() {
        return currentLane;
    }
}
