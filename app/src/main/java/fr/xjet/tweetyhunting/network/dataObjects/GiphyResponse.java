package fr.xjet.tweetyhunting.network.dataObjects;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Represent the giphy complet response
 *
 * Created by Hugo Gresse on 14/03/15.
 */
public class GiphyResponse {

    @SerializedName("data")
    private List<Data> mDatas;

    @SerializedName("meta")
    private Meta mMeta;

    @SerializedName("pagination")
    private Pagination mPagination;

    public List<Data> getDatas() {
        return mDatas;
    }

    public void setDatas(List<Data> data) {
        mDatas = data;
    }

    public Meta getMeta() {
        return mMeta;
    }

    public void setMeta(Meta meta) {
        mMeta = meta;
    }

    public Pagination getPagination() {
        return mPagination;
    }

    public void setPagination(Pagination pagination) {
        mPagination = pagination;
    }
}
