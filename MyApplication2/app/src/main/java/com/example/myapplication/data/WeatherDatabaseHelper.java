package com.example.myapplication.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

public class WeatherDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "weather.db";
    private static final int DATABASE_VERSION = 1;

    public WeatherDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE weather (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "city TEXT, " +
                "temperature REAL, " +
                "condition TEXT, " +
                "updateTime TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS weather");
        onCreate(db);
    }

    public void insertWeather(String city, double temperature, String condition, String updateTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("city", city);
        values.put("temperature", temperature);
        values.put("condition", condition);
        values.put("updateTime", updateTime);
        db.insert("weather", null, values);
    }
}
