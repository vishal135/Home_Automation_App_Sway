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

import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.DatabaseHelper.RoomDbDataManipulationQuery;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Models.RoomModel;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.R;

import java.util.List;

public class RoomRecyclerViewAdapter extends RecyclerView.Adapter<RoomRecyclerViewAdapter.RoomViewHolder> {

    private List<RoomModel> roomModels;
    private Context context;
    private OnRoomListener onRoomListener;
    private OnLongRoomClickListener onLongRoomClickListener;
    private OnDeleteRoomItemListener onDeleteRoomItemListener;

    private RoomDbDataManipulationQuery roomDbDataManipulationQuery;

    public RoomRecyclerViewAdapter(Context context, List<RoomModel> roomModels, OnRoomListener onRoomListener,
                                   OnLongRoomClickListener onLongRoomClickListener,
                                   OnDeleteRoomItemListener onDeleteRoomItemListener) {
        this.context = context;
        this.onRoomListener = onRoomListener;
        this.onLongRoomClickListener = onLongRoomClickListener;
        this.onDeleteRoomItemListener = onDeleteRoomItemListener;

        this.roomModels = roomModels;
        this.roomDbDataManipulationQuery = new RoomDbDataManipulationQuery(context);
    }

    @NonNull
    @Override
    public RoomRecyclerViewAdapter.RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_view_item, parent, false);
        return new RoomViewHolder(view, onRoomListener, onLongRoomClickListener, onDeleteRoomItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomRecyclerViewAdapter.RoomViewHolder holder, int position) {
        holder.roomName.setText(roomModels.get(position).getRoomName());
    }

    @Override
    public int getItemCount() {
        return roomModels.size();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView roomName;
        Switch status;
        CardView roomCardView, deleteRoomItem;

        OnRoomListener onRoomListener;
        OnDeleteRoomItemListener onDeleteRoomItemListener;
        OnLongRoomClickListener onLongRoomClickListener;

        public RoomViewHolder(@NonNull View itemView, OnRoomListener onRoomListener, OnLongRoomClickListener onLongRoomClickListener,
                              OnDeleteRoomItemListener onDeleteRoomItemListener) {
            super(itemView);

            roomName = itemView.findViewById(R.id.room_name);
            status = itemView.findViewById(R.id.all_room_switch);
            roomCardView = itemView.findViewById(R.id.room_card_view);
            deleteRoomItem = itemView.findViewById(R.id.deleteRoomBtn);

            this.onRoomListener = onRoomListener;
            this.onDeleteRoomItemListener = onDeleteRoomItemListener;
            this.onLongRoomClickListener = onLongRoomClickListener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            roomCardView.setOnClickListener(this);
            roomCardView.setOnLongClickListener(this);
            deleteRoomItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            if (view.getId() == roomCardView.getId())
                onRoomListener.OnRoomClick(roomCardView, getAdapterPosition());
            if (view.getId()==deleteRoomItem.getId())
                onDeleteRoomItemListener.onDeleteRoomItem(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            onLongRoomClickListener.onLongRoomClick(roomCardView, getAdapterPosition(), deleteRoomItem);
            return true;
        }
    }

    public interface OnRoomListener{
        void OnRoomClick(View v, int i);
    }

    public interface OnLongRoomClickListener{
        void onLongRoomClick(View v, int position, View view);
    }

    public interface OnDeleteRoomItemListener {
        void onDeleteRoomItem(int position);
    }
}
