package com.example.arcaderacing.sesion;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.arcaderacing.R;

public class SplashActivity extends AppCompatActivity {

    private ImageView logo;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logo = findViewById(R.id.splash);
        title = findViewById(R.id.splashTitle);

        startAnimation();
    }

    private void startAnimation() {

        // Rotación suave del logo
        ObjectAnimator rotate =
                ObjectAnimator.ofFloat(logo, "rotation", 0f, 360f);
        rotate.setDuration(1500);

        // Fade-in del texto
        ObjectAnimator fadeIn =
                ObjectAnimator.ofFloat(title, "alpha", 0f, 1f);
        fadeIn.setDuration(1000);

        // Pequeña pausa (mantener visible)
        ObjectAnimator hold =
                ObjectAnimator.ofFloat(title, "alpha", 1f, 1f);
        hold.setDuration(500);

        // Fade-out del texto
        ObjectAnimator fadeOut =
                ObjectAnimator.ofFloat(title, "alpha", 1f, 0f);
        fadeOut.setDuration(800);

        // Secuencia completa
        AnimatorSet set = new AnimatorSet();
        set.playSequentially(rotate, fadeIn, hold, fadeOut);
        set.start();

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                startActivity(new Intent(SplashActivity.this, StartActivity.class));
                finish();
            }
        });
    }
}
