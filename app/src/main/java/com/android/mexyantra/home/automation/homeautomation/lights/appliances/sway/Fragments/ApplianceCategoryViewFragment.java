package com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class ApplianceCategoryViewFragment extends Fragment implements ExpandableLightAdapter.OnLightButtonListener,
        ExpandableFanAdapter.OnFanButtonListener, ExpandableOtherAppliancesAdapter.OnOtherButtonListener,
        ExpandableLightAdapter.OnLongLightClickListener, ExpandableFanAdapter.OnLongFanClickListener,
        ExpandableOtherAppliancesAdapter.OnLongOtherClickListener, ExpandableLightAdapter.OnDeleteLightItemListener,
        ExpandableFanAdapter.OnDeleteFanItemListener, ExpandableOtherAppliancesAdapter.OnDeleteOtherItemListener {

    private View view;
    private Context context;

    private RecyclerView lightsRecyclerView, fansRecyclerView, otherAppliancesRecyclerView;

    // Appliance Db
    private ApplianceDbDataManipulationQuery applianceDbDataManipulationQuery;

    // expandable adapters
    private ExpandableLightAdapter expandableLightAdapter;
    private ExpandableFanAdapter expandableFanAdapter;
    private ExpandableOtherAppliancesAdapter expandableOtherAppliancesAdapter;

    private CardView lightHeader, fanHeader, otherHeader;

    private ExpandCollapseAnimation expandCollapseAnimation;

    private int status;

    private View prevLightView, prevFanView, prevOtherView, prevDeleteLightView, prevDeleteFanView, prevDeleteOtherView;
    private int preLightViewPos = -1, preFanViewPos = -1, preOtherViewPos = -1;
    private boolean lightShrink = false, fanShrink = false, otherShrink = false;

    private RelativeLayout lights, fans, other;

    private List<FanApplianceModel> fanApplianceModels;
    private List<OtherApplianceModel> otherApplianceModels;
    private List<LightApplianceModel> lightApplianceModels;

    // for transferring data
    private byte[] bufApplianceNamePosStatus; //used to sending information to esp is a form of byte

    private int roomId;

    public ApplianceCategoryViewFragment() {
        // Required empty public constructor
    }

    public ApplianceCategoryViewFragment(int roomId, List<FanApplianceModel> fanApplianceModels,
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
        view = inflater.inflate(R.layout.fragment_appliance_category_view, container, false);

        ControlActivity.applianceCategoryViewFragmentView = view;

        context = getActivity();

        findById(view);

        viewGone();

        //adapter
        setUpRecyclerView();

        toExpandClickListener();

        categoryHeaderVisibility();

        return view;
    }

    public void categoryHeaderVisibility(){
        if(lightApplianceModels.isEmpty()){
            lightHeader.setClickable(false);
            lightHeader.setVisibility(View.GONE);
        }

        if(fanApplianceModels.isEmpty()){
            fanHeader.setClickable(false);
            fanHeader.setVisibility(View.GONE);
        }

        if(otherApplianceModels.isEmpty()){
            otherHeader.setClickable(false);
            otherHeader.setVisibility(View.GONE);
        }
    }

    public void findById(View view){
        lightsRecyclerView = view.findViewById(R.id.lights_recycler_view);
        fansRecyclerView = view.findViewById(R.id.fans_recycler_view);
        otherAppliancesRecyclerView = view.findViewById(R.id.other_recycler_view);
        applianceDbDataManipulationQuery = new ApplianceDbDataManipulationQuery(context);
        expandCollapseAnimation = new ExpandCollapseAnimation();
        lights = view.findViewById(R.id.lights);
        fans = view.findViewById(R.id.fans);
        other = view.findViewById(R.id.other);
        lightHeader = view.findViewById(R.id.lightHeader);
        fanHeader = view.findViewById(R.id.fanHeader);
        otherHeader = view.findViewById(R.id.otherHeader);
        bufApplianceNamePosStatus = new byte[1024];
    }

    public void viewGone(){
        lights.setVisibility(View.GONE);
        fans.setVisibility(View.GONE);
        other.setVisibility(View.GONE);
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
                if (lightApplianceModels.size()==0){
                    lightHeader.setVisibility(View.GONE);
                    lightHeader.setClickable(false);
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
                if (fanApplianceModels.size()==0){
                    fanHeader.setVisibility(View.GONE);
                    fanHeader.setClickable(false);
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
                if (otherApplianceModels.size()==0){
                    otherHeader.setVisibility(View.GONE);
                    otherHeader.setClickable(false);
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

    private void toExpandClickListener(){
        lightHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandLights();
            }
        });

        fanHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandFans();
            }
        });

        otherHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandOtherAppliances();
            }
        });
    }

    private void expandLights(){
        if (lights.getVisibility()==View.GONE){
            if (fans.getVisibility()==View.VISIBLE || other.getVisibility()==View.VISIBLE){
                new ExpandCollapseAnimation().collapse(fans);
                new ExpandCollapseAnimation().collapse(other);
            }
            expandCollapseAnimation.expand(lights);
        }
        else {
            expandCollapseAnimation.collapse(lights);
        }
    }

    private void expandFans() {
        if (fans.getVisibility()==View.GONE){
            if (other.getVisibility()==View.VISIBLE || lights.getVisibility()==View.VISIBLE){
                new ExpandCollapseAnimation().collapse(lights);
                new ExpandCollapseAnimation().collapse(other);
            }
            expandCollapseAnimation.expand(fans);
        }
        else {
            expandCollapseAnimation.collapse(fans);
        }
    }

    private void expandOtherAppliances() {
        if (other.getVisibility()==View.GONE){
            if (fans.getVisibility()==View.VISIBLE || lights.getVisibility()==View.VISIBLE){
                new ExpandCollapseAnimation().collapse(fans);
                new ExpandCollapseAnimation().collapse(lights);
            }
            expandCollapseAnimation.expand(other);
        }
        else {
            expandCollapseAnimation.collapse(other);
        }
    }

    public void newApplianceHeaderVisibility(String applianceCategory){
        findById(ControlActivity.applianceCategoryViewFragmentView);
        if (applianceCategory.equals("Lights")) {
            lightHeader.setVisibility(View.VISIBLE);
            lightHeader.setClickable(true);
            expandCollapseAnimation.collapse(lights);
        }
        else if (applianceCategory.equals("Fans")) {
            fanHeader.setVisibility(View.VISIBLE);
            fanHeader.setClickable(true);
            expandCollapseAnimation.collapse(fans);
        }
        else if (applianceCategory.equals("Other")){
            otherHeader.setVisibility(View.VISIBLE);
            otherHeader.setClickable(true);
            expandCollapseAnimation.collapse(other);
        }
    }

}
