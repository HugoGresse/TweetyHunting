package fr.xjet.tweetyhunting.network;

import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.io.IOException;

import fr.xjet.tweetyhunting.Cat;
import fr.xjet.tweetyhunting.CatApiService;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

/**
 * Created by Hugo on 04/01/2015.
 */
public class CatApiManager implements CatDownloadListener {

    private static final String LOG = "CatApiManager";
    public static final int NUMBER_RETRY = 4;

    private CatApiService mCatApiService;

    private NetworkListener mListener;

    // Result
    private Cat mCat;

    private static int mRetryCount;

    public CatApiManager(NetworkListener listener) {

        mListener = listener;

        OkHttpClient client = new OkHttpClient();
        client.setFollowRedirects(false);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://thecatapi.com/api")
                .setClient(new OkClient(client))
                .build();

        mCatApiService = restAdapter.create(CatApiService.class);
    }

    public void getACat(){

        mCatApiService.getACat(new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                mListener.catFetchFailed(NetworkListener.NetworkError.APIFAIL);
            }

            @Override
            public void failure(RetrofitError error) {
                if(error.getResponse() != null && error.getResponse().getStatus() == 302){
                    // the image url is a String in Location header
                    mRetryCount = 0;
                    mCat = new Cat(StringUtils.getLocation(error.getResponse().getHeaders()));
                    Log.i(LOG, mCat.getUrl());

                    new DownloadImageTask().execute(mCat.getUrl());

                } else {
                    if(mRetryCount < NUMBER_RETRY){
                        mRetryCount ++;
                        Log.d(LOG, "Retry getACat #" + Integer.toString(mRetryCount));
                        getACat();
                        return;
                    }

                    // error
                    Log.e(LOG, "error while getting cat from api : " + error.toString());

                    if(error.getKind() == RetrofitError.Kind.NETWORK){
                        mListener.catFetchFailed(NetworkListener.NetworkError.NETWORKFAIL);
                    } else {
                        mListener.catFetchFailed(NetworkListener.NetworkError.APIFAIL);
                    }

                }

            }
        });

    }

    @Override
    public void onCatDownloadFinished(byte[] imageData) {
        Log.d(LOG, "download finished");
        mCat.setImageData(imageData);
        mListener.catReceived(mCat);
    }


    private class DownloadImageTask extends AsyncTask<String, Void, byte[]> {

        String imageUrl;
        OkHttpClient mClient = new OkHttpClient();

        @Override
        protected byte[] doInBackground(String... strings)  {
            imageUrl = strings[0];

            Request requestImageData = new Request.Builder()
                    .url(imageUrl)
                    .build();


            com.squareup.okhttp.Response responseImageData = null;
            try {
                responseImageData = mClient.newCall(requestImageData).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (responseImageData != null) {
                if (!responseImageData.isSuccessful()) try {
                    throw new IOException("Unexpected code " + responseImageData);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            try {
                return responseImageData.body().bytes();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (NullPointerException e){
                return null;
            }
        }
        @Override
        protected void onPostExecute(byte[] imageData) {
            super.onPostExecute(imageData);

            if(imageData != null){
                CatApiManager.this.onCatDownloadFinished(imageData);
            } else {
                mListener.catFetchFailed(NetworkListener.NetworkError.NETWORKFAIL);
            }
        }
    }

}
