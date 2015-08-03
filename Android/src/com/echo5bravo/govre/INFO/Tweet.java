package com.echo5bravo.govre.INFO;

public class Tweet {
	public String username;
	public String message;
	public String image_url;
	public String dt_tweeted;
	
	public Tweet(String username, String message, String url, String dt_tweeted) {
		this.username = username;
		this.message = message;
		this.image_url = url;
		this.dt_tweeted = dt_tweeted;
	}
}
