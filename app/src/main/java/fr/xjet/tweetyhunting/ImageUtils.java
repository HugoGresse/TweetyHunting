package fr.xjet.tweetyhunting;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Hugo on 05/02/2015.
 */
public class ImageUtils {

    public static File saveImage(Context context, Cat cat) {
        File filename;
        try {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();

            new File(path + "/" + context.getString(R.string.app_name)).mkdirs();
            filename = new File(path + "/"
                    + context.getString(R.string.app_name) + "/myImage.gif");

            FileOutputStream out = new FileOutputStream(filename);
            out.write(cat.getImageData());
            out.flush();
            out.close();

            return filename;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new File(cat.getUrl());
    }


}
