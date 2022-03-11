package com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.RecyclerViewSetup;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Activity.ControlActivity;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Adapters.RoomRecyclerViewAdapter;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Animation.LongPressRecyclerViewAnimation;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.DatabaseHelper.RoomDbDataManipulationQuery;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Fragments.HomeFragment;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Models.RoomModel;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.R;

import java.util.ArrayList;

public class RoomRecyclerViewSetup implements RoomRecyclerViewAdapter.OnRoomListener, RoomRecyclerViewAdapter.OnLongRoomClickListener,
        RoomRecyclerViewAdapter.OnDeleteRoomItemListener {

    private static final String TAG = "RoomRecyclerViewSetup";

    // TABLE_ROOM_DB database
    private RoomDbDataManipulationQuery roomDbDataManipulationQuery;

    private boolean firstRun;

    private Context context;
    private View view;

    // data of categoryView
    private ArrayList<RoomModel> roomModels;

    private RoomRecyclerViewAdapter roomRecyclerViewAdapter;
    private RecyclerView roomView;

    private RelativeLayout noRoomRLayout;

    public RoomRecyclerViewSetup(Context context, View view, boolean firstRun) {
        this.context = context;
        this.view = view;
        this.firstRun = firstRun;
        roomView = view.findViewById(R.id.rooms_recycler_view);
        noRoomRLayout = view.findViewById(R.id.no_room_found_rLayout);
        roomDbDataManipulationQuery = new RoomDbDataManipulationQuery(context);

        roomViewModelData();
    }

    public void roomViewModelData(){
        this.roomModels = new ArrayList<>();

        String category = CategoryRecyclerViewSetup.category;

        Log.d(TAG, "category: " + category);

        if(category!=null && !category.equals("")){
            Cursor res = roomDbDataManipulationQuery.getRoomDbData(category);
            while (res.moveToNext()){
                roomModels.add(new RoomModel(res.getString(1)));
            }
        }

        Log.d(TAG, "rooms: " + roomModels);

        if(roomModels.isEmpty()){
            roomView.setVisibility(View.GONE);
            noRoomRLayout.setVisibility(View.VISIBLE);
        }
        else {
            roomView.setVisibility(View.VISIBLE);
            noRoomRLayout.setVisibility(View.GONE);
        }

        setUpRoomRecyclerView();
    }

    public void setUpRoomRecyclerView(){
        roomRecyclerViewAdapter = new RoomRecyclerViewAdapter(context, roomModels, this,
                this, this);
        roomView.setLayoutManager(new LinearLayoutManager(context));

        if (firstRun){
            roomView.addItemDecoration(new MarginItemDecorator(view.getResources().getDimensionPixelSize(R.dimen.room_view_left_spacing),
                    view.getResources().getDimensionPixelSize(R.dimen.room_view_right_spacing),
                    view.getResources().getDimensionPixelSize(R.dimen.room_view_top_spacing),
                    view.getResources().getDimensionPixelSize(R.dimen.room_view_bottom_spacing),
                    MarginItemDecorator.VERTICAL));
        }
        roomView.setAdapter(roomRecyclerViewAdapter);
        roomRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnRoomClick(View roomCardView, int i) {
        Log.d(TAG, "OnRoomClick: ");
        Intent intent = new Intent(context, ControlActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("position", i);
        bundle.putParcelableArrayList("roomModels", roomModels);
        intent.putExtras(bundle);
        context.startActivity(intent);
        HomeFragment.OnRoomClick(roomCardView, i);
    }

    @Override
    public void onDeleteRoomItem(final int position) {
        final String roomName = roomModels.get(position).getRoomName();
        // getting ID of room name to delete all appliances in it
        int roomID = -1;
        Cursor res = roomDbDataManipulationQuery.getRoomDbData(0, roomName);
        while (res.moveToNext()){
            roomID = res.getInt(0);
        }
        
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View deleteConfirmationDialog = inflater.inflate(R.layout.delete_confirmation_dialog, null);
        final TextView displayText = deleteConfirmationDialog.findViewById(R.id.confirmation_text);
        String ConfirmationText = "Are you sure to delete '" + roomName + "'?";
        displayText.setText(ConfirmationText);
        CardView positiveBtn = deleteConfirmationDialog.findViewById(R.id.positiveBtn);
        CardView negativeBtn = deleteConfirmationDialog.findViewById(R.id.negativeBtn);

        deleteDialog.setView(deleteConfirmationDialog);

        final AlertDialog alertDialog = deleteDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final int finalRoomID = roomID;
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomDbDataManipulationQuery.deleteRoom(roomName, finalRoomID);
                roomModels.remove(position);
                roomRecyclerViewAdapter.notifyItemRemoved(position);
                if(roomModels.isEmpty()){
                    roomView.setVisibility(View.GONE);
                    noRoomRLayout.setVisibility(View.VISIBLE);
                }
                alertDialog.cancel();
            }
        });

        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
    }

    @Override
    public void onLongRoomClick(View v, int position, View view) {
        HomeFragment.onLongRoomClick(v, position, view);
    }
}
