package com.github.weaselworks.happiness.twitter;

import org.vertx.java.platform.Verticle;

/**
 * Created by paul on 26/10/2014.
 */
public class Happiness extends Verticle {

    @Override
    public void start() {
        getContainer().deployWorkerVerticle("com.github.weaselworks.happiness.twitter.TwitterVerticle");
    }
}
