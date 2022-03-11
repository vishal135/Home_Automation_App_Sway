package com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Animation;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;

public class LongPressRecyclerViewAnimation {

    private View header;

    public void expandWidth(final View header) {

        this.header = header;
        int finalWidth = header.getWidth();
        int changeWidth = finalWidth + 180;

        ValueAnimator mAnimator = slideAnimator(finalWidth, changeWidth);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mAnimator.start();
    }

    public void shrinkWidth(final View header) {

        this.header = header;
        int finalWidth = header.getWidth();
        int changeWidth = finalWidth - 180;

        ValueAnimator mAnimator = slideAnimator(finalWidth, changeWidth);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mAnimator.start();
    }

    private ValueAnimator slideAnimator(int start, int end) {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = header.getLayoutParams();
                layoutParams.width = value;
                header.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

}
