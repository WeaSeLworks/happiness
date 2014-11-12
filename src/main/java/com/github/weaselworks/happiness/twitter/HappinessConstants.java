package com.github.weaselworks.happiness.twitter;

/**
 * Created by nick on 03/11/2014.
 */
public class HappinessConstants {

    // Verticle Addresses
    public static final String LANGUAGE_DETECT_ADDRESS = "com.github.weaselworks.happiness.languagedetect";
    public static final String SENTIMENT_ANALYSER_ADDRESS = "com.github.weaselworks.happiness.sentimentanalyser";
    public static final String MACHINE_TRANSLATION_ADDRESS = "com.github.weaselworks.happiness.machinetranslation";
    public static final String SERVER_ADDRESS = "com.github.weaselworks.happiness.twitter.server";
    public static final String CHOROPLETH_ADDRESS = "com.github.weaselworks.happiness.choropleth";

    // Socket Channels
    public static final String SERVER_CLIENT_OUTBOUND_ADDRESS = "msg.server";
    public static final String SERVER_CLIENT_INBOUND_ADDRESS = "msg.client";

    // Json Message Properties
    public static final String SENTIMENT_SCORE_PROPERTY = "sentimentScore";
    public static final String COORDINATES_PROPERTY = "coordinates";
    public static final String COUNTRY_CODE = "countryCode";
    public static final String CHOROPLETH_SCORE = "choroplethScore";
    public static final String TWEET_LAT = "tweetLat";
    public static final String TWEET_LONG = "tweetLong";

    // Choropleth Values
    public static final int CHOROPLETH_MIN = -5;
    public static final int CHOROPLETH_MAX = 5;


}
