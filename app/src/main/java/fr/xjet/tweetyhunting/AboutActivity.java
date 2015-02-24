package fr.xjet.tweetyhunting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Hugo Gresse on 23/02/15.
 */
public class AboutActivity extends Activity {

    public static final String LOG_TAG = "AboutActivity";

    private ListView mListView;
    private TextView mMeTextView;


    private Typeface mRobotoRegular;
    private Typeface mRobotoBold;
    private Typeface mRobotoCondensedBold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate");

        setContentView(R.layout.activity_about);

        ((TweetyHuntingApplication)getApplication()).sendScreenTracking(LOG_TAG);

        mRobotoRegular = Typeface.createFromAsset(getAssets(), "fonts/roboto_regular.ttf");
        mRobotoBold = Typeface.createFromAsset(getAssets(), "fonts/roboto_bold.ttf");
        mRobotoCondensedBold = Typeface.createFromAsset(getAssets(), "fonts/roboto_condensed_bold.ttf");

        mListView = (ListView)findViewById(R.id.about_listview);

        final AboutArrayAdapter adapter = new AboutArrayAdapter(this,
                getResources().getStringArray(R.array.about_libraries));

        View headerListView = getLayoutInflater().inflate(R.layout.activity_about_top, null);

        ((TextView)headerListView.findViewById(R.id.using_title)).setTypeface(mRobotoCondensedBold);
        mMeTextView = ((TextView)headerListView.findViewById(R.id.me_textview));
        mMeTextView.setTypeface(mRobotoBold);
        formatCardUrlIntent(mMeTextView, mMeTextView.getText().toString());

        mListView.addHeaderView(headerListView);
        mListView.setAdapter(adapter);

    }


    public class AboutArrayAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final String[] values;

        public AboutArrayAdapter(Context context, String[] values) {
            super(context, R.layout.list_about, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_about, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.lib_textview);

            textView.setTypeface(mRobotoBold);

            formatCardUrlIntent(textView, values[position]);

            return rowView;
        }
    }


    private void formatCardUrlIntent(TextView textView, String text){

        String textContent = "";

        int lastUrl = text.lastIndexOf("http");
        if (lastUrl == -1) {
            // no dots
            textContent = text;
        } else try {
            URL url = new URL(text.substring(lastUrl));

            // url found :
            textContent += text.substring(0, lastUrl);

            final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()));
            ((ViewGroup)textView.getParent().getParent()).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(browserIntent);
                }
            });

        } catch (MalformedURLException e) {
            textContent = text;
        }

        textView.setText(textContent);
    }
}
