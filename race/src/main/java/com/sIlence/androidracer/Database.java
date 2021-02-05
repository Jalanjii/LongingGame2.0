/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sIlence.androidracer;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.TextView;

/**
 *
 * @author Mytchel
 */
public class Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "androidracer.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "highscores";

    public static final String ID = "id";
    public static final String GAME_TYPE = "gametype";
    public static final String NAME = "name";
    public static final String SCORE = "score";


    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME  + " ("
                    + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + GAME_TYPE + " INTEGER, "
                    + NAME + " TEXT NOT NULL, "
                    + SCORE + " INTEGER"
                    + ");";
        db.execSQL(sql);

        sql = "INSERT INTO " + TABLE_NAME + " ("
            + GAME_TYPE + ", " + NAME + ", " + SCORE + ") "
            + " VALUES (" + AIRacer.DIFF_CHILD + ", 'Nobody', 0)" ;
        db.execSQL(sql);

        sql = "INSERT INTO " + TABLE_NAME + " ("
            + GAME_TYPE + ", " + NAME + ", " + SCORE + ") "
            + " VALUES (" + AIRacer.DIFF_EASY + ", 'Nobody', 0)" ;
        db.execSQL(sql);

        sql = "INSERT INTO " + TABLE_NAME + " ("
            + GAME_TYPE + ", " + NAME + ", " + SCORE + ") "
            + " VALUES (" + AIRacer.DIFF_MEDI + ", 'Nobody', 0)" ;
        db.execSQL(sql);

        sql = "INSERT INTO " + TABLE_NAME + " ("
            + GAME_TYPE + ", " + NAME + ", " + SCORE + ") "
            + " VALUES (" + AIRacer.DIFF_HARD + ", 'Nobody', 0)" ;
        db.execSQL(sql);

        sql = "INSERT INTO " + TABLE_NAME + " ("
            + GAME_TYPE + ", " + NAME + ", " + SCORE + ") "
            + " VALUES (" + AIRacer.DIFF_INSANE + ", 'Nobody', 0)" ;
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void readHighScores(Activity c) {
        SQLiteDatabase db = getReadableDatabase();
        String[] from = {ID, GAME_TYPE, NAME, SCORE};

        Cursor cursor = db.query(TABLE_NAME, from, null, null, null, null, GAME_TYPE);
        cursor.moveToFirst();

        TextView cell;

        cell = (TextView) c.findViewById(R.id.child_name);
        cell.setText(cursor.getString(2));
        cell = (TextView) c.findViewById(R.id.child_score);
        cell.setText(cursor.getString(3));

        cursor.moveToNext();
        cell = (TextView) c.findViewById(R.id.easy_name);
        cell.setText(cursor.getString(2));
        cell = (TextView) c.findViewById(R.id.easy_score);
        cell.setText(cursor.getString(3));

        cursor.moveToNext();
        cell = (TextView) c.findViewById(R.id.medium_name);
        cell.setText(cursor.getString(2));
        cell = (TextView) c.findViewById(R.id.medium_score);
        cell.setText(cursor.getString(3));

        cursor.moveToNext();
        cell = (TextView) c.findViewById(R.id.hard_name);
        cell.setText(cursor.getString(2));
        cell = (TextView) c.findViewById(R.id.hard_score);
        cell.setText(cursor.getString(3));

        cursor.moveToNext();
        cell = (TextView) c.findViewById(R.id.insane_name);
        cell.setText(cursor.getString(2));
        cell = (TextView) c.findViewById(R.id.insane_score);
        cell.setText(cursor.getString(3));


        cursor.close();
        db.close();
    }

    public void clear() {
        SQLiteDatabase db = getWritableDatabase();

        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(sql);

        sql = "CREATE TABLE " + TABLE_NAME  + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + GAME_TYPE + " INTEGER, "
            + NAME + " TEXT NOT NULL, "
            + SCORE + " INTEGER"
            + ");";
        db.execSQL(sql);

        sql = "INSERT INTO " + TABLE_NAME + " ("
            + GAME_TYPE + ", " + NAME + ", " + SCORE + ") "
            + " VALUES (" + AIRacer.DIFF_CHILD + ", 'sIlence', 0)" ;
        db.execSQL(sql);

        sql = "INSERT INTO " + TABLE_NAME + " ("
            + GAME_TYPE + ", " + NAME + ", " + SCORE + ") "
            + " VALUES (" + AIRacer.DIFF_EASY + ", 'sIlence', 0)" ;
        db.execSQL(sql);

        sql = "INSERT INTO " + TABLE_NAME + " ("
            + GAME_TYPE + ", " + NAME + ", " + SCORE + ") "
            + " VALUES (" + AIRacer.DIFF_MEDI + ", 'sIlence', 0)" ;
        db.execSQL(sql);

        sql = "INSERT INTO " + TABLE_NAME + " ("
            + GAME_TYPE + ", " + NAME + ", " + SCORE + ") "
            + " VALUES (" + AIRacer.DIFF_HARD + ", 'sIlence', 0)" ;
        db.execSQL(sql);

        sql = "INSERT INTO " + TABLE_NAME + " ("
            + GAME_TYPE + ", " + NAME + ", " + SCORE + ") "
            + " VALUES (" + AIRacer.DIFF_INSANE + ", 'sIlence', 0)" ;
        db.execSQL(sql);

        db.close();
    }

    public boolean isHighScore(int gameType, int score) {
        SQLiteDatabase db = getReadableDatabase();
        String[] from = {ID, GAME_TYPE, NAME, SCORE};

        Cursor cursor = db.query(TABLE_NAME, from, null, null, null, null, GAME_TYPE);
        cursor.moveToFirst();

        while (true) {
            if (cursor.isAfterLast()) break;

            if (cursor.getInt(1) == gameType) {
                if (cursor.getInt(3) < score) {
                    cursor.close();
                    db.close();
                    return true;
                } else {
                    break;
                }
            }

            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return false;
    }

    public void insertHighScore(String name, int gameType, int score) {
        SQLiteDatabase db = getReadableDatabase();
        String[] from = {ID, GAME_TYPE, NAME, SCORE};

        Cursor cursor = db.query(TABLE_NAME, from, null, null, null, null, GAME_TYPE);
        cursor.moveToFirst();

        while (true) {
            if (cursor.isAfterLast()) break;

            if (cursor.getInt(1) == gameType) {

                if (cursor.getInt(3) < score) {

                    cursor.close();
                    db.close();

                    db = getWritableDatabase();
                    String sql = "UPDATE " + TABLE_NAME
                                + " SET " + NAME + "='" + name + "', "
                                + SCORE + "=" + score
                                + " WHERE " + GAME_TYPE + "=" + gameType;
                    db.execSQL(sql);
                    db.close();
                }
                break;
            }

            cursor.moveToNext();
        }

        cursor.close();
        db.close();
    }
}
