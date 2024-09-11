package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "schedule.db";
    private static final String TABLE_NAME = "schedule_table";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "TIME";
    private static final String COL_3 = "LOCATION";
    private static final String COL_4 = "DESCRIPTION";
    private static final String COL_5 = "STATUS"; // 0 for incomplete, 1 for complete

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }
    public boolean deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, "ID = ?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_2 + " TEXT, " +
                COL_3 + " TEXT, " +
                COL_4 + " TEXT, " +
                COL_5 + " INTEGER DEFAULT 0)"); // 默认状态为0（未完成）
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    public boolean insertData(String time, String location, String description, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, time);
        contentValues.put(COL_3, location);
        contentValues.put(COL_4, description);
        contentValues.put(COL_5, status);
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1; // 返回插入操作是否成功
    }

    public boolean updateStatus(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_5, status);
        int result = db.update(TABLE_NAME, contentValues, COL_1 + " = ?", new String[]{String.valueOf(id)});
        return result > 0; // 返回更新操作是否成功
    }

    public boolean deleteData(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, COL_1 + " = ?", new String[]{String.valueOf(id)});
        return result > 0; // 返回删除操作是否成功
    }

    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null); // 删除所有数据
    }
}
