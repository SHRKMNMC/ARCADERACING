package com.example.arcaderacing.scores;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.arcaderacing.R;
import com.example.arcaderacing.database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Scores2048Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores2048);

        ListView list = findViewById(R.id.listScores);
        DatabaseHelper db = new DatabaseHelper(this);

        Cursor c = db.getScores2048();
        ArrayList<ScoreItem> scores = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());

        while (c.moveToNext()) {
            String user = c.getString(c.getColumnIndexOrThrow("username"));
            int score = c.getInt(c.getColumnIndexOrThrow("score"));
            long date = Long.parseLong(c.getString(c.getColumnIndexOrThrow("date")));

            scores.add(new ScoreItem(
                    user,
                    score,
                    sdf.format(new Date(date))
            ));
        }

        ScoreAdapter adapter = new ScoreAdapter(this, scores);
        list.setAdapter(adapter);
    }
}
