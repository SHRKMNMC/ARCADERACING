package com.example.arcaderacing.sesion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.arcaderacing.MainActivity;
import com.example.arcaderacing.R;
import com.example.arcaderacing.database.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new DatabaseHelper(this);

        EditText user = findViewById(R.id.txtUser);
        EditText pass = findViewById(R.id.txtPass);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {

            String u = user.getText().toString().trim();
            String p = pass.getText().toString().trim();

            if (u.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (db.loginUser(u, p)) {

                SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
                prefs.edit()
                        .putBoolean("logged", true)
                        .putString("username", u)
                        .apply();

                Toast.makeText(this, "Bienvenido " + u, Toast.LENGTH_SHORT).show();

                startActivity(new Intent(this, MainActivity.class));
                finish();

            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
