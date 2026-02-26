package com.example.arcaderacing.dosmilcuarentayocho;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.arcaderacing.R;
import com.example.arcaderacing.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.Random;

public class Dosmilcuarentayocho extends AppCompatActivity {

    // ============================
    //   CONFIGURACIÓN DEL JUEGO
    // ============================
    private static final int SIZE = 4;
    private static final int SWIPE_THRESHOLD = 120;
    private static final long GAME_TIME_MS = 5 * 60 * 1000;

    // ============================
    //   TABLERO Y UI
    // ============================
    private int[][] tablero = new int[SIZE][SIZE];
    private GridLayout gridLayout;
    private TextView draggableTile;
    private FrameLayout rootLayout;

    // ============================
    //   DRAG
    // ============================
    private float rawStartX, rawStartY;
    private boolean dragging = false;

    //   ULTIMA JUGADA

    private int[][] previousBoard = new int[SIZE][SIZE];
    private int previousScore = 0;
    private boolean canUndo = false;


    // ============================
    //   SCORE + TIMER
    // ============================
    private TextView txtScore, txtTimer;
    private int score = 0;
    private CountDownTimer timer;

    // Para evitar guardar dos veces
    private boolean gameEnded = false;

    // ============================
    //   BASE DE DATOS
    // ============================
    private DatabaseHelper db;
    private String username;

    // ============================
    //   CICLO DE VIDA
    // ============================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dosmilcuarentayocho);

        gridLayout = findViewById(R.id.gridLayout);
        draggableTile = findViewById(R.id.draggableTile);
        rootLayout = findViewById(R.id.rootLayout);
        txtScore = findViewById(R.id.txtScore);
        txtTimer = findViewById(R.id.txtTimer);

        findViewById(R.id.btnUndo).setOnClickListener(v -> deshacerMovimiento());


        db = new DatabaseHelper(this);
        username = getSharedPreferences("session", MODE_PRIVATE)
                .getString("username", "Anon");

        iniciarTablero();
        addNewNumber();
        addNewNumber();
        updateUI();

        startGameTimer();
    }

    // ============================
    //   DETENER TIMER AL SALIR
    // ============================
    @Override
    protected void onPause() {
        super.onPause();
        stopTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    // ============================
    //   TEMPORIZADOR
    // ============================
    private void startGameTimer() {
        timer = new CountDownTimer(GAME_TIME_MS, 1000) {
            @Override
            public void onTick(long ms) {
                long s = ms / 1000;
                txtTimer.setText("Tiempo: " + (s / 60) + ":" + String.format("%02d", s % 60));
            }

            @Override
            public void onFinish() {
                endGame("⏳ Tiempo agotado");
            }
        }.start();
    }

    // ============================
    //   FIN DE PARTIDA
    // ============================
    private void endGame(String reason) {

        if (gameEnded) return; // evitar doble guardado
        gameEnded = true;

        stopTimer();

        db.saveScore2048(username, score);

        Toast.makeText(this, reason + " — Puntuación: " + score, Toast.LENGTH_LONG).show();
        finish();
    }

    // ============================
    //   SCORE
    // ============================
    private void addScore(int value) {
        score += value;
        txtScore.setText("Puntos: " + score);
    }

    // ============================
    //   TABLERO
    // ============================
    private void iniciarTablero() {
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                tablero[i][j] = 0;
    }

    private void addNewNumber() {
        ArrayList<int[]> libres = new ArrayList<>();

        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                if (tablero[i][j] == 0)
                    libres.add(new int[]{i, j});

        if (libres.isEmpty()) return;

        int[] celda = libres.get(new Random().nextInt(libres.size()));
        tablero[celda[0]][celda[1]] = (new Random().nextInt(10) < 9) ? 2 : 4;
    }

    private void updateUI() {
        gridLayout.removeAllViews();

        int cellSize = (int) (80 * getResources().getDisplayMetrics().density);

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {

                TextView cell = new TextView(this);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = cellSize;
                params.height = cellSize;
                params.setMargins(8, 8, 8, 8);
                cell.setLayoutParams(params);

                cell.setGravity(android.view.Gravity.CENTER);
                cell.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);

                int value = tablero[i][j];
                if (value != 0) {
                    cell.setText(String.valueOf(value));
                    cell.setBackgroundColor(getColorForValue(value));
                } else {
                    cell.setText("");
                    cell.setBackgroundColor(Color.parseColor("#d6ccc2"));
                }

                cell.setTag(i + "," + j);
                cell.setOnTouchListener(dragListener);

                gridLayout.addView(cell);
            }
        }

        if (!canMove()) {
            endGame("❌ Sin movimientos");
        }
    }

    // ============================
    //   COLORES
    // ============================
    private int getColorForValue(int v) {
        switch (v) {
            case 2: return Color.parseColor("#eee4da");
            case 4: return Color.parseColor("#ede0c8");
            case 8: return Color.parseColor("#f2b179");
            case 16: return Color.parseColor("#f59563");
            case 32: return Color.parseColor("#f67c5f");
            case 64: return Color.parseColor("#f65e3b");
            case 128: return Color.parseColor("#edcf72");
            case 256: return Color.parseColor("#edcc61");
            case 512: return Color.parseColor("#edc850");
            case 1024: return Color.parseColor("#edc53f");
            case 2048: return Color.parseColor("#edc22e");
        }
        return Color.BLACK;
    }

    // ============================
    //   DRAG LISTENER
    // ============================
    private final View.OnTouchListener dragListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent e) {

            switch (e.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    dragging = true;
                    rawStartX = e.getRawX();
                    rawStartY = e.getRawY();

                    String pos = (String) v.getTag();
                    int fila = Integer.parseInt(pos.split(",")[0]);
                    int col = Integer.parseInt(pos.split(",")[1]);

                    draggableTile.setText(String.valueOf(tablero[fila][col]));
                    draggableTile.setBackgroundColor(getColorForValue(tablero[fila][col]));
                    draggableTile.setVisibility(View.VISIBLE);
                    draggableTile.setX(rawStartX - 50);
                    draggableTile.setY(rawStartY - 50);
                    return true;

                case MotionEvent.ACTION_MOVE:
                    if (dragging) {
                        draggableTile.setX(e.getRawX() - 50);
                        draggableTile.setY(e.getRawY() - 50);
                    }
                    return true;

                case MotionEvent.ACTION_UP:
                    dragging = false;
                    draggableTile.setVisibility(View.GONE);

                    float dx = e.getRawX() - rawStartX;
                    float dy = e.getRawY() - rawStartY;

                    if (Math.abs(dx) > Math.abs(dy)) {
                        if (dx > SWIPE_THRESHOLD) moveRight();
                        else if (dx < -SWIPE_THRESHOLD) moveLeft();
                    } else {
                        if (dy > SWIPE_THRESHOLD) moveDown();
                        else if (dy < -SWIPE_THRESHOLD) moveUp();
                    }
                    return true;
            }
            return false;
        }
    };

    // ============================
    //   DETECTAR CAMBIO REAL
    // ============================
    private boolean hasChanged(int[] a, int[] b) {
        for (int i = 0; i < SIZE; i++)
            if (a[i] != b[i]) return true;
        return false;
    }

    // ============================
    //   MOVIMIENTOS
    // ============================
    private void moveLeft() {

        guardarEstado();

        boolean moved = false;

        for (int i = 0; i < SIZE; i++) {

            int[] before = tablero[i].clone();
            int[] compact = compactLine(before);
            int[] merged = mergeLine(compact);

            if (hasChanged(before, merged)) moved = true;

            tablero[i] = merged;
        }

        if (moved) despuesDeMover();
    }


    private void moveRight() {

        guardarEstado();

        boolean moved = false;

        for (int i = 0; i < SIZE; i++) {

            int[] before = tablero[i].clone();
            int[] reversed = reverse(before);
            int[] compact = compactLine(reversed);
            int[] merged = mergeLine(compact);
            merged = reverse(merged);

            if (hasChanged(before, merged)) moved = true;

            tablero[i] = merged;
        }

        if (moved) despuesDeMover();
    }


    private void moveUp() {

        guardarEstado();

        boolean moved = false;

        for (int j = 0; j < SIZE; j++) {

            int[] before = getColumn(j);
            int[] compact = compactLine(before);
            int[] merged = mergeLine(compact);

            if (hasChanged(before, merged)) moved = true;

            setColumn(j, merged);
        }

        if (moved) despuesDeMover();
    }

    private void moveDown() {

        guardarEstado();

        boolean moved = false;

        for (int j = 0; j < SIZE; j++) {

            int[] before = getColumn(j);
            int[] reversed = reverse(before);
            int[] compact = compactLine(reversed);
            int[] merged = mergeLine(compact);
            merged = reverse(merged);

            if (hasChanged(before, merged)) moved = true;

            setColumn(j, merged);
        }

        if (moved) despuesDeMover();
    }


    // ============================
    //   LÓGICA DE FUSIÓN
    // ============================
    private int[] compactLine(int[] line) {
        int[] result = new int[SIZE];
        int p = 0;
        for (int v : line)
            if (v != 0) result[p++] = v;
        return result;
    }

    private int[] mergeLine(int[] line) {
        int[] result = new int[SIZE];
        int p = 0;

        for (int i = 0; i < SIZE; i++) {
            if (i < SIZE - 1 && line[i] == line[i + 1] && line[i] != 0) {
                int merged = line[i] * 2;
                addScore(merged);
                result[p++] = merged;
                i++;
            } else if (line[i] != 0) {
                result[p++] = line[i];
            }
        }
        return result;
    }

    private int[] reverse(int[] arr) {
        int[] r = new int[SIZE];
        for (int i = 0; i < SIZE; i++)
            r[i] = arr[SIZE - 1 - i];
        return r;
    }

    private int[] getColumn(int col) {
        int[] c = new int[SIZE];
        for (int i = 0; i < SIZE; i++)
            c[i] = tablero[i][col];
        return c;
    }

    private void setColumn(int col, int[] values) {
        for (int i = 0; i < SIZE; i++)
            tablero[i][col] = values[i];
    }

    // ============================
    //   DESPUÉS DE MOVER
    // ============================
    private void despuesDeMover() {
        addNewNumber();
        updateUI();
    }

    // ============================
    //   DETECTAR FIN DE JUEGO
    // ============================
    private boolean canMove() {
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++) {

                if (tablero[i][j] == 0) return true;

                if (i < SIZE - 1 && tablero[i][j] == tablero[i + 1][j]) return true;
                if (j < SIZE - 1 && tablero[i][j] == tablero[i][j + 1]) return true;
            }
        return false;
    }

    // GUARDAR ULTIMO ESTADO

    private void guardarEstado() {
        for (int i = 0; i < SIZE; i++)
            previousBoard[i] = tablero[i].clone();

        previousScore = score;
        canUndo = true;
    }

    // DESHACER MOVIMIENTO

    private void deshacerMovimiento() {
        if (!canUndo) {
            Toast.makeText(this, "No hay jugada para deshacer", Toast.LENGTH_SHORT).show();
            return;
        }

        for (int i = 0; i < SIZE; i++)
            tablero[i] = previousBoard[i].clone();

        score = previousScore;
        txtScore.setText("Puntos: " + score);

        updateUI();

        canUndo = false; // solo 1 undo permitido
    }


}
