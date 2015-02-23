package fr.xjet.tweetyhunting;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Hugo Gresse on 23/02/15.
 */
public class AboutActivity extends Activity {

    public static final String LOG_TAG = "AboutActivity";

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate");

        ((TweetyHuntingApplication)getApplication()).sendScreenTracking(LOG_TAG);

        setContentView(R.layout.activity_about);

        mListView = (ListView)findViewById(R.id.about_listview);

        final AboutArrayAdapter adapter = new AboutArrayAdapter(this,
                getResources().getStringArray(R.array.about_libraries));

        View headerListView = getLayoutInflater().inflate(R.layout.activity_about_top, null);
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

            textView.setText(values[position]);

            return rowView;
        }
    }


}
