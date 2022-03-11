package com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Activity.ControlActivity;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Adapters.ExpandableFanAdapter;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Adapters.ExpandableLightAdapter;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Adapters.ExpandableOtherAppliancesAdapter;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Animation.ExpandCollapseAnimation;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Animation.LongPressRecyclerViewAnimation;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Communication.Client;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.DatabaseHelper.ApplianceDbDataManipulationQuery;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Models.FanApplianceModel;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Models.LightApplianceModel;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Models.OtherApplianceModel;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.R;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.RecyclerViewSetup.MarginItemDecorator;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class AllApplianceViewFragment extends Fragment implements ExpandableLightAdapter.OnLightButtonListener,
        ExpandableFanAdapter.OnFanButtonListener, ExpandableOtherAppliancesAdapter.OnOtherButtonListener,
        ExpandableLightAdapter.OnLongLightClickListener, ExpandableFanAdapter.OnLongFanClickListener,
        ExpandableOtherAppliancesAdapter.OnLongOtherClickListener, ExpandableLightAdapter.OnDeleteLightItemListener,
        ExpandableFanAdapter.OnDeleteFanItemListener, ExpandableOtherAppliancesAdapter.OnDeleteOtherItemListener {

    private View view;
    private Context context;

    private RecyclerView lightsRecyclerView, fansRecyclerView, otherAppliancesRecyclerView;

    private List<FanApplianceModel> fanApplianceModels;
    private List<OtherApplianceModel> otherApplianceModels;
    private List<LightApplianceModel> lightApplianceModels;

    // Appliance Db
    private ApplianceDbDataManipulationQuery applianceDbDataManipulationQuery;

    // expandable adapters
    private ExpandableLightAdapter expandableLightAdapter;
    private ExpandableFanAdapter expandableFanAdapter;
    private ExpandableOtherAppliancesAdapter expandableOtherAppliancesAdapter;

    private int status;

    private View prevLightView, prevFanView, prevOtherView, prevDeleteLightView, prevDeleteFanView, prevDeleteOtherView;
    private int preLightViewPos = -1, preFanViewPos = -1, preOtherViewPos = -1;
    private boolean lightShrink = false, fanShrink = false, otherShrink = false;

    private RelativeLayout lights, fans, other;

    // for transferring data
    private byte[] bufApplianceNamePosStatus; //used to sending information to esp is a form of byte

    private int roomId;

    public AllApplianceViewFragment() {
        // Required empty public constructor
    }

    public AllApplianceViewFragment(int roomId, List<FanApplianceModel> fanApplianceModels,
                                    List<OtherApplianceModel> otherApplianceModels, List<LightApplianceModel> lightApplianceModels) {
        this.roomId = roomId;
        this.lightApplianceModels = lightApplianceModels;
        this.fanApplianceModels = fanApplianceModels;
        this.otherApplianceModels = otherApplianceModels;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_all_appliance_view, container, false);

        ControlActivity.allApplianceViewFragmentView = view;

        context = getActivity();

        findById(view);

        //adapter
        setUpRecyclerView();

        return view;
    }

    public void findById(View view){
        lightsRecyclerView = view.findViewById(R.id.lights_2_recycler_view);
        fansRecyclerView = view.findViewById(R.id.fans_2_recycler_view);
        otherAppliancesRecyclerView = view.findViewById(R.id.other_2_recycler_view);
        applianceDbDataManipulationQuery = new ApplianceDbDataManipulationQuery(context);
        lights = view.findViewById(R.id.lights_2);
        fans = view.findViewById(R.id.fans_2);
        other = view.findViewById(R.id.other_2);
        bufApplianceNamePosStatus = new byte[1024];
    }

    public void onBackPressed(){
        LongPressRecyclerViewAnimation longPressRecyclerViewAnimation_1 = new LongPressRecyclerViewAnimation();
        LongPressRecyclerViewAnimation longPressRecyclerViewAnimation_2 = new LongPressRecyclerViewAnimation();
        LongPressRecyclerViewAnimation longPressRecyclerViewAnimation_3 = new LongPressRecyclerViewAnimation();
        if (lightShrink || fanShrink || otherShrink){
            if (lightShrink && preLightViewPos!=-1) {
                longPressRecyclerViewAnimation_1.expandWidth(prevLightView);
                lightShrink = false;
            }
            if (fanShrink && preFanViewPos!=-1) {
                longPressRecyclerViewAnimation_2.expandWidth(prevFanView);
                fanShrink = false;
            }
            if (otherShrink && preOtherViewPos!=-1) {
                longPressRecyclerViewAnimation_3.expandWidth(prevOtherView);
                otherShrink = false;
            }
        }
        else {
            //super.onBackPressed();
        }
    }

    private void setUpRecyclerView(){

        // expandable light adapter
        setExpandableLightAdapter();

        // expandable fan adapter
        setExpandableFanAdapter();

        // expandable other adapter
        setExpandableOtherAdapter();
    }

    public void setExpandableLightAdapter(){
        expandableLightAdapter = new ExpandableLightAdapter(context, lightApplianceModels,
                this, this, this);
        lightsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        lightsRecyclerView.addItemDecoration(new MarginItemDecorator(getResources().getDimensionPixelSize(R.dimen.ex_view_left_spacing),
                getResources().getDimensionPixelSize(R.dimen.ex_view_right_spacing),
                getResources().getDimensionPixelSize(R.dimen.ex_view_top_spacing),
                getResources().getDimensionPixelSize(R.dimen.ex_view_bottom_spacing),
                MarginItemDecorator.VERTICAL));
        lightsRecyclerView.setAdapter(expandableLightAdapter);
        expandableLightAdapter.notifyDataSetChanged();
    }

    public void setExpandableFanAdapter(){
        expandableFanAdapter = new ExpandableFanAdapter(context, fanApplianceModels, this,
                this, this);
        fansRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        fansRecyclerView.addItemDecoration(new MarginItemDecorator(getResources().getDimensionPixelSize(R.dimen.ex_view_left_spacing),
                getResources().getDimensionPixelSize(R.dimen.ex_view_right_spacing),
                getResources().getDimensionPixelSize(R.dimen.ex_view_top_spacing),
                getResources().getDimensionPixelSize(R.dimen.ex_view_bottom_spacing),
                MarginItemDecorator.VERTICAL));
        fansRecyclerView.setAdapter(expandableFanAdapter);
        expandableFanAdapter.notifyDataSetChanged();
    }

    public void setExpandableOtherAdapter(){
        expandableOtherAppliancesAdapter = new ExpandableOtherAppliancesAdapter(context,
                otherApplianceModels, this, this, this);
        otherAppliancesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        otherAppliancesRecyclerView.addItemDecoration(new MarginItemDecorator(getResources().getDimensionPixelSize(R.dimen.ex_view_left_spacing),
                getResources().getDimensionPixelSize(R.dimen.ex_view_right_spacing),
                getResources().getDimensionPixelSize(R.dimen.ex_view_top_spacing),
                getResources().getDimensionPixelSize(R.dimen.ex_view_bottom_spacing),
                MarginItemDecorator.VERTICAL));
        otherAppliancesRecyclerView.setAdapter(expandableOtherAppliancesAdapter);
        expandableOtherAppliancesAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnLightButtonClick(int position) {

        Log.d(TAG, "OnLightButtonClick: Light Button is clicked");
        boolean tableUpdated;
        String applianceName = lightApplianceModels.get(position).getLightName();
        Cursor res = applianceDbDataManipulationQuery.getAppliancesStatus(applianceName, roomId);
        while (res.moveToNext()){
            status = res.getInt(2);
        }

        if(status==1){
            tableUpdated = applianceDbDataManipulationQuery.setAppliancesStatus(0, applianceName, roomId);
            if (tableUpdated){
                // send status data to NODE-MCU
                sendStatus(applianceName, 0);
            }
        }
        else{
            tableUpdated = applianceDbDataManipulationQuery.setAppliancesStatus(1, applianceName, roomId);
            if (tableUpdated){
                // send status data to NODE-MCU
                sendStatus(applianceName, 1);
            }
        }

    }

    @Override
    public void OnFanButtonClick(int position) {

        Log.d(TAG, "OnFanButtonClick: Fan Button is clicked");
        boolean tableUpdated;
        String applianceName = fanApplianceModels.get(position).getFanName();
        Cursor res = applianceDbDataManipulationQuery.getAppliancesStatus(applianceName, roomId);
        while (res.moveToNext()){
            status = res.getInt(2);
        }

        if(status==1){
            tableUpdated = applianceDbDataManipulationQuery.setAppliancesStatus(0, applianceName, roomId);
            if (tableUpdated){
                // send status data to NODE-MCU
                sendStatus(applianceName, 0);
            }
        }
        else{
            tableUpdated = applianceDbDataManipulationQuery.setAppliancesStatus(1, applianceName, roomId);
            if (tableUpdated){
                // send status data to NODE-MCU
                sendStatus(applianceName, 1);
            }
        }

    }

    @Override
    public void OnOtherButtonClick(int position) {

        Log.d(TAG, "OnOtherButtonClick: Other Button is clicked");
        boolean tableUpdated;
        String applianceName = otherApplianceModels.get(position).getOtherName();
        Cursor res = applianceDbDataManipulationQuery.getAppliancesStatus(applianceName, roomId);
        while (res.moveToNext()){
            status = res.getInt(2);
        }

        if(status==1){
            tableUpdated = applianceDbDataManipulationQuery.setAppliancesStatus(0, applianceName, roomId);
            if (tableUpdated){
                // send status data to NODE-MCU
                sendStatus(applianceName, 0);
            }
        }
        else{
            tableUpdated = applianceDbDataManipulationQuery.setAppliancesStatus(1, applianceName, roomId);
            if (tableUpdated){
                // send status data to NODE-MCU
                sendStatus(applianceName, 1);
            }
        }

    }

    @Override
    public void onLongLightClick(View lightCardView, final int position, View deleteLightBtn) {
        LongPressRecyclerViewAnimation longPressRecyclerViewAnimation_1 = new LongPressRecyclerViewAnimation();
        LongPressRecyclerViewAnimation longPressRecyclerViewAnimation_2 = new LongPressRecyclerViewAnimation();

        if(preLightViewPos==position){
            if(lightShrink) {
                longPressRecyclerViewAnimation_1.expandWidth(lightCardView);
                deleteLightBtn.setClickable(false);
                lightShrink = false;
            }
            else {
                longPressRecyclerViewAnimation_1.shrinkWidth(lightCardView);
                deleteLightBtn.setClickable(true);
                lightShrink = true;
            }
        }
        else {
            longPressRecyclerViewAnimation_1.shrinkWidth(lightCardView);
            deleteLightBtn.setClickable(true);
            lightShrink = true;
            if(preLightViewPos!=-1) {
                longPressRecyclerViewAnimation_2.expandWidth(prevLightView);
                prevDeleteLightView.setClickable(false);
            }
        }

        prevLightView = lightCardView;
        preLightViewPos = position;
        prevDeleteLightView = deleteLightBtn;
    }

    @Override
    public void onLongFanClick(View fanCardView, int position, View deleteFanBtn) {
        LongPressRecyclerViewAnimation longPressRecyclerViewAnimation_1 = new LongPressRecyclerViewAnimation();
        LongPressRecyclerViewAnimation longPressRecyclerViewAnimation_2 = new LongPressRecyclerViewAnimation();

        if(preFanViewPos==position){
            if(fanShrink) {
                longPressRecyclerViewAnimation_1.expandWidth(fanCardView);
                deleteFanBtn.setClickable(false);
                fanShrink = false;
            }
            else {
                longPressRecyclerViewAnimation_1.shrinkWidth(fanCardView);
                deleteFanBtn.setClickable(true);
                fanShrink = true;
            }
        }
        else {
            longPressRecyclerViewAnimation_1.shrinkWidth(fanCardView);
            deleteFanBtn.setClickable(true);
            fanShrink = true;
            if(preFanViewPos!=-1) {
                longPressRecyclerViewAnimation_2.expandWidth(prevFanView);
                prevDeleteFanView.setClickable(false);
            }
        }

        prevFanView = fanCardView;
        preFanViewPos = position;
        prevDeleteFanView = deleteFanBtn;
    }

    @Override
    public void onLongOtherClick(View otherCardView, int position, View deleteOtherBtn) {
        LongPressRecyclerViewAnimation longPressRecyclerViewAnimation_1 = new LongPressRecyclerViewAnimation();
        LongPressRecyclerViewAnimation longPressRecyclerViewAnimation_2 = new LongPressRecyclerViewAnimation();

        if(preOtherViewPos==position){
            if(otherShrink) {
                longPressRecyclerViewAnimation_1.expandWidth(otherCardView);
                deleteOtherBtn.setClickable(false);
                otherShrink = false;
            }
            else {
                longPressRecyclerViewAnimation_1.shrinkWidth(otherCardView);
                deleteOtherBtn.setClickable(true);
                otherShrink = true;
            }
        }
        else {
            longPressRecyclerViewAnimation_1.shrinkWidth(otherCardView);
            deleteOtherBtn.setClickable(true);
            otherShrink = true;
            if(preOtherViewPos!=-1) {
                longPressRecyclerViewAnimation_2.expandWidth(prevOtherView);
                prevDeleteOtherView.setClickable(false);
            }
        }

        prevOtherView = otherCardView;
        preOtherViewPos = position;
        prevDeleteOtherView = deleteOtherBtn;
    }

    @Override
    public void onDeleteLightItemClick(final int position) {
        Log.d(TAG, "onDeleteLightItemClick: Clicked");
        final String applianceName = lightApplianceModels.get(position).getLightName();
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(context);
        View deleteConfirmationDialog = getLayoutInflater().inflate(R.layout.delete_confirmation_dialog, null);
        final TextView displayText = deleteConfirmationDialog.findViewById(R.id.confirmation_text);
        String ConfirmationText = "Are you sure to delete '" + applianceName + "'?";
        displayText.setText(ConfirmationText);
        CardView positiveBtn = deleteConfirmationDialog.findViewById(R.id.positiveBtn);
        CardView negativeBtn = deleteConfirmationDialog.findViewById(R.id.negativeBtn);

        deleteDialog.setView(deleteConfirmationDialog);

        final AlertDialog alertDialog = deleteDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applianceDbDataManipulationQuery.deleteAppliance(applianceName, roomId);
                lightApplianceModels.remove(position);
                expandableLightAdapter.notifyItemRemoved(position);
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
    public void onDeleteFanItemClick(final int position) {
        final String applianceName = fanApplianceModels.get(position).getFanName();
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(context);
        View deleteConfirmationDialog = getLayoutInflater().inflate(R.layout.delete_confirmation_dialog, null);
        final TextView displayText = deleteConfirmationDialog.findViewById(R.id.confirmation_text);
        String ConfirmationText = "Are you sure to delete '" + applianceName + "'?";
        displayText.setText(ConfirmationText);
        CardView positiveBtn = deleteConfirmationDialog.findViewById(R.id.positiveBtn);
        CardView negativeBtn = deleteConfirmationDialog.findViewById(R.id.negativeBtn);

        deleteDialog.setView(deleteConfirmationDialog);

        final AlertDialog alertDialog = deleteDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applianceDbDataManipulationQuery.deleteAppliance(applianceName, roomId);
                fanApplianceModels.remove(position);
                expandableFanAdapter.notifyItemRemoved(position);
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
    public void onDeleteOtherItemClick(final int position) {
        Log.d(TAG, "onDeleteOtherItemClick: Clicked");
        final String applianceName = otherApplianceModels.get(position).getOtherName();
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(context);
        View deleteConfirmationDialog = getLayoutInflater().inflate(R.layout.delete_confirmation_dialog, null);
        final TextView displayText = deleteConfirmationDialog.findViewById(R.id.confirmation_text);
        String ConfirmationText = "Are you sure to delete '" + applianceName + "'?";
        displayText.setText(ConfirmationText);
        CardView positiveBtn = deleteConfirmationDialog.findViewById(R.id.positiveBtn);
        CardView negativeBtn = deleteConfirmationDialog.findViewById(R.id.negativeBtn);

        deleteDialog.setView(deleteConfirmationDialog);

        final AlertDialog alertDialog = deleteDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applianceDbDataManipulationQuery.deleteAppliance(applianceName, roomId);
                otherApplianceModels.remove(position);
                expandableOtherAppliancesAdapter.notifyItemRemoved(position);
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

    public void sendStatus(String applianceName, int status){
        Log.d(TAG, "sendStatus: ");
        String applianceNameFromDb;
        int count = 1;
        int applianceNamePosition = -1;

        Cursor res = applianceDbDataManipulationQuery.getApplianceDbData(roomId);
        while (res.moveToNext()){
            applianceNameFromDb = res.getString(1);
            if (applianceName.equals(applianceNameFromDb)){
                applianceNamePosition = count;
                break;
            }
            count++;
        }

        if (applianceNamePosition!=-1){
            String applianceNamePosStatus = "" + applianceNamePosition + status;
            bufApplianceNamePosStatus = null;
            bufApplianceNamePosStatus = (applianceNamePosStatus).getBytes();
            Client a = new Client(bufApplianceNamePosStatus);
            a.run();
        }

    }

    public void newAppliance(String applianceCategory){
        findById(ControlActivity.allApplianceViewFragmentView);
        if (applianceCategory.equals("Lights")) {
            new ExpandCollapseAnimation().collapse(lights);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new ExpandCollapseAnimation().expand(lights);
                }
            }, 350);
        }
        else if (applianceCategory.equals("Fans")) {
            new ExpandCollapseAnimation().collapse(fans);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new ExpandCollapseAnimation().expand(fans);
                }
            }, 350);
        }
        else if (applianceCategory.equals("Other")){
            new ExpandCollapseAnimation().collapse(other);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new ExpandCollapseAnimation().expand(other);
                }
            }, 350);
        }
    }

}
