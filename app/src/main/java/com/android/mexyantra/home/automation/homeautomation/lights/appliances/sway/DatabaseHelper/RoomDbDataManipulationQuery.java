package com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

public class RoomDbDataManipulationQuery extends DatabaseHelper {

    public RoomDbDataManipulationQuery(@Nullable Context context) {
        super(context);
    }

    public boolean insertRoomDbData(String room_name, String category){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ROOM_DB_COL_2, room_name);
        contentValues.put(ROOM_DB_COL_3, category);
        long result = db.insert(TABLE_ROOM_DB, null, contentValues);
        return result != -1;
    }

    public Cursor getAllRoomDbData(){
        SQLiteDatabase db = this.getWritableDatabase();

        String getRoomDbDataQuery = "select * from " + TABLE_ROOM_DB;

        Cursor res = db.rawQuery(getRoomDbDataQuery, null);
        return res;
    }

    public Cursor getRoomDbData(String category){
        SQLiteDatabase db = this.getWritableDatabase();

        String getRoomDbDataQuery = "select * from " + TABLE_ROOM_DB +
                " where " + ROOM_DB_COL_3 + " = ? " ;

        Cursor res = db.rawQuery(getRoomDbDataQuery, new String[]{category});
        return res;
    }

    public Cursor getRoomDbData(int i, String room_name){
        SQLiteDatabase db = this.getWritableDatabase();

        String getRoomDbDataQuery = "select " + ROOM_DB_COL_1 + " from " + TABLE_ROOM_DB +
                " where " + ROOM_DB_COL_2 + " = ? " ;

        Cursor res = db.rawQuery(getRoomDbDataQuery, new String[]{room_name});
        return res;
    }

    public Integer deleteRoom(String roomName, int roomId){ //roomId to delete all the appliances present in the room
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClauseRoom = ROOM_DB_COL_2 + " = ?";
        String whereClauseAppliances = APPLIANCE_DB_COL_5 + " = ?";
        Integer res = db.delete(TABLE_ROOM_DB, whereClauseRoom, new String[] {roomName});
        db.delete(TABLE_APPLIANCE_DB, whereClauseAppliances, new String[] {String.valueOf(roomId)});
        return res;
    }

}
