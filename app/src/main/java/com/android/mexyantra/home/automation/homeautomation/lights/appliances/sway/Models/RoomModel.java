package com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class RoomModel implements Parcelable {

    private String roomName;
    private int status;

    public RoomModel(String roomName) {
        this.roomName = roomName;
    }

    public RoomModel(Parcel in) {
        roomName = in.readString();
        //status = in.readInt();
    }

    public static final Parcelable.Creator<RoomModel> CREATOR = new Parcelable.Creator<RoomModel>() {
        @Override
        public RoomModel createFromParcel(Parcel in) {
            return new RoomModel(in);
        }

        @Override
        public RoomModel[] newArray(int size) {
            return new RoomModel[size];
        }
    };

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(roomName);
    }
}
