package fr.xjet.tweetyhunting;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by Hugo on 19/02/2015.
 */
public class TweetyHuntingApplication extends Application {

    // Google Analytics Property ID (tracking code)
    private static final String PROPERTY_ID = "UA-59957915-1";

    /**
     * Send screen name tracking
     * @param screenName
     */
    public void sendScreenTracking(String screenName){
        // Get tracker.
        Tracker t = GoogleAnalytics.getInstance(this).newTracker(PROPERTY_ID);

        // Set screen name.
        // Where path is a String representing the screen name.
        t.setScreenName(screenName);

        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    public void sendEventTracking(int categoryId, int actionId, String label) {
        // Get tracker.
        Tracker t = GoogleAnalytics.getInstance(this).newTracker(PROPERTY_ID);

        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder()
                .setCategory(getString(categoryId))
                .setAction(getString(actionId))
                .setLabel(label)
                .build());
    }
}
