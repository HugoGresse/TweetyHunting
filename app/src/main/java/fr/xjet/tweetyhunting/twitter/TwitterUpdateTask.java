package fr.xjet.tweetyhunting.twitter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import fr.xjet.tweetyhunting.Cat;
import fr.xjet.tweetyhunting.Constant;
import fr.xjet.tweetyhunting.ImageUtils;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Send a tweet with a media
 * Created by Hugo on 11/02/2015.
 */
public class TwitterUpdateTask extends AsyncTask<Pair<String, Cat>, String, String> {

    Context mContext;
    SharedPreferences mSharedPreferences;
    OnUpdateTwitter mListener;

    TwitterException exception;

    public TwitterUpdateTask(Context context, SharedPreferences sharedPreferences, OnUpdateTwitter listener) {
        mContext = context;
        mSharedPreferences = sharedPreferences;
        mListener = listener;

    }

    /**
     * Before starting background thread Show Progress Dialog
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //TODO : show dialog
    }

    /**
     * getting Places JSON
     */
    protected String doInBackground( Pair<String, Cat>... args) {

        Pair<String, Cat> pair = args[0];

        try {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(Constant.CONSUMER_KEY);
            builder.setOAuthConsumerSecret(Constant.CONSUMER_SECRET);

            // Access Token
            String access_token = mSharedPreferences.getString(Constant.PREF_KEY_TOKEN, "");
            // Access Token Secret
            String access_token_secret = mSharedPreferences.getString(Constant.PREF_KEY_SECRET, "");

            AccessToken accessToken = new AccessToken(access_token, access_token_secret);
            Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);

            // Update status

            StatusUpdate status = new StatusUpdate(pair.first);
            status.setMedia(ImageUtils.saveImage(mContext, pair.second));
            twitter4j.Status response = twitter.updateStatus(status);

            Log.d("Status", "> " + response.getText());
        } catch (TwitterException e) {
            // Error in updating status
            Log.d("Twitter Update Error", e.getMessage());

            exception = e;
        }
        return null;
    }

    /**
     * After completing background task Dismiss the progress dialog and show
     * the data in UI Always use runOnUiThread(new Runnable()) to update UI
     * from background thread, otherwise you will get error
     * *
     */
    protected void onPostExecute(String file_url) {
        // TODO : dismiss loader

        if(exception != null){
            mListener.onFail(exception.getErrorCode());
        } else {
            mListener.onSuccess();
        }

    }

    public interface OnUpdateTwitter {
        public void onSuccess();
        public void onFail(int errorCode);
    }
}
