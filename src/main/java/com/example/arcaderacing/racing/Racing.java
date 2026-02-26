package com.example.arcaderacing.racing;

import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.arcaderacing.R;
import com.example.arcaderacing.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class Racing extends AppCompatActivity {

    private float startX;
    private static final int SWIPE_THRESHOLD = 50;

    private ImageView car;
    private ImageView flashEffect;
    private CarController carController;

    private float[] lanePositions;

    private static final int OBSTACLE_COUNT = 6;
    private final List<Obstacle> obstacles = new ArrayList<>();

    private boolean gameOver = false;

    public float globalSpeedFactor = 1f;

    private RoadView roadView;

    // ===============================
    //   SISTEMA DE FRENADO
    // ===============================
    private boolean brakeOnCooldown = false;
    private boolean brakeActive = false;

    private static final int BRAKE_DELAY_MS = 300;
    private static final int BRAKE_DURATION_MS = 800;
    private static final int BRAKE_COOLDOWN_MS = 3000;

    private Handler brakeTimer = new Handler();
    private Runnable brakeStartRunnable;
    private Runnable brakeStopRunnable;

    // ===============================
    //   FLASH ABILITY
    // ===============================
    private Button flashButton;
    private boolean flashOnCooldown = false;
    private static final int FLASH_COOLDOWN_MS = 4000;

    // ===============================
    //   CRONÓMETRO REAL
    // ===============================
    private long startTime = 0;
    private long elapsedTime = 0;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;
    private boolean timerRunning = false;

    private TextView txtTimerScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_racing);

        car = findViewById(R.id.car);
        flashEffect = findViewById(R.id.flashEffect);
        roadView = findViewById(R.id.road);
        flashButton = findViewById(R.id.btnFlash);
        txtTimerScore = findViewById(R.id.txtTimerScore);

        ConstraintLayout parent = findViewById(R.id.main);

        parent.post(() -> {

            int width = parent.getWidth();
            int height = parent.getHeight();

            lanePositions = new float[]{
                    width * 1f / 8f,
                    width * 3f / 8f,
                    width * 5f / 8f,
                    width * 7f / 8f
            };

            carController = new CarController(car, lanePositions, 1);

            spawnObstacles(parent, lanePositions, height);
        });

        flashButton.setOnClickListener(v -> useFlashAbility());

        // ===============================
        //   CONTROL TÁCTIL
        // ===============================
        findViewById(R.id.main).setOnTouchListener((v, event) -> {
            if (gameOver) return false;

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:

                    startX = event.getX();

                    if (!brakeOnCooldown) {
                        brakeStartRunnable = () -> startBrake();
                        brakeTimer.postDelayed(brakeStartRunnable, BRAKE_DELAY_MS);
                    }

                    return true;

                case MotionEvent.ACTION_UP:

                    float endX = event.getX();
                    float deltaX = endX - startX;

                    brakeTimer.removeCallbacks(brakeStartRunnable);

                    if (Math.abs(deltaX) > SWIPE_THRESHOLD) {
                        if (deltaX > 0) carController.moveRight();
                        else carController.moveLeft();
                    }

                    return true;
            }
            return false;
        });

        // 🔥 INICIAR CRONÓMETRO REAL
        startTimer();
    }

    // ===========================================================
    //   CRONÓMETRO REAL (mm:ss)
    // ===========================================================
    private void startTimer() {
        startTime = System.currentTimeMillis();
        timerRunning = true;

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (!timerRunning) return;

                elapsedTime = System.currentTimeMillis() - startTime;

                long seconds = elapsedTime / 1000;
                long minutes = seconds / 60;
                seconds = seconds % 60;

                txtTimerScore.setText(String.format("Tiempo: %02d:%02d", minutes, seconds));

                timerHandler.postDelayed(this, 100);
            }
        };

        timerHandler.postDelayed(timerRunnable, 100);
    }

    private void stopTimer() {
        timerRunning = false;
        timerHandler.removeCallbacks(timerRunnable);
    }

    // ===========================================================
    //   GUARDAR SCORE
    // ===========================================================
    private void saveRacingScore() {

        if (!gameOver) return; // seguridad extra

        DatabaseHelper db = new DatabaseHelper(this);

        String username = getSharedPreferences("session", MODE_PRIVATE)
                .getString("username", "Anon");

        int finalScoreSeconds = (int) (elapsedTime / 1000);

        db.saveRacingScore(username, finalScoreSeconds);
    }

    // ===========================================================
    //   FRENADO NUEVO
    // ===========================================================
    private void startBrake() {

        if (brakeOnCooldown || brakeActive) return;

        brakeActive = true;
        brakeOnCooldown = true;

        globalSpeedFactor = 0.45f;
        roadView.setGlobalSpeedFactor(0.45f);

        brakeStopRunnable = this::stopBrake;
        brakeTimer.postDelayed(brakeStopRunnable, BRAKE_DURATION_MS);

        brakeTimer.postDelayed(() -> brakeOnCooldown = false, BRAKE_COOLDOWN_MS);
    }

    private void stopBrake() {
        if (!brakeActive) return;

        brakeActive = false;

        globalSpeedFactor = 1f;
        roadView.setGlobalSpeedFactor(1f);
    }

    // ===========================================================
    //   FLASH ABILITY
    // ===========================================================
    private void useFlashAbility() {

        if (flashOnCooldown) {
            Toast.makeText(this, "Habilidad en enfriamiento...", Toast.LENGTH_SHORT).show();
            return;
        }

        int lane = carController.getCurrentLane();
        Obstacle front = getFrontObstacleInLane(lane);

        if (front == null) {
            Toast.makeText(this, "No hay coche delante", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean applied = front.applyFlashEffect(obstacles);

        if (!applied) {
            Toast.makeText(this, "No se puede aplicar la luz a este coche", Toast.LENGTH_SHORT).show();
            return;
        }

        playFlashAnimation();

        flashOnCooldown = true;
        flashButton.setEnabled(false);
        flashButton.setAlpha(0.3f);

        new Handler().postDelayed(() -> {
            flashOnCooldown = false;
            flashButton.setEnabled(true);
            flashButton.setAlpha(1f);
        }, FLASH_COOLDOWN_MS);
    }

    // ===========================================================
    //   ANIMACIÓN DE FLASH
    // ===========================================================
    private void playFlashAnimation() {

        flashEffect.setVisibility(ImageView.VISIBLE);

        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(120);

        AlphaAnimation fadeOut = new AlphaAnimation(1f, 0f);
        fadeOut.setStartOffset(200);
        fadeOut.setDuration(250);

        flashEffect.startAnimation(fadeIn);
        flashEffect.startAnimation(fadeOut);

        new Handler().postDelayed(() ->
                flashEffect.setVisibility(ImageView.GONE), 450);
    }

    // ===========================================================
    //   BUSCAR OBSTÁCULO DELANTE
    // ===========================================================
    private Obstacle getFrontObstacleInLane(int lane) {

        float carY = car.getY();
        Obstacle closest = null;
        float maxY = -Float.MAX_VALUE;

        for (Obstacle o : obstacles) {
            if (o.getLaneIndex() != lane) continue;

            float oy = o.getSprite().getY();

            if (oy < carY && oy > maxY) {
                maxY = oy;
                closest = o;
            }
        }
        return closest;
    }

    private void spawnObstacles(ConstraintLayout parent, float[] lanes, int screenHeight) {

        for (int i = 0; i < OBSTACLE_COUNT; i++) {

            int offsetY = -500 * (i + 1);

            Obstacle o = new Obstacle(
                    this,
                    parent,
                    lanes,
                    screenHeight,
                    offsetY
            );

            obstacles.add(o);
        }
    }

    public void checkCollision(ImageView obstacle) {
        if (gameOver) return;

        if (isColliding(car, obstacle)) {
            gameOver();
        }
    }

    private boolean isColliding(ImageView car, ImageView obstacle) {

        int[] carPos = new int[2];
        int[] obsPos = new int[2];

        car.getLocationOnScreen(carPos);
        obstacle.getLocationOnScreen(obsPos);

        float carX = carPos[0] + car.getWidth() * 0.15f;
        float carY = carPos[1] + car.getHeight() * 0.10f;
        float carW = car.getWidth() * 0.70f;
        float carH = car.getHeight() * 0.80f;

        float obsX = obsPos[0] + obstacle.getWidth() * 0.15f;
        float obsY = obsPos[1] + obstacle.getHeight() * 0.10f;
        float obsW = obstacle.getWidth() * 0.70f;
        float obsH = obstacle.getHeight() * 0.80f;

        return carX < obsX + obsW &&
                carX + carW > obsX &&
                carY < obsY + obsH &&
                carY + carH > obsY;
    }

    private void gameOver() {
        gameOver = true;

        stopTimer();       // detener cronómetro real
        saveRacingScore(); // guardar tiempo SOLO aquí

        runOnUiThread(() ->
                Toast.makeText(this, "💥 ¡Has chocado!", Toast.LENGTH_SHORT).show()
        );

        finish();
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }
}
