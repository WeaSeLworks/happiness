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
    public static final String GLOBE_ADDRESS = "com.github.weaselworks.happiness.globe";

    // Socket Channels
    public static final String SERVER_CLIENT_OUTBOUND_ADDRESS = "msg.server";
    public static final String SERVER_CLIENT_INBOUND_ADDRESS = "msg.client";

    // Json Message Properties
    public static final String SENTIMENT_SCORE_PROPERTY = "sentimentScore";
    public static final String COORDINATES_PROPERTY = "coordinates";
    public static final String TEXT_PROPERTY = "text";
    public static final String COUNTRY_CODE = "countryCode";
    public static final String CHOROPLETH_SCORE = "choroplethScore";
    public static final String TWEET_LAT = "tweetLat";
    public static final String TWEET_LONG = "tweetLong";
    public static final String TWEET_TEXT = "tweetText";

    // Display Values
    public static final int DISPLAY_MIN = -5;
    public static final int DISPLAY_MAX = 5;


}
