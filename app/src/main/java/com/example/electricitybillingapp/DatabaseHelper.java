package com.example.electricitybillingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Define Constants for Database and Table names
    public static final String DATABASE_NAME = "Bills.db";
    public static final String TABLE_NAME = "bill_table";

    // Define Constants for Columns
    public static final String COL_1 = "ID";
    public static final String COL_2 = "MONTH";
    public static final String COL_3 = "UNITS";
    public static final String COL_4 = "TOTAL";
    public static final String COL_5 = "REBATE";
    public static final String COL_6 = "FINAL";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Using constants ensures the table is created correctly without typos
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_2 + " TEXT, " +
                COL_3 + " DOUBLE, " +
                COL_4 + " DOUBLE, " +
                COL_5 + " DOUBLE, " +
                COL_6 + " DOUBLE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Method to insert data into the database
    public boolean insertData(String month, double units, double total, double rebate, double finalCost) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_2, month);
        cv.put(COL_3, units);
        cv.put(COL_4, total);
        cv.put(COL_5, rebate);
        cv.put(COL_6, finalCost);

        long result = db.insert(TABLE_NAME, null, cv);
        return result != -1; // Returns true if data is inserted successfully
    }

    // Method to fetch all records for the History Page
    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    public Integer deleteData(String month, String units) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Deletes the row where both Month and Units match (to ensure specific record is removed)
        return db.delete("bill_table", "MONTH = ? AND UNITS = ?", new String[]{month, units});
    }

}