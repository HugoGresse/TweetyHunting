package fr.xjet.tweetyhunting;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFloat;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.util.List;

import fr.xjet.tweetyhunting.network.CatApiManager;
import fr.xjet.tweetyhunting.network.NetworkListener;
import fr.xjet.tweetyhunting.twitter.TwitterManager;
import fr.xjet.tweetyhunting.twitter.TwitterUpdateTask;
import fr.xjet.tweetyhunting.view.CustomProgressBarCircularIndeterminate;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


public class TweetingActivity extends ActionBarActivity implements NetworkListener, TwitterManager.TwitterManagerListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String LOG_TAG = "TweetingActivity";

    private static final int ANIMATION_DURATION = 5;

    private OkHttpClient                            mClient;

    private TwitterManager                          mTwitterManager;
    protected float                                 mDensity;

    private SwipeRefreshLayout                      mSwipeRefreshLayout;
    private CustomProgressBarCircularIndeterminate  mLoader;
    private GifImageView                            mImageView;
    private CardView                                mCardView;
    private StateButtonManager                      mStateButtonManager;
    private ButtonFloat                             mShareButton;
    private ViewGroup                               mStateButtonLayout;
    private CustomProgressBarCircularIndeterminate  mShareButtonLoader;
    private LinearLayout                            mEditTextLayout;
    private MaterialEditText                        mTweetEditText;
    private MenuItem                                mTwitterMenuItem;
    private boolean                                 mRequestTweetCat;

    private Cat                                     mCurrentCat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((TweetyHuntingApplication)getApplication()).sendScreenTracking(LOG_TAG);

        setContentView(R.layout.activity_tweeting);

        Log.d(LOG_TAG, "onCreate");

        mDensity  = getResources().getDisplayMetrics().density;
        mClient = new OkHttpClient();

        mImageView = (GifImageView) findViewById(R.id.catImageView);
        mLoader = (CustomProgressBarCircularIndeterminate) findViewById(R.id.progressBar);
        mShareButton = (ButtonFloat) findViewById(R.id.shareButton);
        mStateButtonLayout = (ViewGroup) findViewById(R.id.stateButton);
        mShareButtonLoader = (CustomProgressBarCircularIndeterminate) findViewById(R.id.stateButtonLoader);
        mCardView = (CardView) findViewById(R.id.tweet_card);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mTweetEditText = (MaterialEditText) findViewById(R.id.tweetEditText);
        mEditTextLayout = (LinearLayout) findViewById(R.id.editTextLayout);

        mTweetEditText.setMaxCharacters(140 - getString(R.string.tweet_sufix).length() -2);

        new LoaderManager(mLoader, mImageView);
        mStateButtonManager = new StateButtonManager(this, mShareButtonLoader,mShareButton);

        mSwipeRefreshLayout.setOnRefreshListener(this);

        getACat();

        mTwitterManager = new TwitterManager(this.getApplicationContext(), this);


        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // close keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mTweetEditText.getWindowToken(), 0);

                requestTweetCat();
            }
        });
    }

    @Override
    protected void onNewIntent (Intent intent){

        Log.d(LOG_TAG, "onNewIntent");

        // Check if coming from Browser
        if(intent != null &&
                intent.getData() != null &&
                intent.getData().toString().startsWith(Constant.CALLBACK_URL) ){

            // maybe handle intent if callbacked from Twitter
            mTwitterManager.handleOAuthCallback(intent);

            if(mRequestTweetCat){
                Toast.makeText(this, getString(R.string.twitter_sharingimage), Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * see {@link #onConnect()}
         */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tweeting, menu);

        mTwitterMenuItem = menu.findItem(R.id.action_logout_tw);

        if(!mTwitterManager.isConnected()){
            mTwitterMenuItem.setTitle(getString(R.string.action_tw_login));
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_logout_tw:
                if(mTwitterManager.isConnected()){
                    mTwitterManager.disconnectTwitter();
                } else {
                    mTwitterManager.askOAuth();
                    mStateButtonManager.hideLoader();
                }
                return true;
            case R.id.action_about:
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                this.startActivity(aboutIntent);
                return false;
        }


        return super.onOptionsItemSelected(item);
    }

    private void updateImageViewHeight(){

        int height = mSwipeRefreshLayout.getHeight() - mEditTextLayout.getHeight();

        height -= ((ViewGroup.MarginLayoutParams)mCardView.getLayoutParams()).topMargin +
                ((ViewGroup.MarginLayoutParams)mCardView.getLayoutParams()).bottomMargin;

        height -= mStateButtonLayout.getHeight() + ((ViewGroup.MarginLayoutParams)mStateButtonLayout.getLayoutParams()).topMargin;

        height -= 20;

        mImageView.getLayoutParams().height = height;
    }

    private void getACat(){

        // send tracking event
        ((TweetyHuntingApplication)getApplication()).sendEventTracking(
                R.string.tracker_tweetingactivity,
                R.string.tracker_tweetingactivity_getcat,
                "");

        mClient.setFollowRedirects(false);

        new CatApiManager(this).getACat();
    }

    /**
     * Tweet current cat, if possible
     */
    private void requestTweetCat(){
        if(mCurrentCat != null && mCurrentCat.isEmpty() || LoaderManager.isLoading()){
            Toast.makeText(
                    TweetingActivity.this, getString(R.string.msg_nocat),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mRequestTweetCat = true;

        if(mTwitterManager.isConnected()){
            mStateButtonManager.showLoader();
            mRequestTweetCat = false;

            // send tracking event
            ((TweetyHuntingApplication)getApplication()).sendEventTracking(
                    R.string.tracker_tweetingactivity,
                    R.string.tracker_tweetingactivity_tweet,
                    "");

            String tweetText =
                    mTweetEditText.getText().toString() +
                    " " +
                    getString(R.string.tweet_sufix);

            mTwitterManager.shareTweet(new Pair<>(tweetText, mCurrentCat), new TwitterUpdateTask.OnUpdateTwitter() {
                @Override
                public void onSuccess() {
                    mTweetEditText.setText("");
                    mStateButtonManager.hideLoader();

                    Toast.makeText(
                            TweetingActivity.this,
                            getString(R.string.twitter_imageshared),
                            Toast.LENGTH_SHORT).show();

                    slideOutView(mCardView);
                }

                @Override
                public void onFail(int errorCode) {
                    Log.d(LOG_TAG, "onFail");
                    mStateButtonManager.hideLoader();
                    // Managed in TwitterManager
                }
            });
        } else {
            mTwitterManager.askOAuth();
            mStateButtonManager.hideLoader();
        }
    }

    // NetworkListener Implementation

    @Override
    public void catReceived(Cat cat) {
        Log.d(LOG_TAG, "final cat : " + cat.getUrl());
        mSwipeRefreshLayout.setRefreshing(false);

        if(cat.getImageData() != null){
            mCurrentCat = cat;

            LoaderManager.hideLoader();

            GifDrawable gifFromStream = null;
            try {
                gifFromStream = new GifDrawable(cat.getImageData());
                mImageView.setImageDrawable(gifFromStream);


                mImageView.getLayoutParams().width = ((ViewGroup)mImageView.getParent()).getWidth();

                updateImageViewHeight();

            } catch (IOException e) {
                e.printStackTrace();

                Toast.makeText(this, getString(R.string.msg_error), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.msg_error), Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void catFetchFailed(NetworkError e) {

        mSwipeRefreshLayout.setRefreshing(false);
        LoaderManager.hideLoader();

        switch (e){
            case APIFAIL:
                Toast.makeText(this, getString(R.string.msg_error_api), Toast.LENGTH_SHORT).show();
                break;
            case NETWORKFAIL:
                Toast.makeText(this, getString(R.string.msg_error_network), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void slideOutView(View view){

        AnimatorSet animatorSet = new AnimatorSet();

        AnimatorSet slideOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.slide_out);
        slideOut.setTarget(mCardView);
        AnimatorSet popIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.pop_in);
        popIn.setTarget(mCardView);

        ObjectAnimator alphaOutAnimation = ObjectAnimator.ofFloat(mImageView, View.ALPHA, 1, 0);
        alphaOutAnimation.setDuration(0);
        alphaOutAnimation.setTarget(mImageView);
        AnimatorSet popInLoader = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.pop_in);
        popInLoader.setTarget(mLoader);

        animatorSet.playSequentially(slideOut, popIn);
        animatorSet.play(popIn).with(alphaOutAnimation).with(popInLoader);
//        animatorSet.play(slideOut);
        animatorSet.start();

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                getACat();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });



    }


    // Implement SwipeRefreshLayout.OnRefreshListener

    @Override
    public void onRefresh() {
        getACat();
    }


    // Implement TwitterManagerListener;

    @Override
    public void onConnect() {
        Log.d(LOG_TAG, "onConnect");

        mTwitterMenuItem.setTitle(getString(R.string.action_tw_logout));

        if(!mCurrentCat.isEmpty() && mRequestTweetCat){
            requestTweetCat();
        } else {

            Toast.makeText(
                    TweetingActivity.this,
                    getString(R.string.twitter_logedin),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDisconnect() {
        Log.d(LOG_TAG, "onDisconnect");

        Toast.makeText(
                TweetingActivity.this,
                getString(R.string.twitter_logedout),
                Toast.LENGTH_SHORT).show();

        mTwitterMenuItem.setTitle(getString(R.string.action_tw_login));
    }

    @Override
    public void onFail() {
        Log.d(LOG_TAG, "onFail");

    }




    /***************
     * UNUSED
     ***************/

    private void sendShareTwit(Cat cat) {

        Intent tweetIntent = new Intent(Intent.ACTION_SEND);

        tweetIntent.setType("image/gif");
        tweetIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_name));
        tweetIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(ImageUtils.saveImage(this, cat)));

        PackageManager packManager = getPackageManager();
        List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(tweetIntent,  PackageManager.MATCH_DEFAULT_ONLY);

        boolean resolved = false;
        for(ResolveInfo resolveInfo: resolvedInfoList){
            if(resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")){
                tweetIntent.setClassName(
                        resolveInfo.activityInfo.packageName,
                        resolveInfo.activityInfo.name );
                resolved = true;
                break;
            }
        }


        if(resolved){
            startActivity(tweetIntent);
        }else{
            startActivity(Intent.createChooser(tweetIntent, getString(R.string.dialog_share)));
        }
    }

}
