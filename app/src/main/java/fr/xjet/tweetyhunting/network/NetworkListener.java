package fr.xjet.tweetyhunting.network;

import fr.xjet.tweetyhunting.Cat;

/**
 * Created by Hugo on 05/01/2015.
 */
public interface NetworkListener {

    public enum NetworkError {
        APIFAIL,
        NETWORKFAIL
    }

    public void catReceived(Cat cat);
    public void catFetchFailed(NetworkError e);

}
