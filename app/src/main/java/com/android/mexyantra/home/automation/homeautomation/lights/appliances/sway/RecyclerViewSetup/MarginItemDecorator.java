package com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.RecyclerViewSetup;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MarginItemDecorator extends RecyclerView.ItemDecoration {

    private final int leftSpacing;
    private final int rightSpacing;
    private final int topSpacing;
    private final int bottomSpacing;
    private int displayMode;

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    public static final int GRID = 2;

    public MarginItemDecorator(int leftSpacing, int rightSpacing, int topSpacing, int bottomSpacing) {
        this(leftSpacing, rightSpacing, topSpacing, bottomSpacing, -1);
    }

    public MarginItemDecorator(int leftSpacing, int rightSpacing, int topSpacing, int bottomSpacing,  int displayMode) {
        this.leftSpacing = leftSpacing;
        this.rightSpacing = rightSpacing;
        this.topSpacing = topSpacing;
        this.bottomSpacing = bottomSpacing;
        this.displayMode = displayMode;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildViewHolder(view).getAdapterPosition();
        int itemCount = state.getItemCount();
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        setSpacingForDirection(outRect, layoutManager, position, itemCount);
    }

    private void setSpacingForDirection(Rect outRect,
                                        RecyclerView.LayoutManager layoutManager,
                                        int position,
                                        int itemCount) {

        // Resolve display mode automatically
        if (displayMode == -1) {
            displayMode = resolveDisplayMode(layoutManager);
        }

        switch (displayMode) {
            case HORIZONTAL:
                outRect.left = leftSpacing;
                outRect.right = position == itemCount - 1 ? rightSpacing : 0;
                outRect.top = topSpacing;
                outRect.bottom = bottomSpacing;
                break;
            case VERTICAL:
                outRect.left = leftSpacing;
                outRect.right = rightSpacing;
                outRect.top = topSpacing;
                outRect.bottom = position == itemCount - 1 ? bottomSpacing : 0;
                break;
            case GRID:
                if (layoutManager instanceof GridLayoutManager) {
                    GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                    int cols = gridLayoutManager.getSpanCount();
                    int rows = itemCount / cols;

                    outRect.left = leftSpacing;
                    outRect.right = position % cols == cols - 1 ? rightSpacing : 0;
                    outRect.top = topSpacing;
                    outRect.bottom = position / cols == rows - 1 ? bottomSpacing : 0;
                }
                break;
        }
    }

    private int resolveDisplayMode(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof GridLayoutManager) return GRID;
        if (layoutManager.canScrollHorizontally()) return HORIZONTAL;
        return VERTICAL;
    }
}
