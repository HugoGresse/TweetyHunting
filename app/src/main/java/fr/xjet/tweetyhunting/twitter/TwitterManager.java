package fr.xjet.tweetyhunting.twitter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import fr.xjet.tweetyhunting.Cat;
import fr.xjet.tweetyhunting.Constant;
import fr.xjet.tweetyhunting.R;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Hugo on 18/01/2015.
 */
public class TwitterManager {

    private static final String TAG = "TwitterManager";

    private Context mContext;
    private static SharedPreferences mSharedPreferences;

    private TwitterManagerListener mListener;

    private static Twitter twitter;
    private static RequestToken mRequestToken;

    private static AsyncTask mAsyncGetRequestToken;
    private static AsyncTask mAsyncGetAccessToken;

    public TwitterManager(Context context, TwitterManagerListener listener) {
        mContext = context;
        mListener = listener;

        mSharedPreferences = mContext.getSharedPreferences(Constant.PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    /**
     * check if the account is authorized
     *
     * @return
     */
    public boolean isConnected() {
        return mSharedPreferences.getString(Constant.PREF_KEY_TOKEN, null) != null;
    }

    /**
     * Try to connect to the Twitter API
     */
    public void connect() {
        if (isConnected()) {
            String oauthAccessToken = mSharedPreferences.getString(Constant.PREF_KEY_TOKEN, "");
            String oAuthAccessTokenSecret = mSharedPreferences.getString(Constant.PREF_KEY_SECRET, "");
            ConfigurationBuilder confbuilder = new ConfigurationBuilder();
            Configuration conf = confbuilder
                    .setOAuthConsumerKey(Constant.CONSUMER_KEY)
                    .setOAuthConsumerSecret(Constant.CONSUMER_SECRET)
                    .setOAuthAccessToken(oauthAccessToken)
                    .setOAuthAccessTokenSecret(oAuthAccessTokenSecret)
                    .build();
            mListener.onConnect();
        } else {
            mListener.onDisconnect();
        }
    }

    @SuppressWarnings("unchecked")
    public void shareTweet(Pair<String, Cat> pair, final TwitterUpdateTask.OnUpdateTwitter listener) {

        if (this.isConnected()) {

            TwitterUpdateTask updateTask = new TwitterUpdateTask(mContext, mSharedPreferences, new TwitterUpdateTask.OnUpdateTwitter() {
                @Override
                public void onSuccess() {
                    listener.onSuccess();
                }

                @Override
                public void onFail(int errorCode) {
                    // invalid credentials
                    if(errorCode == 89){
                        Toast.makeText(mContext, mContext.getString(R.string.invalid_credentials), Toast.LENGTH_SHORT).show();
                        disconnectTwitter();
                    } else if(errorCode == -1) {
                        // cannot resolve host
                        Toast.makeText(
                                mContext,
                                mContext.getString(R.string.msg_error_network) ,
                                Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        // others
                        Toast.makeText(
                                mContext,
                                mContext.getString(R.string.unknown_error) + " code:" + Integer.toString(errorCode) ,
                                Toast.LENGTH_SHORT)
                                .show();
                    }

                    listener.onFail(errorCode);
                }
            });

            updateTask.execute(pair);
        }

    }


    public void handleOAuthCallback(Intent intent) {
        /**
         * Handle OAuth Callback
         */
        Uri uri = intent.getData();
        if (uri != null && uri.toString().startsWith(Constant.CALLBACK_URL)) {
            String verifier = uri.getQueryParameter(Constant.IEXTRA_OAUTH_VERIFIER);

            if (mAsyncGetAccessToken != null && mAsyncGetAccessToken.getStatus() != AsyncTask.Status.FINISHED) {
                mAsyncGetAccessToken.cancel(true);
            }

            mAsyncGetAccessToken = new AsyncGetOAuthAccessToken(new OnOAuthAccessTokenCallback() {
                @Override
                public void onSuccess(AccessToken accessToken) {
                    Log.d(TAG, "OnOAuthSuccessCallback");

                    SharedPreferences.Editor e = mSharedPreferences.edit();
                    e.putString(Constant.PREF_KEY_TOKEN, accessToken.getToken());
                    e.putString(Constant.PREF_KEY_SECRET, accessToken.getTokenSecret());
                    e.apply();

                    mListener.onConnect();
                }

                @Override
                public void onFail(int statusCode) {

                    if(statusCode == -1){
                        Toast.makeText(mContext, mContext.getString(R.string.msg_error_network), Toast.LENGTH_LONG).show();
                    } else {
                        Log.d(TAG, "OnOAuthAccessTokenFailCallback");
                        Toast.makeText(mContext, mContext.getString(R.string.twitter_requestacess_fail_accesstoken)
                                + " " + Integer.toString(statusCode), Toast.LENGTH_LONG).show();
                    }

                    mListener.onFail();
                }
            }, verifier, mRequestToken).execute();
        }
    }

    public void askOAuth() {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerKey(Constant.CONSUMER_KEY);
        configurationBuilder.setOAuthConsumerSecret(Constant.CONSUMER_SECRET);
        Configuration configuration = configurationBuilder.build();
        twitter = new TwitterFactory(configuration).getInstance();


        // Check if not already connected
        String oauthAccessToken = mSharedPreferences.getString(Constant.PREF_KEY_TOKEN, "");
        String oAuthAccessTokenSecret = mSharedPreferences.getString(Constant.PREF_KEY_SECRET, "");
        if (!oauthAccessToken.isEmpty() && !oAuthAccessTokenSecret.isEmpty()) {
            Toast.makeText(mContext, "Already connected", Toast.LENGTH_SHORT).show();
            return;
        }


        if (mAsyncGetRequestToken != null && mAsyncGetRequestToken.getStatus() != AsyncTask.Status.FINISHED) {
            mAsyncGetRequestToken.cancel(true);
        }

        new AsyncGetOAuthRequestToken(new OnOAuthRequestTokenCallback() {
            @Override
            public void onSuccess(RequestToken requestToken) {
                Log.d(TAG, "OnOAuthSuccessCallback");

                mRequestToken = requestToken;

                Resources res = mContext.getResources();
                String msgToast = String.format(res.getString(R.string.twitter_requestacess),
                                                res.getString(R.string.app_name));

                Toast.makeText(mContext, msgToast, Toast.LENGTH_LONG).show();

                Intent oAuthIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL()));
                oAuthIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(oAuthIntent);
            }

            @Override
            public void onFail() {
                Log.d(TAG, "OnOAuthFailCallback");
                Toast.makeText(mContext, mContext.getString(R.string.twitter_requestacess_fail), Toast.LENGTH_LONG).show();
                mListener.onFail();
            }
        }).execute();

    }

    /**
     * Remove Token, Secret from preferences
     */
    public void disconnectTwitter() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.remove(Constant.PREF_KEY_TOKEN);
        editor.remove(Constant.PREF_KEY_SECRET);
        editor.apply();

        mListener.onDisconnect();
    }

    public interface TwitterManagerListener {
        public void onConnect();

        public void onDisconnect();

        public void onFail();
    }

    public interface OnOAuthRequestTokenCallback {
        public void onSuccess(RequestToken requestToken);

        public void onFail();
    }

    public interface OnOAuthAccessTokenCallback {
        public void onSuccess(AccessToken accessToken);

        public void onFail(int statusCode);
    }

    public class AsyncGetOAuthRequestToken extends AsyncTask<Void, Void, RequestToken> {

        private OnOAuthRequestTokenCallback mListener;

        public AsyncGetOAuthRequestToken(OnOAuthRequestTokenCallback listener) {
            mListener = listener;
        }

        @Override
        protected RequestToken doInBackground(Void... voidParam) {
            try {
                return twitter.getOAuthRequestToken(Constant.CALLBACK_URL);
            } catch (TwitterException e) {
                e.printStackTrace();
                return null;
            }
        }


        protected void onPostExecute(RequestToken requestToken) {
            if (requestToken != null) {
                mListener.onSuccess(requestToken);
            } else {
                mListener.onFail();
            }
        }
    }

    public class AsyncGetOAuthAccessToken extends AsyncTask<Void, Void, AccessToken> {

        private OnOAuthAccessTokenCallback mListener;
        private String mVerifier;
        private RequestToken mRequestToken;

        public AsyncGetOAuthAccessToken(OnOAuthAccessTokenCallback listener, String verifier, RequestToken requestToken) {
            mListener = listener;
            mVerifier = verifier;
            mRequestToken = requestToken;
        }

        @Override
        protected AccessToken doInBackground(Void... voidParam) {

            if(mVerifier == null || mVerifier.isEmpty() || mRequestToken == null){
                return null;
            }

            try {
                return twitter.getOAuthAccessToken(mRequestToken, mVerifier);
            } catch (TwitterException e) {
                if(e.isCausedByNetworkIssue()){
                    mListener.onFail(-1);
                } else {
                    mListener.onFail(e.getStatusCode());
                }
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(AccessToken accessToken) {
            if (accessToken != null) {
                mListener.onSuccess(accessToken);
            } else {
                mListener.onFail(0);
            }
        }
    }


}
