package com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

public class ApplianceDbDataManipulationQuery extends DatabaseHelper {

    public ApplianceDbDataManipulationQuery(@Nullable Context context) {
        super(context);
    }

    public boolean insertApplianceDbData(String appliance_name, Integer status, String appliance_category, int room_id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(APPLIANCE_DB_COL_2, appliance_name);
        contentValues.put(APPLIANCE_DB_COL_3, status);
        contentValues.put(APPLIANCE_DB_COL_4, appliance_category);
        contentValues.put(APPLIANCE_DB_COL_5, room_id);
        long result = db.insert(TABLE_APPLIANCE_DB, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getApplianceDbData(String category, int roomId){
        SQLiteDatabase db = this.getWritableDatabase();

        String getApplianceDbDataQuery = "select * from " + TABLE_APPLIANCE_DB +
                " where " + APPLIANCE_DB_COL_4 + " = ? and " + APPLIANCE_DB_COL_5 + " = ?" ;

        Cursor res = db.rawQuery(getApplianceDbDataQuery, new String[]{category, String.valueOf(roomId)});
        return res;
    }

    public Cursor getApplianceDbData(int roomId){
        SQLiteDatabase db = this.getWritableDatabase();

        String getApplianceDbDataQuery = "select * from " + TABLE_APPLIANCE_DB +
                " where " + APPLIANCE_DB_COL_5 + " = ?" ;

        return db.rawQuery(getApplianceDbDataQuery, new String[]{String.valueOf(roomId)});
    }

    public boolean setAppliancesStatus(int status, String applianceName, int roomId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(APPLIANCE_DB_COL_3, status);
        String whereClause = APPLIANCE_DB_COL_2 + " = ? and "
                + APPLIANCE_DB_COL_5 + " = ?";
        long result = db.update(TABLE_APPLIANCE_DB, contentValues, whereClause, new String[] {applianceName, String.valueOf(roomId)});
        return result != -1;
    }

    public Cursor getAppliancesStatus(String applianceName, int roomId){
        SQLiteDatabase db = this.getWritableDatabase();

        String getAppliancesStatusQuery = "select * from " + TABLE_APPLIANCE_DB +
                " where " + APPLIANCE_DB_COL_2 + " = ? and " + APPLIANCE_DB_COL_5 + " = ?" ;

        Cursor res = db.rawQuery(getAppliancesStatusQuery, new String[]{applianceName, String.valueOf(roomId)});
        return res;
    }

    public void deleteAppliance(String applianceName, int roomId){
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = APPLIANCE_DB_COL_2 + " = ? and "
                + APPLIANCE_DB_COL_5 + " = ?";
        db.delete(TABLE_APPLIANCE_DB, whereClause, new String[] {applianceName, String.valueOf(roomId)});
    }

}
