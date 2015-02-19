package fr.xjet.tweetyhunting.network;

import java.util.List;

import retrofit.client.Header;

/**
 * Created by Hugo on 05/01/2015.
 */
public class StringUtils {


    public static String getLocation(List<Header> headers){
        for (Header header : headers) {

            if(null == header ){
                continue;
            }
            if(null == header.getName()){
                continue;
            }
            if(header.getName().isEmpty()){
                continue;
            }

            if( null != header.getValue() && !header.getValue().isEmpty() ){
                // add xwsse header to user
                if(  header.getName().equals("Location") ||  header.getName().equals("Location")){
                    return header.getValue();
                }
            }
        }
        return "";
    }


}
