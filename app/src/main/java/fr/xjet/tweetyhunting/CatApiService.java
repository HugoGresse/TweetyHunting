package fr.xjet.tweetyhunting;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by Hugo on 04/01/2015.
 */
public interface CatApiService {

    @GET("/images/get?type=gif&format=src")
    public void getACat(Callback<Response> callback);


    @GET("{src}")
    public void getCatImage(@Path("src") String url, Callback<byte[]> callback);

}
