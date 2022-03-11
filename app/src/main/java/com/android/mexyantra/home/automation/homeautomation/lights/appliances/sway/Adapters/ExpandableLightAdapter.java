package com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Adapters;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Animation.LongPressRecyclerViewAnimation;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Models.LightApplianceModel;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.R;

import java.util.List;

import static android.content.ContentValues.TAG;

public class ExpandableLightAdapter extends RecyclerView.Adapter<ExpandableLightAdapter.ExpandableLightViewHolder> {

    private Context mContext;
    private List<LightApplianceModel> lightApplianceModels;

    private OnLightButtonListener onLightButtonListener;
    private OnLongLightClickListener onLongLightClickListener;
    private OnDeleteLightItemListener onDeleteLightItemListener;

    public ExpandableLightAdapter(Context context, List<LightApplianceModel> lightApplianceModels,
                                  OnLightButtonListener onLightButtonListener, OnLongLightClickListener onLongLightClickListener,
                                  OnDeleteLightItemListener onDeleteLightItemListener){
        mContext = context;
        this.lightApplianceModels = lightApplianceModels;
        this.onLightButtonListener = onLightButtonListener;
        this.onLongLightClickListener = onLongLightClickListener;
        this.onDeleteLightItemListener = onDeleteLightItemListener;
    }

    @NonNull
    @Override
    public ExpandableLightAdapter.ExpandableLightViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view  = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.light_recycler_view_item, viewGroup, false);
        return new ExpandableLightAdapter.ExpandableLightViewHolder(view, onLightButtonListener, onLongLightClickListener,
                onDeleteLightItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpandableLightAdapter.ExpandableLightViewHolder holder, int i) {
        holder.lightName.setText(lightApplianceModels.get(i).getLightName());
        holder.lightSwitch.setChecked(lightApplianceModels.get(i).isLightStatus());
    }

    @Override
    public int getItemCount() {
        return lightApplianceModels.size();
    }

    public static class ExpandableLightViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView lightName;
        Switch lightSwitch;
        CardView lightCardView, deleteLightItem;

        OnLightButtonListener onLightButtonListener;
        OnLongLightClickListener onLongLightClickListener;
        OnDeleteLightItemListener onDeleteLightItemListener;

        public ExpandableLightViewHolder(@NonNull View itemView, OnLightButtonListener onLightButtonListener,
                                         OnLongLightClickListener onLongLightClickListener,
                                         OnDeleteLightItemListener onDeleteLightItemListener) {
            super(itemView);

            lightName = itemView.findViewById(R.id.light_name);
            lightSwitch = itemView.findViewById(R.id.light_Switch);
            lightCardView = itemView.findViewById(R.id.light_item_view);
            deleteLightItem = itemView.findViewById(R.id.deleteLightBtn);

            this.onLightButtonListener = onLightButtonListener;
            this.onLongLightClickListener = onLongLightClickListener;
            this.onDeleteLightItemListener = onDeleteLightItemListener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            lightSwitch.setOnClickListener(this);
            deleteLightItem.setOnClickListener(this);
            deleteLightItem.setClickable(false);
        }

        @Override
        public void onClick(View v) {
            if(v.getId()==lightSwitch.getId())
                onLightButtonListener.OnLightButtonClick(getAdapterPosition());

            if(v.getId()==deleteLightItem.getId())
                onDeleteLightItemListener.onDeleteLightItemClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            Log.d(TAG, "onLongClick: Long click feedback");
            onLongLightClickListener.onLongLightClick(lightCardView, getAdapterPosition(), deleteLightItem);
            return true;
        }

    }

    public interface OnLightButtonListener{
        void OnLightButtonClick(int position);
    }

    public interface OnLongLightClickListener{
        void onLongLightClick(View v, int position, View view);
    }

    public interface OnDeleteLightItemListener{
        void onDeleteLightItemClick(int position);
    }

}