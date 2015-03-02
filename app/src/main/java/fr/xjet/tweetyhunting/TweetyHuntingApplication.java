package fr.xjet.tweetyhunting;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Hugo on 19/02/2015.
 */
public class TweetyHuntingApplication extends Application {

    // Google Analytics Property ID (tracking code)
    private static String mPropertyId = "";

    @Override
    public void onCreate(){

        Fabric.with(this, new Crashlytics());

        mPropertyId = getString(R.string.ga_property_id);
    }

    /**
     * Send screen name tracking
     * @param screenName
     */
    public void sendScreenTracking(String screenName){
        // Get tracker.
        Tracker t = GoogleAnalytics.getInstance(this).newTracker(mPropertyId);

        // Set screen name.
        // Where path is a String representing the screen name.
        t.setScreenName(screenName);

        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    public void sendEventTracking(int categoryId, int actionId, String label) {
        // Get tracker.
        Tracker t = GoogleAnalytics.getInstance(this).newTracker(mPropertyId);

        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder()
                .setCategory(getString(categoryId))
                .setAction(getString(actionId))
                .setLabel(label)
                .build());
    }
}
