package com.github.weaselworks.happiness.twitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonElement;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import com.github.weaselworks.happiness.geocode.ReverseGeoCode;
import com.github.weaselworks.happiness.geocode.GeoName;

import org.vertx.java.platform.Verticle;

import static com.github.weaselworks.happiness.twitter.HappinessConstants.*;


/**
 * Created by nick on 06/11/2014.
 */
public class Choropleth extends Verticle {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Choropleth.class);

    private Iso3166CountryCodeConverter countryConverter = new Iso3166CountryCodeConverter();
    private ReverseGeoCode geocode;

    private ConcurrentMap<String, Country> countryByAlpha2Code;

    @Override
    public void start() {

        final EventBus eb = vertx.eventBus();
        countryByAlpha2Code = vertx.sharedData().getMap("choropleth.countryCodes");
        logger.info("The Choropleth verticle has started");

        try {

            geocode = new ReverseGeoCode(Choropleth.class.getResourceAsStream("/web/cities15000.txt"), true);
            logger.info("ReverseGeoCode instantiated");
        }
        catch (IOException ex) {
            logger.error("Exception creating ReverseGeoCode", ex);

        }

        Handler<Message<JsonObject> > choroplethHandler = new Handler<Message<JsonObject>>() {

            public void handle(Message<JsonObject> message) {

                org.vertx.java.core.json.JsonObject jo = message.body();

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

                        // Reverse geocode the location to get the Alpha2 country code
                        GeoName nearestPlace = geocode.nearestPlace(tweetLat.doubleValue(), tweetLong.doubleValue());
                        String alpha2Code = nearestPlace.country;

                        // If this is the first time we've seen a tweet for this country,
                        // create it and store it against its Alpha2 code so we don't have
                        // to convert it every time we want to add a score
                        Country country = null;
                        if (!countryByAlpha2Code.containsKey(alpha2Code)) {

                            // Lookup the alpha3 code for this country and store it
                            // for use in the presentation layer
                            String alpha3 = countryConverter.getIsoAlpha3Code(alpha2Code);

                            country = new Country(alpha3, sentimentScore);
                            countryByAlpha2Code.put(alpha2Code, country);

                        } else {
                            logger.info("Country: " + alpha2Code + " found");
                            country = countryByAlpha2Code.get(alpha2Code);
                            country.updateAverage(sentimentScore);
                        }

                        long choroplethVal = calculateChoroplethValue(country.getCurrentAverage());

                        logger.info("Choropleth val: " + choroplethVal);

                        HashMap<String, Object> vals = new HashMap<String, Object>();
                        vals.put(COUNTRY_CODE, country.getAlpha3Code());
                        vals.put(CHOROPLETH_SCORE, choroplethVal);
                        vals.put(TWEET_LAT, tweetLat);
                        vals.put(TWEET_LONG, tweetLong);

                        JsonObject json = new JsonObject(vals);

                        eb.publish(SERVER_ADDRESS, json);


                    }
                }

            }
        };

        eb.registerHandler(CHOROPLETH_ADDRESS, choroplethHandler);

    }

    private long calculateChoroplethValue(Number average) {

        logger.info("Calculating Choropleth Value for: " + average);

        long retVal = 0;
        if (average.doubleValue() < CHOROPLETH_MIN) {
            retVal = CHOROPLETH_MIN;
        }
        else if (average.doubleValue() > CHOROPLETH_MAX) {
            retVal = CHOROPLETH_MAX;
        }
        else {
            retVal = (long) Math.rint(average.doubleValue());
            logger.info("After round: " + retVal);
        }

        //logger.info("Retval: " + retVal);

        return retVal;

    }

}
