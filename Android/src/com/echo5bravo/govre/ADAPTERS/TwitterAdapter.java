package com.echo5bravo.govre.ADAPTERS;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.echo5bravo.govre.INFO.Tweet;
import com.echo5bravo.govre.UTILS.ImageDownloader;
import com.echo5bravo.govre.R;

import android.content.Context;
import android.text.util.Linkify;
import android.text.util.Linkify.TransformFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TwitterAdapter extends ArrayAdapter<Tweet>{
	
	private static final String TAG = "TwitterAdapter";

	private ArrayList<Tweet> tweets;
	private final ImageDownloader imgDownloader = new ImageDownloader();

	public TwitterAdapter(Context context, int textViewResourceId, ArrayList<Tweet> tweets) {
		super(context, textViewResourceId, tweets);
		this.tweets = tweets;	
	}

	public View getView(int position, View convertView, ViewGroup parent) {		
		
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.twitter_listitem, null);
		}

		Tweet tweet = tweets.get(position);
		if (tweet != null) {
			TextView username = (TextView) v.findViewById(R.id.username);
			TextView message = (TextView) v.findViewById(R.id.message);
			ImageView image = (ImageView) v.findViewById(R.id.avatar);
			TextView dt_tweeted = (TextView) v.findViewById(R.id.dt_tweeted);

			if (username != null) {
				
				username.setText("@" + tweet.username);
				
				 // Match @mentions and capture just the username portion of the text.
			    Pattern pattern = Pattern.compile("@([A-Za-z0-9_-]+)");
			    String scheme = "http://twitter.com/";
			    Linkify.addLinks(username, pattern, scheme, null, mentionFilter);
			}

			if(message != null) {				
				message.setText(tweet.message);
				Linkify.addLinks(message, Linkify.ALL);
			}
			
			if(image != null) {				
						
				try{
				  this.imgDownloader.download(tweet.image_url, image);
				}
				catch (Exception e)
				{
					 //Log.e(TAG, "Error: " + e.toString());
				}				
			}
			
			if(dt_tweeted != null) {
				dt_tweeted.setText(twitterHumanFriendlyDate(tweet.dt_tweeted));
			}
		}
		return v;
	}	
	
	/* Used to format the DateTime returned from Twitter JSON */
    private static String twitterHumanFriendlyDate(String dateStr) {
        // parse Twitter date
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.ENGLISH);
        dateFormat.setLenient(false);
        //dateFormat.setTimeZone(TimeZone.getTimeZone("gmt"));
        Date created = null;
        try {
            created = dateFormat.parse(dateStr);
        } catch (Exception e) {
            return null;
        }

        // today
        Date today = new Date();

        // how much time since (ms)
        Long duration = today.getTime() - created.getTime();

        int second = 1000;
        int minute = second * 60;
        int hour = minute * 60;
        int day = hour * 24;

        if (duration < second * 7) {
            return "right now";
        }

        if (duration < minute) {
            int n = (int) Math.floor(duration / second);
            return n + " seconds ago";
        }

        if (duration < minute * 2) {
            return "about 1 minute ago";
        }

        if (duration < hour) {
            int n = (int) Math.floor(duration / minute);
            return n + " minutes ago";
        }

        if (duration < hour * 2) {
            return "about 1 hour ago";
        }

        if (duration < day) {
            int n = (int) Math.floor(duration / hour);
            return n + " hours ago";
        }
        if (duration > day && duration < day * 2) {
            return "yesterday";
        }

        if (duration < day * 365) {
            int n = (int) Math.floor(duration / day);
            return n + " days ago";
        } else {
            return "over a year ago";
        }
    }
	
	 // A transform filter that simply returns just the text captured by the
    // first regular expression group.
    TransformFilter mentionFilter = new TransformFilter() {
        public final String transformUrl(final Matcher match, String url) {
            return match.group(1);
        }
    };
}

