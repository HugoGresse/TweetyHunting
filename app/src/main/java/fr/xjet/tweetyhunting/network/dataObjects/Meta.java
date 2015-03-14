package fr.xjet.tweetyhunting.network.dataObjects;

import com.google.gson.annotations.SerializedName;

/**
 * Represent Meta object of Giphy
 *
 * Created by Hugo Gresse on 14/03/15.
 */
public class Meta {

    @SerializedName("status")
    private int mStatus;

    @SerializedName("msg")
    private String mMessage;

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }
}
