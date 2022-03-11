package com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Activity.MainActivity;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Adapters.CategoryHorizontalRecyclerViewAdapter;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Adapters.RoomRecyclerViewAdapter;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Animation.LongPressRecyclerViewAnimation;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Communication.Client;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.DatabaseHelper.RoomDbDataManipulationQuery;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Models.CategoryModel;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.R;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.RecyclerViewSetup.CategoryRecyclerViewSetup;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.RecyclerViewSetup.RoomRecyclerViewSetup;
import com.google.android.material.appbar.AppBarLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener, View.OnClickListener {

    private static final String TAG = "HomeFragment";

    private Context context;
    private View view;
    private CategoryHorizontalRecyclerViewAdapter.OnCategoryListener onCategoryListener;

    // App bar layout, collapsing toolbar
    private AppBarLayout appBarLayout;
    private boolean appBarExpanded;

    private RecyclerView roomsRecyclerView;
    private RelativeLayout noRoomRLayout;
    private RelativeLayout.LayoutParams marginParameter;

    // TABLE_ROOM_DB database
    private RoomDbDataManipulationQuery roomDbDataManipulationQuery;

    // new room name
    private CardView newRoom, createRoom;

    //on long click room item variable
    private static View prevView;
    private static int preViewPos = -1;
    private static boolean roomShrink = false;
    private static String prevCategory = "";

    // for transferring data
    byte[] bufApplianceNameRoomId = new byte[1024]; //used to sending information to esp is a form of byte

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        findById();

        context = getActivity();
        onRecyclerViewClickListener();

        classImport();

        return view;
    }

    public void findById(){
        appBarLayout = view.findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener(this);
        roomsRecyclerView = view.findViewById(R.id.rooms_recycler_view);
        noRoomRLayout = view.findViewById(R.id.no_room_found_rLayout);
        newRoom = view.findViewById(R.id.newRoom);
        newRoom.setOnClickListener(this);
        roomDbDataManipulationQuery = new RoomDbDataManipulationQuery(getActivity());
    }

    public void classImport(){

        // Category recycler view
        CategoryRecyclerViewSetup categoryRecyclerViewSetup = new CategoryRecyclerViewSetup(context, view,
                onCategoryListener);
        categoryRecyclerViewSetup.categoryViewModelData();

        // Room recycler view
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new RoomRecyclerViewSetup(context, view, true);
                //roomRecyclerViewSetup.roomViewModelData();
            }
        },200);
    }

    public void onRecyclerViewClickListener(){
        onCategoryListener = new CategoryHorizontalRecyclerViewAdapter.OnCategoryListener() {
            @Override
            public void OnCategoryClick(int i) {
                //Toast.makeText(getContext(), "OnCategoryClick : "+i, Toast.LENGTH_SHORT).show();
                appBarLayout.setExpanded(false);
            }
        };
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        noRoomRLayoutMargin(i);
        appBarExpanded = i == 0;
    }

    public void onBackPressed() {
        LongPressRecyclerViewAnimation longPressRecyclerViewAnimation_1 = new LongPressRecyclerViewAnimation();
        if(!appBarExpanded){
            if (roomShrink){
                longPressRecyclerViewAnimation_1.expandWidth(prevView);
                roomShrink = false;
            }
            else {
                appBarLayout.setExpanded(true);
                MainActivity.doubleOnBackPressed_PressedOnce = false;
                roomsRecyclerView.scrollToPosition(0);
            }
        }
        else {
            if(roomShrink){
                longPressRecyclerViewAnimation_1.expandWidth(prevView);
                roomShrink = false;
            }
            else
                MainActivity.doubleOnBackPressed_PressedOnce = true;
        }
        Log.d(TAG, "onBackPressed: appBar");
    }

    private void noRoomRLayoutMargin(int i){
        marginParameter = (RelativeLayout.LayoutParams) noRoomRLayout.getLayoutParams();
        if (i==0){
            marginParameter.setMargins(marginParameter.leftMargin, 200, marginParameter.rightMargin, marginParameter.bottomMargin);
        }
        else {
            marginParameter.setMargins(marginParameter.leftMargin, 700, marginParameter.rightMargin, marginParameter.bottomMargin);
        }
        noRoomRLayout.setLayoutParams(marginParameter);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.newRoom) {
            Log.d(TAG, "onClick: CLICKED");
            String category = CategoryRecyclerViewSetup.category;
            Log.d(TAG, "Inb: " + category);
            alertBoxForNewRoom(category);
        }
    }

    public void alertBoxForNewRoom(final String category){
        final AlertDialog.Builder roomName = new AlertDialog.Builder(context);
        View new_room_alert_dialog = getLayoutInflater().inflate(R.layout.new_room_alert_dialog, null);
        final EditText inputRoomName = new_room_alert_dialog.findViewById(R.id.input_room_name);

        createRoom = new_room_alert_dialog.findViewById(R.id.createRoom);

        roomName.setView(new_room_alert_dialog);
        final AlertDialog alertDialog = roomName.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        createRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getRoomName = inputRoomName.getText().toString();
                if (getRoomName.equals("")){
                    Toast.makeText(getContext(), "Please enter a room name", Toast.LENGTH_LONG).show();
                }
                else {
                    Log.d(TAG, "insertRoomInDb: " + getRoomName);
                    roomDbDataManipulationQuery.insertRoomDbData(getRoomName, category);
                    new RoomRecyclerViewSetup(context, view, false);
                    mapRoom(getRoomName, category);
                    alertDialog.cancel();
                }
            }
        });
    }

    public static void onLongRoomClick(View roomCardView, int position, View deleteRoomBtn) {
        Log.d(TAG, "onLongRoomClick: Clicked");
        LongPressRecyclerViewAnimation longPressRecyclerViewAnimation_1 = new LongPressRecyclerViewAnimation();
        LongPressRecyclerViewAnimation longPressRecyclerViewAnimation_2 = new LongPressRecyclerViewAnimation();

        if(preViewPos==position && CategoryRecyclerViewSetup.category.equals(prevCategory)){
            if(roomShrink) {
                longPressRecyclerViewAnimation_1.expandWidth(roomCardView);
                deleteRoomBtn.setClickable(false);
                roomShrink = false;
            }
            else {
                longPressRecyclerViewAnimation_1.shrinkWidth(roomCardView);
                deleteRoomBtn.setClickable(true);
                roomShrink = true;
            }
        }
        else {
            longPressRecyclerViewAnimation_1.shrinkWidth(roomCardView);
            deleteRoomBtn.setClickable(true);
            roomShrink = true;
            if(preViewPos!=-1)
                longPressRecyclerViewAnimation_2.expandWidth(prevView);
        }

        prevView = roomCardView;
        preViewPos = position;
        prevCategory = CategoryRecyclerViewSetup.category;
    }

    public static void OnRoomClick(View roomCardView, int i) {
        if (roomShrink){
            LongPressRecyclerViewAnimation longPressRecyclerViewAnimation_1 = new LongPressRecyclerViewAnimation();
            longPressRecyclerViewAnimation_1.expandWidth(roomCardView);
            longPressRecyclerViewAnimation_1.expandWidth(prevView);
            roomShrink = false;
        }
    }

    public void mapRoom(String roomName, String category){
        String roomNameCategory = roomName + category;
        bufApplianceNameRoomId = null;
        bufApplianceNameRoomId = (roomNameCategory).getBytes();
        Client a = new Client(bufApplianceNameRoomId);
        a.run();
    }

}
