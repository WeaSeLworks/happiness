package com.github.weaselworks.happiness.twitter;


import org.slf4j.*;
import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.Context;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.json.impl.Json;

import org.vertx.java.platform.Verticle;
import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;


/**
 * Created by paul on 25/10/2014.
 */

public class TwitterVerticle extends BusModBase {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TwitterVerticle.class);

    TwitterStream twitterStream = null;

    public void start() {
        super.start();

        final StatusListener statusListener = new StatusAdapter() {

            @Override
            public void onStatus(final twitter4j.Status status) {

                String statusJson = TwitterObjectFactory.getRawJSON(status);

                logger.debug(statusJson);

                JsonObject jo = new JsonObject(statusJson);

                eb.publish("com.github.weaselworks.happiness.languagedetect", jo);

            }
        };

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setJSONStoreEnabled(true);

        twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
        twitterStream.addListener(statusListener);


        FilterQuery filterQuery = new FilterQuery();
        double theWorld[][]= {{-180, -90}, {180, 90}};
        filterQuery.locations(theWorld);
        twitterStream.filter(filterQuery);


        logger.info("Spinning up...");
    }
}
