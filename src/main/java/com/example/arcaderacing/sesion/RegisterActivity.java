package com.example.arcaderacing.sesion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.arcaderacing.R;
import com.example.arcaderacing.database.DatabaseHelper;

public class RegisterActivity extends AppCompatActivity {

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new DatabaseHelper(this);

        EditText user = findViewById(R.id.txtUser);
        EditText pass = findViewById(R.id.txtPass);
        Button btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {

            String u = user.getText().toString().trim();
            String p = pass.getText().toString().trim();

            if (u.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean ok = db.registerUser(u, p);

            if (ok) {
                Toast.makeText(this, "Usuario creado correctamente", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(this, "El usuario ya existe", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
