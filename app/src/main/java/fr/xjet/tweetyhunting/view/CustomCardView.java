package fr.xjet.tweetyhunting.view;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * Created by Hugo on 17/02/2015.
 */
public class CustomCardView extends CardView {

    public CustomCardView(Context context) {
        super(context);
    }

    public CustomCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public float getXFraction(){
        final WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        return (width == 0) ? 0 : getX() / (float) width;
    }

    public void setXFraction(float xFraction) {

        final WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        float move = xFraction * width;

        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) getLayoutParams();

        if(move >= 0 && move < lp.leftMargin){
            move = lp.leftMargin;
        }

        setX((width > 0) ? (move) : 0);
    }

}
