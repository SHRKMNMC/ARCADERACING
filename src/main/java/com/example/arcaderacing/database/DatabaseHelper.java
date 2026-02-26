package com.example.arcaderacing.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "arcade.db";

    // SUBE LA VERSIÓN PARA FORZAR RECREAR TABLAS
    private static final int DB_VERSION = 3;

    // ============================
    //   TABLA USERS
    // ============================
    public static final String TABLE_USERS = "users";
    public static final String COL_ID = "id";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";

    // ============================
    //   TABLA SCORES 2048
    // ============================
    public static final String TABLE_SCORES_2048 = "scores2048";
    public static final String COL_SCORE_ID = "id";
    public static final String COL_SCORE_USER = "username";
    public static final String COL_SCORE_VALUE = "score";
    public static final String COL_SCORE_DATE = "date";

    // ============================
    //   TABLA SCORES RACING
    // ============================
    public static final String TABLE_RACING = "racing_scores";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // TABLA USUARIOS
        String createUsers = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT UNIQUE, " +
                COL_PASSWORD + " TEXT)";
        db.execSQL(createUsers);

        // TABLA SCORES 2048
        String createScores2048 = "CREATE TABLE " + TABLE_SCORES_2048 + " (" +
                COL_SCORE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_SCORE_USER + " TEXT, " +
                COL_SCORE_VALUE + " INTEGER, " +
                COL_SCORE_DATE + " TEXT)";
        db.execSQL(createScores2048);

        // TABLA SCORES RACING
        String createRacingScores = "CREATE TABLE " + TABLE_RACING + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT, " +
                "score INTEGER, " +
                "date TEXT)";
        db.execSQL(createRacingScores);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {

        // BORRAR TODAS LAS TABLAS
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES_2048);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RACING);

        onCreate(db);
    }

    // ============================
    //   USUARIOS
    // ============================

    public boolean registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COL_USERNAME, username);
        cv.put(COL_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, cv);
        return result != -1;
    }

    public boolean loginUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS +
                        " WHERE username=? AND password=?",
                new String[]{username, password}
        );

        boolean exists = c.getCount() > 0;
        c.close();
        return exists;
    }

    // ============================
    //   SCORES 2048
    // ============================

    public void saveScore2048(String username, int score) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COL_SCORE_USER, username);
        cv.put(COL_SCORE_VALUE, score);
        cv.put(COL_SCORE_DATE, String.valueOf(System.currentTimeMillis()));

        db.insert(TABLE_SCORES_2048, null, cv);
    }

    public Cursor getScores2048() {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM " + TABLE_SCORES_2048 +
                        " ORDER BY score DESC LIMIT 10",
                null
        );
    }

    // ============================
    //   SCORES RACING
    // ============================

    public void saveRacingScore(String username, int score) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("score", score);
        cv.put("date", String.valueOf(System.currentTimeMillis()));

        db.insert(TABLE_RACING, null, cv);
    }

    public Cursor getRacingScores() {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM " + TABLE_RACING +
                        " ORDER BY score DESC LIMIT 10",
                null
        );
    }
}
