package fr.xjet.tweetyhunting.network.dataObjects;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Represent Data object of Giphy
 *
 * Created by Hugo Gresse on 14/03/15.
 */
public class Data {

    @SerializedName("type")
    private String mType;

    @SerializedName("id")
    private int mId;

    @SerializedName("url")
    private String mUrl;

    @SerializedName("rating")
    private String mRating;

    @SerializedName("images")
    private Map<String, ImageData> mImages;

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getRating() {
        return mRating;
    }

    public void setRating(String rating) {
        mRating = rating;
    }

    public Map<String, ImageData> getImages() {
        return mImages;
    }

    public void setImages(Map<String, ImageData> images) {
        mImages = images;
    }
}
