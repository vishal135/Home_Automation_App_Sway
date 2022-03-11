package com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.RecyclerViewSetup;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Adapters.CategoryHorizontalRecyclerViewAdapter;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Models.CategoryModel;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.R;

import java.util.ArrayList;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class CategoryRecyclerViewSetup {

    private RecyclerView categoryView;

    // data of categoryView
    private List<CategoryModel> categoryModels;

    public static String category;

    private static int position;

    //SnapHelper
    private SnapHelper snapHelper;
    private RecyclerView.LayoutManager layoutManager;

    private View view;
    private Context context;
    private CategoryHorizontalRecyclerViewAdapter.OnCategoryListener onCategoryListener;

    public CategoryRecyclerViewSetup(Context context, View view,
                                     CategoryHorizontalRecyclerViewAdapter.OnCategoryListener onCategoryListener) {
        this.view = view;
        this.context = context;
        this.onCategoryListener = onCategoryListener;

        categoryView = view.findViewById(R.id.categoryView);
    }

    public void categoryViewModelData(){
        categoryModels = new ArrayList<>();
        categoryModels.add(new CategoryModel(R.drawable.bedroom, "Bed Room", "bed"));
        categoryModels.add(new CategoryModel(R.drawable.living_room, "Living Room", "liv"));
        categoryModels.add(new CategoryModel(R.drawable.dining_room, "Dining Room", "din"));
        categoryModels.add(new CategoryModel(R.drawable.kitchen, "Kitchen", "kit"));
        categoryModels.add(new CategoryModel(R.drawable.bathroom, "Bath Room", "bath"));
        categoryModels.add(new CategoryModel(R.drawable.garden, "Garden/Garage", "gar"));

        setUpCategoryRecyclerView();
    }

    private void setUpCategoryRecyclerView() {
        CategoryHorizontalRecyclerViewAdapter categoryHorizontalRecyclerViewAdapter = new CategoryHorizontalRecyclerViewAdapter(context,
                categoryModels, onCategoryListener);
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        categoryView.setLayoutManager(layoutManager);

        //SnapHelper
        snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(categoryView);
        snapHelperListener();

        categoryView.setAdapter(categoryHorizontalRecyclerViewAdapter);
        categoryView.setPadding(72, 0, 72, 0);

        //notify Data set
        categoryHorizontalRecyclerViewAdapter.notifyDataSetChanged();
    }

    public void snapHelperListener(){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RecyclerView.ViewHolder viewHolder = categoryView.findViewHolderForAdapterPosition(position);
                CardView cardView = viewHolder.itemView.findViewById(R.id.cardView);
                cardView.animate().scaleY(1).scaleX(1).setDuration(350).setInterpolator(new AccelerateInterpolator()).start();
                category = categoryModels.get(position).getCategory();
                Log.d(TAG, "onRun: " + category);
            }
        }, 100);

        categoryView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                View view = snapHelper.findSnapView(layoutManager);
                int pos = layoutManager.getPosition(view);

                position = pos;

                RecyclerView.ViewHolder viewHolder = categoryView.findViewHolderForAdapterPosition(pos);
                CardView cardView = viewHolder.itemView.findViewById(R.id.cardView);

                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    cardView.animate().setDuration(350).scaleX(1).scaleY(1).setInterpolator(new AccelerateInterpolator()).start();
                    category = categoryModels.get(position).getCategory();
                    Log.d(TAG, "onScrollStateChanged: " + category);
                    updateRoomRecyclerViewAdapterData();
                }
                else {
                    cardView.animate().setDuration(350).scaleX(0.9f).scaleY(0.9f).setInterpolator(new AccelerateInterpolator()).start();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    public void updateRoomRecyclerViewAdapterData(){
        new RoomRecyclerViewSetup(context, view, false);
    }

}
