package com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Activity.ControlActivity;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Adapters.RoomRecyclerViewAdapter;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.DatabaseHelper.RoomDbDataManipulationQuery;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.DatabaseHelper.StarredRoomDbDataManipulation;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Models.RoomModel;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.R;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.RecyclerViewSetup.MarginItemDecorator;

import java.util.ArrayList;

public class StarredFragment extends Fragment implements RoomRecyclerViewAdapter.OnRoomListener, RoomRecyclerViewAdapter.OnLongRoomClickListener,
        RoomRecyclerViewAdapter.OnDeleteRoomItemListener {

    private View view;
    private Context context;

    private RecyclerView allRoomRecyclerView;
    private CardView starredRooms, allRooms;

    private RoomDbDataManipulationQuery roomDbDataManipulationQuery;
    private StarredRoomDbDataManipulation starredRoomDbDataManipulation;
    private RoomRecyclerViewAdapter roomRecyclerViewAdapter;

    private ArrayList<RoomModel> allRoomModels, starredRoomModels;

    private boolean starredRoomActive;
    private RelativeLayout noStarredRoomLayout;
    private TextView noRoomFoundText;

    public StarredFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_starred, container, false);

        context = getActivity();

        findById();

        setOnClickListener();

        fetchStarredRooms(true);

        starredRoomActive = true;

        return view;
    }

    public void findById(){
        allRoomRecyclerView = view.findViewById(R.id.starred_recycler_view);
        starredRooms = view.findViewById(R.id.starred_rooms);
        allRooms = view.findViewById(R.id.all_rooms);
        noRoomFoundText = view.findViewById(R.id.no_room_text);
        noStarredRoomLayout = view.findViewById(R.id.no_starred_room_found_rLayout);
        roomDbDataManipulationQuery = new RoomDbDataManipulationQuery(context);
        starredRoomDbDataManipulation = new StarredRoomDbDataManipulation(context);
    }

    public void fetchAllRooms(boolean fragmentFirstRun){
        this.allRoomModels = new ArrayList<>();
        Cursor res = roomDbDataManipulationQuery.getAllRoomDbData();
        while (res.moveToNext()){
            allRoomModels.add(new RoomModel(res.getString(1)));
        }

        if(allRoomModels.isEmpty()){
            allRoomRecyclerView.setVisibility(View.GONE);
            noStarredRoomLayout.setVisibility(View.VISIBLE);
            noRoomFoundText.setText("No room found!");
        }
        else {
            allRoomRecyclerView.setVisibility(View.VISIBLE);
            noStarredRoomLayout.setVisibility(View.GONE);
        }

        setUpAllRoomRecyclerView(fragmentFirstRun);
    }

    public void fetchStarredRooms(boolean fragmentFirstRun){
        this.starredRoomModels = new ArrayList<>();
        Cursor res = starredRoomDbDataManipulation.getStarredRoomDbData();
        while(res.moveToNext()){
            starredRoomModels.add(new RoomModel(res.getString(0)));
        }

        if(starredRoomModels.isEmpty()){
            allRoomRecyclerView.setVisibility(View.GONE);
            noStarredRoomLayout.setVisibility(View.VISIBLE);
            noRoomFoundText.setText("No Starred room found!");

        }
        else {
            allRoomRecyclerView.setVisibility(View.VISIBLE);
            noStarredRoomLayout.setVisibility(View.GONE);
        }

        setUpStarredRoomRecyclerView(fragmentFirstRun);
    }

    public void setUpAllRoomRecyclerView(boolean fragmentFirstRun){
        roomRecyclerViewAdapter = new RoomRecyclerViewAdapter(context, allRoomModels, this,
                this, this);
        allRoomRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        if (fragmentFirstRun){
            allRoomRecyclerView.addItemDecoration(new MarginItemDecorator(view.getResources().getDimensionPixelSize(R.dimen.room_view_left_spacing),
                    view.getResources().getDimensionPixelSize(R.dimen.room_view_right_spacing),
                    view.getResources().getDimensionPixelSize(R.dimen.room_view_top_spacing),
                    view.getResources().getDimensionPixelSize(R.dimen.room_view_bottom_spacing),
                    MarginItemDecorator.VERTICAL));
        }
        allRoomRecyclerView.setAdapter(roomRecyclerViewAdapter);
        roomRecyclerViewAdapter.notifyDataSetChanged();
    }

    public void setUpStarredRoomRecyclerView(boolean fragmentFirstRun){
        roomRecyclerViewAdapter = new RoomRecyclerViewAdapter(context, starredRoomModels, this,
                this, this);
        allRoomRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        if (fragmentFirstRun){
            allRoomRecyclerView.addItemDecoration(new MarginItemDecorator(view.getResources().getDimensionPixelSize(R.dimen.room_view_left_spacing),
                    view.getResources().getDimensionPixelSize(R.dimen.room_view_right_spacing),
                    view.getResources().getDimensionPixelSize(R.dimen.room_view_top_spacing),
                    view.getResources().getDimensionPixelSize(R.dimen.room_view_bottom_spacing),
                    MarginItemDecorator.VERTICAL));
        }
        allRoomRecyclerView.setAdapter(roomRecyclerViewAdapter);
        roomRecyclerViewAdapter.notifyDataSetChanged();
    }

    public void setOnClickListener(){
        starredRooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchStarredRooms(false);
                starredRoomActive = true;
            }
        });

        allRooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchAllRooms(false);
                starredRoomActive = false;
            }
        });
    }

    @Override
    public void OnRoomClick(View roomCardView, int i) {
        Intent intent = new Intent(context, ControlActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("position", i);
        if (starredRoomActive)
            bundle.putParcelableArrayList("roomModels", starredRoomModels);
        else
            bundle.putParcelableArrayList("roomModels", allRoomModels);
        intent.putExtras(bundle);
        context.startActivity(intent);
        HomeFragment.OnRoomClick(roomCardView, i);
    }

    @Override
    public void onDeleteRoomItem(final int position) {
        final String roomName = allRoomModels.get(position).getRoomName();
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
                allRoomModels.remove(position);
                roomRecyclerViewAdapter.notifyItemRemoved(position);
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
