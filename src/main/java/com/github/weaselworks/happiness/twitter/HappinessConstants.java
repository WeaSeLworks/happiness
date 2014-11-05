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

    // Socket Channels
    public static final String SERVER_CLIENT_OUTBOUND_ADDRESS = "msg.server";
    public static final String SERVER_CLIENT_INBOUND_ADDRESS = "msg.client";

    // Message Properties
    public static final String SENTIMENT_SCORE_PROPERTY = "sentimentScore";

}
