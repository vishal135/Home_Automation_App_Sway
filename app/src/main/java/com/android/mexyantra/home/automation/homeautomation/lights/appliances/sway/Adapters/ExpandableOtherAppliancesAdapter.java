package com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Models.OtherApplianceModel;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.R;

import java.util.List;

public class ExpandableOtherAppliancesAdapter extends RecyclerView.Adapter<ExpandableOtherAppliancesAdapter.ExOtherAppliancesViewHolder> {

    private Context mContext;
    private List<OtherApplianceModel> otherApplianceModels;
    private OnOtherButtonListener onOtherButtonListener;
    private OnLongOtherClickListener onLongOtherClickListener;
    private OnDeleteOtherItemListener onDeleteOtherItemListener;

    public ExpandableOtherAppliancesAdapter(Context context, List<OtherApplianceModel> otherApplianceModels,
                                            OnOtherButtonListener onOtherButtonListener, OnLongOtherClickListener onLongOtherClickListener,
                                            OnDeleteOtherItemListener onDeleteOtherItemListener){
        this.mContext = context;
        this.otherApplianceModels = otherApplianceModels;
        this.onOtherButtonListener = onOtherButtonListener;
        this.onLongOtherClickListener = onLongOtherClickListener;
        this.onDeleteOtherItemListener = onDeleteOtherItemListener;
    }

    @NonNull
    @Override
    public ExpandableOtherAppliancesAdapter.ExOtherAppliancesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.other_recycler_view_item, parent, false);
        return new ExOtherAppliancesViewHolder(view, onOtherButtonListener, onLongOtherClickListener, onDeleteOtherItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpandableOtherAppliancesAdapter.ExOtherAppliancesViewHolder holder, int position) {
        holder.otherName.setText(otherApplianceModels.get(position).getOtherName());
        holder.otherStatus.setChecked(otherApplianceModels.get(position).isOtherStatus());
    }

    @Override
    public int getItemCount() {
        return otherApplianceModels.size();
    }

    public static class ExOtherAppliancesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        TextView otherName;
        Switch otherStatus;
        CardView otherCardView, deleteOtherItem;

        OnOtherButtonListener onOtherButtonListener;
        OnLongOtherClickListener onLongOtherClickListener;
        OnDeleteOtherItemListener onDeleteOtherItemListener;

        public ExOtherAppliancesViewHolder(@Nullable View itemView, OnOtherButtonListener onOtherButtonListener,
                                           OnLongOtherClickListener onLongOtherClickListener,
                                           OnDeleteOtherItemListener onDeleteOtherItemListener){
            super(itemView);

            otherName = itemView.findViewById(R.id.other_name);
            otherStatus = itemView.findViewById(R.id.other_Switch);
            otherCardView = itemView.findViewById(R.id.other_view_item);
            deleteOtherItem = itemView.findViewById(R.id.deleteOtherBtn);

            this.onOtherButtonListener = onOtherButtonListener;
            this.onLongOtherClickListener = onLongOtherClickListener;
            this.onDeleteOtherItemListener = onDeleteOtherItemListener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            otherStatus.setOnClickListener(this);
            deleteOtherItem.setOnClickListener(this);
            deleteOtherItem.setClickable(false);
        }

        @Override
        public void onClick(View v) {
            if(v.getId()==otherStatus.getId())
                onOtherButtonListener.OnOtherButtonClick(getAdapterPosition());
            if(v.getId()==deleteOtherItem.getId())
                onDeleteOtherItemListener.onDeleteOtherItemClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            onLongOtherClickListener.onLongOtherClick(otherCardView, getAdapterPosition(), deleteOtherItem);
            return true;
        }
    }

    public interface OnOtherButtonListener{
        void OnOtherButtonClick(int position);
    }

    public interface OnLongOtherClickListener{
        void onLongOtherClick(View v, int position, View view);
    }

    public interface OnDeleteOtherItemListener{
        void onDeleteOtherItemClick(int position);
    }

}
