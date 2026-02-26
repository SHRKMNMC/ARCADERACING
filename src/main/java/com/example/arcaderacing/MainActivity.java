package com.example.arcaderacing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.arcaderacing.dosmilcuarentayocho.Dosmilcuarentayocho;
import com.example.arcaderacing.racing.Racing;
import com.example.arcaderacing.scores.Scores2048Activity;
import com.example.arcaderacing.scores.ScoresRacingActivity;
import com.example.arcaderacing.sesion.StartActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ============================
        //   CONTROL DE SESIÓN
        // ============================
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        boolean logged = prefs.getBoolean("logged", false);

        if (!logged) {
            startActivity(new Intent(this, StartActivity.class));
            finish();
            return;
        }

        // ============================
        //   MENÚ PRINCIPAL
        // ============================
        setContentView(R.layout.activity_main);

        ListView menuList = findViewById(R.id.menuList);

        String[] opciones = {
                "RACING",
                "2048",
                "Scores 2048",
                "Scores Racing"
        };

        int[] iconos = {
                R.drawable.ic_racing,
                R.drawable.ic_2048,
                R.drawable.ic_scores,
                R.drawable.ic_scores
        };

        MenuAdapter adapter = new MenuAdapter(this, opciones, iconos);
        menuList.setAdapter(adapter);

        menuList.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {

                case 0:
                    startActivity(new Intent(MainActivity.this, Racing.class));
                    break;

                case 1:
                    startActivity(new Intent(MainActivity.this, Dosmilcuarentayocho.class));
                    break;

                case 2:
                    startActivity(new Intent(MainActivity.this, Scores2048Activity.class));
                    break;

                case 3:
                    startActivity(new Intent(MainActivity.this, ScoresRacingActivity.class));
                    break;
            }
        });
    }
}
