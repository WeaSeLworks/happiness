package com.github.weaselworks.happiness.twitter;

import org.vertx.java.platform.Verticle;

/**
 * Created by paul on 26/10/2014.
 */
public class Happiness extends Verticle {

    @Override
    public void start() {
        getContainer().deployWorkerVerticle("com.github.weaselworks.happiness.twitter.TwitterVerticle");
        getContainer().deployVerticle("com.github.weaselworks.happiness.twitter.LanguageDetect");
        getContainer().deployVerticle("com.github.weaselworks.happiness.twitter.MachineTranslation");
        getContainer().deployVerticle("com.github.weaselworks.happiness.twitter.SentimentAnalyser");
        getContainer().deployVerticle("com.github.weaselworks.happiness.twitter.Server");

    }
}
