package com.github.weaselworks.happiness.twitter;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.platform.Verticle;
import twitter4j.Status;
import uk.bl.wa.sentimentalj.Sentiment;
import uk.bl.wa.sentimentalj.SentimentalJ;

public class SentimentAnalyser extends Verticle {

    Logger logger = LoggerFactory.getLogger(SentimentAnalyser.class);



    @Override
    public void start() {

        final EventBus eb = vertx.eventBus();

        Handler<Message> sentimentHandler = new Handler<Message>() {
            public void handle(Message message) {
                logger.debug("message received " + message.body());

                try{


                    Status status = twitter4j.json.DataObjectFactory.createStatus(message.body().toString());

                    SentimentalJ sj = new SentimentalJ();
                    String jsonTwitterString = status.getText();
                    Sentiment s = sj.positivity(jsonTwitterString);



                    logger.info("The status with id "+status.getId()+ "has a positivity score of "+s.getPositive().getScore());

                    JsonObject jo = new JsonParser().parse(jsonTwitterString).getAsJsonObject();

                    jo.addProperty("positivity", s.getScore());

                    eb.publish("com.github.weaselworks.happiness.twitter.server", jo.getAsString());

                }
                catch(Exception e){
                    logger.error("Unable to parse the string back into a twitter status object",e);
                }




            }
        };

        eb.registerHandler("com.github.weaselworks.happiness.sentimentanalyser", sentimentHandler);


    }
}
