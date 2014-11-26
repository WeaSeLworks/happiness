package com.github.weaselworks.happiness.twitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.util.HashMap;

import static com.github.weaselworks.happiness.twitter.HappinessConstants.*;


/**
 * Created by nick on 06/11/2014.
 */
public class Globe extends Verticle {

    private final Logger logger = LoggerFactory.getLogger(Globe.class);

    @Override
    public void start() {

        final EventBus eb = vertx.eventBus();
        logger.info("The Globe verticle has started");

        Handler<Message<JsonObject> > choroplethHandler = new Handler<Message<JsonObject>>() {

            public void handle(Message<JsonObject> message) {

                JsonObject jo = message.body();

                int sentimentScore = jo.getInteger(SENTIMENT_SCORE_PROPERTY);

                // Round the sentimentScore and filter the ones at zero
                if (sentimentScore != 0) {

                    JsonObject outerCoordinates = jo.getField(COORDINATES_PROPERTY);

                    if (null != outerCoordinates) {

                        JsonArray innerCoords = outerCoordinates.getField(COORDINATES_PROPERTY);

                        Number tweetLong = null;
                        Number tweetLat = null;
                        try {
                            tweetLong = innerCoords.get(0);
                            tweetLat = innerCoords.get(1);
                            logger.info("Lat: " + tweetLat + ", Long: " + tweetLong + ", Sentiment: " + sentimentScore);
                        }
                        catch (ClassCastException ex) {
                            Object tweetLongObj = innerCoords.get(0);
                            logger.info("Type: "+ tweetLongObj.getClass());
                            throw ex;
                        }

                        long displayVal = calculateDisplayValue(sentimentScore);

                        logger.info("Display val: " + displayVal);

                        HashMap<String, Object> vals = new HashMap<String, Object>();
                        vals.put(CHOROPLETH_SCORE, displayVal);
                        vals.put(TWEET_LAT, tweetLat);
                        vals.put(TWEET_LONG, tweetLong);
                        vals.put(TWEET_TEXT, jo.getField(TEXT_PROPERTY));

                        JsonObject json = new JsonObject(vals);

                        eb.publish(SERVER_ADDRESS, json);


                    }
                }

            }
        };

        eb.registerHandler(GLOBE_ADDRESS, choroplethHandler);

    }

    private long calculateDisplayValue(Number average) {

        logger.info("Calculating Choropleth Value for: " + average);

        long retVal = 0;
        if (average.doubleValue() < DISPLAY_MIN) {
            retVal = DISPLAY_MIN;
        }
        else if (average.doubleValue() > DISPLAY_MAX) {
            retVal = DISPLAY_MAX;
        }
        else {
            retVal = (long) Math.rint(average.doubleValue());
            logger.info("After round: " + retVal);
        }

        //logger.info("Retval: " + retVal);

        return retVal;

    }

}
