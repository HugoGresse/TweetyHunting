package fr.xjet.tweetyhunting;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.gc.materialdesign.views.ButtonFloat;

import fr.xjet.tweetyhunting.view.CustomProgressBarCircularIndeterminate;

/**
 * Created by Hugo on 15/02/2015.
 */
public class StateButtonManager {

    private boolean                             mIsLoading;

    private CustomProgressBarCircularIndeterminate mProgressBar;
    private ButtonFloat                         mButton;
    private ImageView                           mButtonImageView;
    private Drawable                            mButtonIcon;
    private Animation                           mCurrentAnimation;
    private int                                 mAnimationDuration;

    public StateButtonManager(Context context,
                              CustomProgressBarCircularIndeterminate progressBarCircularIndeterminate,
                              ButtonFloat button) {
        mProgressBar = progressBarCircularIndeterminate;
        mButton = button;

        init();

        mIsLoading = false;
        mAnimationDuration = mProgressBar.getContext().getResources().getInteger(R.integer.animation_fade);

        updateVisibility();
    }

    private void init(){
        ((ViewGroup)mProgressBar.getParent()).removeView(mProgressBar);
        mButton.addView(mProgressBar);
        mButtonImageView = mButton.getIcon();
        mButtonIcon = mButton.getDrawableIcon();
    }

    private void updateVisibility(){

        if(mProgressBar == null){
            return;
        }

        if(mIsLoading){
            mProgressBar.resetProgress();
            mProgressBar.animate()
                    .alpha(1f)
                    .setDuration(mAnimationDuration)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mProgressBar.setVisibility(View.VISIBLE);
                        }
                    });
            mButtonImageView.animate()
                    .alpha(0f)
                    .setDuration(mAnimationDuration);
        } else {
            mButtonImageView.animate()
                    .alpha(1f)
                    .setDuration(mAnimationDuration);

            mProgressBar.animate()
                    .alpha(0f)
                    .setDuration(mAnimationDuration)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mProgressBar.resetProgress();
                        }
                    });

        }
    }

    public void showLoader(){
        mIsLoading = true;
        updateVisibility();
    }

    public void hideLoader(){
        mIsLoading = false;
        updateVisibility();
    }


}
