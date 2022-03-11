package com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

public class StarredRoomDbDataManipulation extends DatabaseHelper {

    public StarredRoomDbDataManipulation(@Nullable Context context) {
        super(context);
    }

    public boolean markRoomStarred(int roomId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(STARRED_ROOM_DB_COL_2, roomId);
        long result = db.insert(TABLE_STARRED_ROOM_DB, null, contentValues);
        return result != -1;
    }

    public Cursor getStarredRoomDbData(){
        SQLiteDatabase db = this.getWritableDatabase();

        String getStarredRoomDbDataQuery = "select " + TABLE_ROOM_DB + "." + ROOM_DB_COL_2 + " from " + TABLE_ROOM_DB +
                " INNER JOIN " + TABLE_STARRED_ROOM_DB + " ON " + TABLE_ROOM_DB + "." + ROOM_DB_COL_1 + " = " +
                TABLE_STARRED_ROOM_DB + "." + STARRED_ROOM_DB_COL_2 ;

        return db.rawQuery(getStarredRoomDbDataQuery, null);
    }

    public Cursor getStarredRoomDbData(int roomId){
        SQLiteDatabase db = this.getWritableDatabase();

        String getStarredRoomDbDataQuery = "select * from " + TABLE_STARRED_ROOM_DB +
                " where " + STARRED_ROOM_DB_COL_2 + " = ? " ;

        Cursor res = db.rawQuery(getStarredRoomDbDataQuery, new String[]{String.valueOf(roomId)});
        return res;
    }

    public Integer unMarkRoomStarred(int roomId){
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClauseRoom = STARRED_ROOM_DB_COL_2 + " = ?";
        Integer res = db.delete(TABLE_STARRED_ROOM_DB, whereClauseRoom, new String[] {String.valueOf(roomId)});
        return res;
    }

}
