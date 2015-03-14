package fr.xjet.tweetyhunting.network.dataObjects;

import com.google.gson.annotations.SerializedName;

/**
 * Represent the image datas of a giphy response
 *
 * Created by Hugo Gresse on 14/03/15.
 */
public class ImageData {

    @SerializedName("url")
    private String mUrl;

    @SerializedName("width")
    private String mWidth;

    @SerializedName("height")
    private String mHeight;

    @SerializedName("size")
    private String mSize;

    @SerializedName("mp4")
    private String mMp4;

    public String getMp4() {
        return mMp4;
    }

    public void setMp4(String mp4) {
        mMp4 = mp4;
    }

    public String getSize() {
        return mSize;
    }

    public void setSize(String size) {
        mSize = size;
    }

    public String getHeight() {
        return mHeight;
    }

    public void setHeight(String height) {
        mHeight = height;
    }

    public String getWidth() {
        return mWidth;
    }

    public void setWidth(String width) {
        mWidth = width;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }
}
