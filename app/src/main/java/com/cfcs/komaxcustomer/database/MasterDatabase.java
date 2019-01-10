package com.cfcs.komaxcustomer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Admin on 16-11-2017.
 */

public class MasterDatabase {
    public static final int databaseVersion = 1;
    public static final String databaseName = "CustomerDatabase";
    public SQLiteDatabase sqLiteDatabase;
    public Context context;
    public DatabaseHelper databaseHelper;
    public CustomerInfoMasterTable personalInfoMasterTable = new CustomerInfoMasterTable();

    public MasterDatabase(Context context) {
        this.context = context;
        databaseHelper = new DatabaseHelper(context, databaseName, null, databaseVersion);
    }

    //get and set Employee Data
    public void setEmployeeData(String name, String email, String phone) {
        sqLiteDatabase = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(CustomerInfoMasterTable.name, name);
        contentValues.put(CustomerInfoMasterTable.email, email);
        contentValues.put(CustomerInfoMasterTable.phone, phone);

        sqLiteDatabase.insertWithOnConflict(CustomerInfoMasterTable.tableName, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        sqLiteDatabase.close();
    }

    public Cursor getEmployeeData() {
        sqLiteDatabase = databaseHelper.getReadableDatabase();
        Cursor cursor = null;
        cursor = sqLiteDatabase.rawQuery("SELECT " + CustomerInfoMasterTable.email + ", " + CustomerInfoMasterTable.phone +
                ", " + CustomerInfoMasterTable.name + " FROM " + CustomerInfoMasterTable.tableName, null);
        return cursor;
    }

    public int getEmployeeDataCount() {

        String countQuery = "SELECT  * FROM " + CustomerInfoMasterTable.tableName;
        sqLiteDatabase = databaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public void updateRecord(String name, String email, String phone) {

        sqLiteDatabase = databaseHelper.getReadableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(CustomerInfoMasterTable.name, name);
        contentValues.put(CustomerInfoMasterTable.email, email);
        contentValues.put(CustomerInfoMasterTable.phone, phone);

        sqLiteDatabase.update(CustomerInfoMasterTable.tableName, contentValues, null, null);
        sqLiteDatabase.close();
    }
}
