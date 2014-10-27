package com.github.weaselworks.happiness.twitter;



import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;
import twitter4j.Status;
import twitter4j.TwitterObjectFactory;
import uk.bl.wa.sentimentalj.Sentiment;
import uk.bl.wa.sentimentalj.SentimentalJ;

public class SentimentAnalyser extends Verticle {

    Logger logger = LoggerFactory.getLogger(SentimentAnalyser.class);



    @Override
    public void start() {

        final EventBus eb = vertx.eventBus();

        Handler<Message<JsonObject>> sentimentHandler = new Handler<Message<JsonObject>>() {
            public void handle(Message<JsonObject> message) {
                logger.debug("message received " + message.body());

                try{

                    SentimentalJ sj = new SentimentalJ();
                    Sentiment s = sj.positivity(message.body().getString("text"));

                    JsonObject jo = message.body();

                    jo.putNumber("positivity", s.getScore());

                    eb.publish("com.github.weaselworks.happiness.twitter.server", jo);

                }
                catch(Exception e){
                    logger.error("Unable to parse the string back into a twitter status object",e);
                }




            }
        };

        eb.registerHandler("com.github.weaselworks.happiness.sentimentanalyser", sentimentHandler);


    }
}
