package com.github.weaselworks.happiness.twitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.json.impl.Json;
import org.vertx.java.platform.Verticle;
import twitter4j.Status;
import twitter4j.TwitterObjectFactory;

/**
 * Created by steve on 27/10/2014.
 */
public class LanguageDetect extends Verticle {

    Logger logger = LoggerFactory.getLogger(LanguageDetect.class);

    public void start(){

        final EventBus eb = vertx.eventBus();


        logger.info("The language detect verticle has started");

        Handler<Message<JsonObject> > languageDetectHandler = new Handler<Message<JsonObject> >() {
            public void handle(Message<JsonObject>  message) {

                logger.debug("message received "+message.body());

                JsonObject jo = message.body();


                //perform some action in here to call the bluemix language detect service

                //if(language != english)
                //{
                    //eb.publish("com.github.weaselworks.happiness.machinetranslation", jo);

                //}
                //else{
                    //publish to the sentiment analyser
                eb.publish("com.github.weaselworks.happiness.sentimentanalyser", jo);
                //}

            }
        };

        eb.registerHandler("com.github.weaselworks.happiness.languagedetect", languageDetectHandler);

    }
}
