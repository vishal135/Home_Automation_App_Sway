package com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Models.FanApplianceModel;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.R;

import java.util.List;

public class ExpandableFanAdapter extends RecyclerView.Adapter<ExpandableFanAdapter.ExpandableFanViewHolder> {

    private Context mContext;
    private List<FanApplianceModel> fanApplianceModels;
    private OnFanButtonListener onFanButtonListener;
    private OnLongFanClickListener onLongFanClickListener;
    private OnDeleteFanItemListener onDeleteFanItemListener;

    public ExpandableFanAdapter(Context context, List<FanApplianceModel> fanApplianceModels, OnFanButtonListener onFanButtonListener,
                                OnLongFanClickListener onLongFanClickListener, OnDeleteFanItemListener onDeleteFanItemListener){
        this.mContext = context;
        this.fanApplianceModels = fanApplianceModels;
        this.onFanButtonListener = onFanButtonListener;
        this.onLongFanClickListener = onLongFanClickListener;
        this.onDeleteFanItemListener = onDeleteFanItemListener;
    }

    @NonNull
    @Override
    public ExpandableFanAdapter.ExpandableFanViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view  = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fan_recycler_view_item, viewGroup, false);
        return new ExpandableFanAdapter.ExpandableFanViewHolder(view, onFanButtonListener, onLongFanClickListener, onDeleteFanItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpandableFanAdapter.ExpandableFanViewHolder holder, int i) {
        holder.fanName.setText(fanApplianceModels.get(i).getFanName());
        holder.fanSwitch.setChecked(fanApplianceModels.get(i).isFanStatus());
    }

    @Override
    public int getItemCount() {
        return fanApplianceModels.size();
    }

    public static class ExpandableFanViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView fanName;
        Switch fanSwitch;
        CardView fanCardView, deleteFanItem;

        OnFanButtonListener onFanButtonListener;
        OnLongFanClickListener onLongFanClickListener;
        OnDeleteFanItemListener onDeleteFanItemListener;

        public ExpandableFanViewHolder(@NonNull View itemView, OnFanButtonListener onFanButtonListener,
                                       OnLongFanClickListener onLongFanClickListener,
                                       OnDeleteFanItemListener onDeleteFanItemListener) {
            super(itemView);

            fanName = itemView.findViewById(R.id.fan_name);
            fanSwitch = itemView.findViewById(R.id.fan_Switch);
            fanCardView = itemView.findViewById(R.id.fan_item_view);
            deleteFanItem = itemView.findViewById(R.id.deleteFanBtn);

            this.onFanButtonListener = onFanButtonListener;
            this.onLongFanClickListener = onLongFanClickListener;
            this.onDeleteFanItemListener = onDeleteFanItemListener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            fanSwitch.setOnClickListener(this);
            deleteFanItem.setOnClickListener(this);
            deleteFanItem.setClickable(false);
        }

        @Override
        public void onClick(View v) {
            if(v.getId()==fanSwitch.getId())
                onFanButtonListener.OnFanButtonClick(getAdapterPosition());
            if(v.getId()==deleteFanItem.getId())
                onDeleteFanItemListener.onDeleteFanItemClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            onLongFanClickListener.onLongFanClick(fanCardView, getAdapterPosition(), deleteFanItem);
            return true;
        }
    }

    public interface OnFanButtonListener{
        void OnFanButtonClick(int position);
    }

    public interface OnLongFanClickListener{
        void onLongFanClick(View v, int position, View view);
    }

    public interface OnDeleteFanItemListener{
        void onDeleteFanItemClick(int position);
    }

}
