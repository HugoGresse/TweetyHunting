package fr.xjet.tweetyhunting.network.dataObjects;

import com.google.gson.annotations.SerializedName;

/**
 * Represent Pagination object of Giphy
 *
 * Created by Hugo Gresse on 14/03/15.
 */
public class Pagination {

    @SerializedName("total_count")
    private long mTotalCount;

    @SerializedName("count")
    private int  mCount;

    @SerializedName("offset")
    private int  mOffset;

    public long getTotalCount() {
        return mTotalCount;
    }

    public void setTotalCount(long totalCount) {
        mTotalCount = totalCount;
    }

    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        mCount = count;
    }

    public int getOffset() {
        return mOffset;
    }

    public void setOffset(int offset) {
        mOffset = offset;
    }
}
