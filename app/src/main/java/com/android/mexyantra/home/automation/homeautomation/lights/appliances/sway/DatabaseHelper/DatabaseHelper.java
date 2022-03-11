package com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.DatabaseHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "home.db";

    //table 1
    public static final String TABLE_ROOM_DB = "ROOM_DB";
    //column name of table 1
    public static final String ROOM_DB_COL_1 = "ROOM_ID";
    public static final String ROOM_DB_COL_2 = "ROOM_NAME";
    public static final String ROOM_DB_COL_3 = "CATEGORY";

    //table 2
    public static final String TABLE_APPLIANCE_DB = "APPLIANCE_DB";
    //column name of table 1
    public static final String APPLIANCE_DB_COL_1 = "APPLIANCE_ID";
    public static final String APPLIANCE_DB_COL_2 = "APPLIANCE_NAME";
    public static final String APPLIANCE_DB_COL_3 = "STATUS";
    public static final String APPLIANCE_DB_COL_4 = "APPLIANCE_CATEGORY";
    public static final String APPLIANCE_DB_COL_5 = "REFERENCE_ROOM_ID";

    //TABLE 3
    public static final String TABLE_DEVICE_IP_DB = "DEVICE_IP_DB";
    //column name of table 3
    public static final String DEVICE_IP_DB_COL_1 = "DEVICE_IP";
    public static final String DEVICE_IP_DB_COL_2 = "REFERENCE_ROOM_ID";

    //TABLE 4
    public static final String TABLE_STARRED_ROOM_DB = "STARRED_ROOM_DB";
    //column name of table 3
    public static final String STARRED_ROOM_DB_COL_1 = "STARRED_ROOM_ID";
    public static final String STARRED_ROOM_DB_COL_2 = "REFERENCE_ROOM_ID";

    // CREATE TABLE_ROOM_DB QUERY
    public static  final String CREATE_ROOM_DB_TABLE = "create table if not exists " + TABLE_ROOM_DB +
            " (ROOM_ID INTEGER PRIMARY KEY," +
            " ROOM_NAME STRING NOT NULL UNIQUE," +
            " CATEGORY STRING NOT NULL)";

    // CREATE TABLE_APPLIANCE_DB QUERY
    public static  final String CREATE_APPLIANCE_DB_TABLE = "create table if not exists " + TABLE_APPLIANCE_DB +
            " (APPLIANCE_ID INTEGER PRIMARY KEY," +
            " APPLIANCE_NAME STRING NOT NULL UNIQUE," +
            " STATUS INTEGER NOT NULL," +
            " APPLIANCE_CATEGORY STRING NOT NULL," +
            " REFERENCE_ROOM_ID INTEGER NOT NULL," +
            " FOREIGN KEY ("+APPLIANCE_DB_COL_5+") REFERENCES "+TABLE_ROOM_DB+"("+ROOM_DB_COL_1+"));";

    // CREATE TABLE_DEVICE_IP_DB QUERY
    public static  final String CREATE_DEVICE_IP_DB_TABLE = "create table if not exists " + TABLE_DEVICE_IP_DB +
            " (DEVICE_IP INTEGER PRIMARY KEY," +
            " REFERENCE_ROOM_ID INTEGER NOT NULL," +
            " FOREIGN KEY ("+DEVICE_IP_DB_COL_2+") REFERENCES "+TABLE_ROOM_DB+"("+ROOM_DB_COL_1+"));";

    // CREATE TABLE_STARRED_ROOM_DB QUERY
    public static  final String CREATE_STARRED_ROOM_DB_TABLE = "create table if not exists " + TABLE_STARRED_ROOM_DB +
            " (STARRED_ROOM_ID INTEGER PRIMARY KEY," +
            " REFERENCE_ROOM_ID INTEGER NOT NULL," +
            " FOREIGN KEY ("+STARRED_ROOM_DB_COL_2+") REFERENCES "+TABLE_ROOM_DB+"("+ROOM_DB_COL_1+"));";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ROOM_DB_TABLE);
        db.execSQL(CREATE_APPLIANCE_DB_TABLE);
        db.execSQL(CREATE_DEVICE_IP_DB_TABLE);
        db.execSQL(CREATE_STARRED_ROOM_DB_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOM_DB);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPLIANCE_DB);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEVICE_IP_DB);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STARRED_ROOM_DB);
    }

}
