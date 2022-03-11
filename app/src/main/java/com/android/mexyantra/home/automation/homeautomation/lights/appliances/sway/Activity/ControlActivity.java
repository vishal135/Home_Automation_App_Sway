package com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Adapters.ExpandableFanAdapter;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Adapters.ExpandableLightAdapter;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Adapters.ExpandableOtherAppliancesAdapter;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Animation.ExpandCollapseAnimation;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Animation.LongPressRecyclerViewAnimation;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Communication.Client;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.DatabaseHelper.ApplianceDbDataManipulationQuery;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.DatabaseHelper.RoomDbDataManipulationQuery;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.DatabaseHelper.StarredRoomDbDataManipulation;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Fragments.AllApplianceViewFragment;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Fragments.ApplianceCategoryViewFragment;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Models.FanApplianceModel;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Models.LightApplianceModel;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Models.OtherApplianceModel;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Models.RoomModel;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.R;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.RecyclerViewSetup.MarginItemDecorator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ControlActivity extends AppCompatActivity {

    private static final String TAG = "ControlActivity";

    private Switch allLightSwitch, allFanSwitch, starredRoomSwitch;

    private RoomDbDataManipulationQuery roomDbDataManipulationQuery;
    // Appliance Db
    private ApplianceDbDataManipulationQuery applianceDbDataManipulationQuery;
    private StarredRoomDbDataManipulation starredRoomDbDataManipulation;

    // spinner appliance drop down
    private Spinner applianceDropDownSpinner;

    private int position;
    private ArrayList<RoomModel> roomModels;
    private int roomId;

    private LinearLayout applianceViewHeader;

    private List<FanApplianceModel> fanApplianceModels;
    private List<OtherApplianceModel> otherApplianceModels;
    private List<LightApplianceModel> lightApplianceModels;

    // shared preferences
    private SharedPreferences sharedPreferences;
    private boolean categoryApplianceView;

    private ExpandCollapseAnimation expandCollapseAnimation;

    public static View applianceCategoryViewFragmentView;
    public static View allApplianceViewFragmentView;

    private RelativeLayout coverView;

    // fragments
    private AllApplianceViewFragment allApplianceViewFragment;
    private ApplianceCategoryViewFragment applianceCategoryViewFragment;

    public ControlActivity(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        findById();// for mapping buttons

        viewGone();
    }

//    @Override
//    public void onBackPressed() {
//        List<Fragment> fragments = getSupportFragmentManager().getFragments();
//        for(Fragment f : fragments){
//            if(f instanceof ApplianceCategoryViewFragment){
//                ((ApplianceCategoryViewFragment)f).onBackPressed();
//                if(){
//                    super.onBackPressed();
//                }
//            }
//        }
//    }

    public void viewGone(){
        applianceViewHeader.setVisibility(View.GONE);
    }

    public void intentExtras(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        position = bundle.getInt("position");
        roomModels = bundle.getParcelableArrayList("roomModels");
        fetchAllRooms();
    }

    public void findById(){
        allLightSwitch = findViewById(R.id.all_light_Switch);
        allFanSwitch = findViewById(R.id.all_fan_Switch);
        starredRoomSwitch = findViewById(R.id.starred_switch);
        roomDbDataManipulationQuery = new RoomDbDataManipulationQuery(this);
        applianceDbDataManipulationQuery = new ApplianceDbDataManipulationQuery(this);
        starredRoomDbDataManipulation = new StarredRoomDbDataManipulation(this);
        applianceViewHeader = findViewById(R.id.applianceViewHeader);
        sharedPreferences = getPreferences(MODE_PRIVATE);
        categoryApplianceView = sharedPreferences.getBoolean("categoryApplianceView", true);
        expandCollapseAnimation = new ExpandCollapseAnimation();
        coverView = findViewById(R.id.cover_view);
        coverView.setVisibility(View.GONE);

        intentExtras();

        fetchLightsData();
        fetchFansData();
        fetchOtherData();

        isStarred();

        instantiation();
    }

    public void instantiation(){
        allApplianceViewFragment = new AllApplianceViewFragment(roomId, fanApplianceModels, otherApplianceModels, lightApplianceModels);
        applianceCategoryViewFragment = new ApplianceCategoryViewFragment(roomId, fanApplianceModels, otherApplianceModels, lightApplianceModels);
        if (categoryApplianceView)
            setFragment(applianceCategoryViewFragment);
        else
            setFragment(allApplianceViewFragment);

    }

    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.control_frame, fragment);
        fragmentTransaction.commit();
    }

    public void applianceView(View view){
        if (applianceViewHeader.getVisibility()==View.GONE){
            expandCollapseAnimation.expand(applianceViewHeader);
            coverView.setVisibility(View.VISIBLE);
        }
        else {
            expandCollapseAnimation.collapse(applianceViewHeader);
            coverView.setVisibility(View.GONE);
        }
    }

    public void categoryView(View v) {
        coverView.setVisibility(View.VISIBLE);
        boolean categoryApplianceView = sharedPreferences.getBoolean("categoryApplianceView", true);
        if (!categoryApplianceView){
            sharedPreferences.edit().putBoolean("categoryApplianceView", true).apply();
            setFragment(applianceCategoryViewFragment);
        }
    }

    public void allApplianceView(View v){
        coverView.setVisibility(View.VISIBLE);
        boolean categoryApplianceView = sharedPreferences.getBoolean("categoryApplianceView", true);
        if (categoryApplianceView){
            sharedPreferences.edit().putBoolean("categoryApplianceView", false).apply();
            setFragment(allApplianceViewFragment);
        }
    }

    public void starredRoomSwitch(View v){
        if (starredRoomSwitch.isChecked())
            starredRoomDbDataManipulation.markRoomStarred(roomId);
        else
            starredRoomDbDataManipulation.unMarkRoomStarred(roomId);
    }

    public void fetchAllRooms(){
        String roomName = roomModels.get(position).getRoomName();
        Cursor res = roomDbDataManipulationQuery.getRoomDbData(0, roomName);
        while (res.moveToNext()){
            roomId = res.getInt(0);
        }
        Log.d(TAG, "fetchAllRooms: " + roomId);
    }

    public void fetchLightsData(){
        String applianceCategory = "Lights";
        this.lightApplianceModels = new ArrayList<>();
        Cursor res = applianceDbDataManipulationQuery.getApplianceDbData(applianceCategory, roomId);
        while (res.moveToNext()){
            String name = res.getString(1);
            boolean status = res.getInt(2) > 0;
            lightApplianceModels.add(new LightApplianceModel(name, status));
        }
    }

    public void fetchFansData(){
        String applianceCategory = "Fans";
        this.fanApplianceModels = new ArrayList<>();
        Cursor res = applianceDbDataManipulationQuery.getApplianceDbData(applianceCategory, roomId);
        while (res.moveToNext()){
            String name = res.getString(1);
            boolean status = res.getInt(2) > 0;
            fanApplianceModels.add(new FanApplianceModel(name, status));
        }
    }

    public void fetchOtherData(){
        String applianceCategory = "Other";
        this.otherApplianceModels = new ArrayList<>();
        Cursor res = applianceDbDataManipulationQuery.getApplianceDbData(applianceCategory, roomId);
        while (res.moveToNext()){
            String name = res.getString(1);
            boolean status = res.getInt(2) > 0;
            otherApplianceModels.add(new OtherApplianceModel(name, status));
        }
    }

    public void isStarred(){
        Cursor res = starredRoomDbDataManipulation.getStarredRoomDbData(roomId);
        if (res.getCount()==0)
            starredRoomSwitch.setChecked(false);
        else
            starredRoomSwitch.setChecked(true);
    }

    public void newAppliance(View v) {
        final AlertDialog.Builder applianceName = new AlertDialog.Builder(this);
        View new_appliance_alert_dialog_view = getLayoutInflater().inflate(R.layout.new_appliance_alert_dialog, null);
        final EditText inputApplianceName = new_appliance_alert_dialog_view.findViewById(R.id.input_appliance_name);
        applianceDropDownSpinner = new_appliance_alert_dialog_view.findViewById(R.id.appliance_drop_down);
        CardView createAppliance = new_appliance_alert_dialog_view.findViewById(R.id.createAppliance);

        // List of items for appliance drop down
        spinnerAdapter();

        applianceName.setView(new_appliance_alert_dialog_view);

        final AlertDialog alertDialog = applianceName.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        createAppliance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getApplianceName = inputApplianceName.getText().toString();
                if (getApplianceName.equals("")){
                    Toast.makeText(getApplicationContext(), "Please enter an appliance name", Toast.LENGTH_SHORT).show();
                }
                else {
                    String applianceCategory = applianceDropDownSpinner.getSelectedItem().toString();
                    if (applianceCategory.equals("Choose a category")){
                        Toast.makeText(getApplicationContext(), "Choose a category", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        applianceDbDataManipulationQuery.insertApplianceDbData(getApplianceName, 0, applianceCategory,
                                roomId);
                        if (applianceCategory.equals("Lights")) {
                            lightApplianceModels.add(new LightApplianceModel(getApplianceName, false));
                        }
                        if (applianceCategory.equals("Fans")) {
                            fanApplianceModels.add(new FanApplianceModel(getApplianceName, false));
                        }
                        if (applianceCategory.equals("Other")){
                            otherApplianceModels.add(new OtherApplianceModel(getApplianceName, false));
                        }
                        boolean categoryApplianceView = sharedPreferences.getBoolean("categoryApplianceView", true);
                        if (categoryApplianceView){
                            new ApplianceCategoryViewFragment(roomId, fanApplianceModels, otherApplianceModels, lightApplianceModels)
                                    .newApplianceHeaderVisibility(applianceCategory);
                        }
                        else {
                            new AllApplianceViewFragment(roomId, fanApplianceModels, otherApplianceModels, lightApplianceModels)
                                    .newAppliance(applianceCategory);
                        }

                        alertDialog.cancel();
                    }
                }
            }
        });
    }

    public void spinnerAdapter(){
        // List of items for appliance drop down
        ArrayAdapter<String> spinnerDropDownAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.applianceDropDownList));
        spinnerDropDownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        applianceDropDownSpinner.setAdapter(spinnerDropDownAdapter);
    }

    public void hideApplianceViewHeader(View v){
        expandCollapseAnimation.collapse(applianceViewHeader);
        coverView.setVisibility(View.GONE);
    }

}

