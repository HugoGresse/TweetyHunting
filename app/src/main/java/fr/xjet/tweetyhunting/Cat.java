package fr.xjet.tweetyhunting;

/**
 * A simple cat object representing a Cat with and url and an image
 *
 * Created by Hugo on 10/12/2014.
 */
public class Cat {

    private String url;
    private byte[] imageData;

    public Cat(String url) {
        this(url, null);
    }

    public Cat(String url, byte[] imageData) {
        this.url = url;
        this.imageData = imageData;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public boolean isEmpty(){
        if(url != null && !url.isEmpty() && imageData != null && imageData.length != 0){
            return false;
        }
        return true;
    }
}
