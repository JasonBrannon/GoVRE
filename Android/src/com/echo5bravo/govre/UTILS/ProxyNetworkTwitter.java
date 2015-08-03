package com.echo5bravo.govre.UTILS;

import java.util.ArrayList;

import twitter4j.RateLimitStatus;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.Twitter;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;


import android.content.Context;
import com.echo5bravo.govre.INFO.Tweet;
import com.echo5bravo.govre.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import android.util.Log;



public class ProxyNetworkTwitter {
	
	private static ConfigurationBuilder builder;

    private static final String TAG = "Twitter Network Utility";

    public static ArrayList<Tweet> getTweets(String searchTerm, int count, Context context) {

        ArrayList<Tweet> tweets = new ArrayList<Tweet>();
        
        try {
        	
        	builder = new ConfigurationBuilder();
        	builder.setDebugEnabled(true);
            builder.setUseSSL(true);
            builder.setApplicationOnlyAuthEnabled(true);
        	builder.setOAuthConsumerKey("fGJS44QDvQhFgWB8j7e5KQ").setOAuthConsumerSecret("sd74AghvZHAgghiA9XPl0lbVHyG8YIuGFwtnpkMx8");
            OAuth2Token token = new TwitterFactory(builder.build()).getInstance().getOAuth2Token();
        	
        	// exercise & verify
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true);
            cb.setUseSSL(true);
            cb.setApplicationOnlyAuthEnabled(true);
            cb.setOAuthConsumerKey("fGJS44QDvQhFgWB8j7e5KQ");
            cb.setOAuthConsumerSecret("sd74AghvZHAgghiA9XPl0lbVHyG8YIuGFwtnpkMx8");
            cb.setOAuth2TokenType(token.getTokenType());
            cb.setOAuth2AccessToken(token.getAccessToken());

            Twitter twitter = new TwitterFactory(cb.build()).getInstance();
            
            Query query = new Query(searchTerm);
            query.setCount(count);
            QueryResult result = twitter.search(query);
            for (Status status : result.getTweets()) {
                //Log.d(TAG, "Screen Name " + status.getUser().getScreenName());

                Tweet tweet = new Tweet(
                        status.getUser().getScreenName().toString(),                //from_user
                        status.getText().toString(),                                //"text"
                        status.getUser().getBiggerProfileImageURL().toString(),     //"profile_image_url"
                        status.getCreatedAt().toString());                          //"created_at"
                tweets.add(tweet);
            }
            
        }
        catch (TwitterException t)
        {
            //Log.d(TAG, "Twitter Error " + t.toString());
        }
        catch (Exception e)
        {
            //Log.d(TAG, "Error " + e.toString());
        }

		return tweets;
	}
}
