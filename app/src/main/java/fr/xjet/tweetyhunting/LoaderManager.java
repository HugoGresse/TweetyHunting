package fr.xjet.tweetyhunting;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

import fr.xjet.tweetyhunting.view.CustomProgressBarCircularIndeterminate;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Hugo on 11/02/2015.
 */
public class LoaderManager {

    private static CustomProgressBarCircularIndeterminate sProgressBar;
    private static GifImageView sImageView;

    public LoaderManager(CustomProgressBarCircularIndeterminate progressBar, GifImageView imageView) {
        sProgressBar = progressBar;
        sImageView = imageView;
    }

    public static void displayLoader(){

        if(sProgressBar != null && sImageView != null){
            sProgressBar.resetProgress();

            ((View)sImageView).setAlpha(0);
            sProgressBar.setAlpha(1);
        }

    }

    public static void hideLoader(){

        if(sProgressBar != null && sImageView != null && sProgressBar.getAlpha() != 0.0){

            ((View)sImageView).setAlpha(0);
            sImageView.setVisibility(View.VISIBLE);

            AnimatorSet animatorSet = new AnimatorSet();

            ObjectAnimator alphaOutAnimation = ObjectAnimator.ofFloat(sImageView, View.ALPHA, 1, 0);
            ObjectAnimator alphaInAnimation = ObjectAnimator.ofFloat(sImageView, View.ALPHA, 0, 1);
            alphaOutAnimation.setTarget(sProgressBar);
            alphaInAnimation.setTarget(sImageView);

            animatorSet.playTogether(alphaOutAnimation, alphaInAnimation);
            animatorSet.start();

        }

    }

}
