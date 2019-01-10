package com.cfcs.komaxcustomer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Admin on 16-11-2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public CustomerInfoMasterTable personalInfoMasterTable = new CustomerInfoMasterTable();

    public DatabaseHelper(Context context, String DatabaseName, SQLiteDatabase.CursorFactory cursorFactory, int DatabaseVersion) {
        super(context, DatabaseName, cursorFactory, DatabaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CustomerInfoMasterTable.customerMasterTableData);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + CustomerInfoMasterTable.tableName);
    }
}
