package com.github.weaselworks.happiness.twitter;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;
import uk.bl.wa.sentimentalj.Sentiment;
import uk.bl.wa.sentimentalj.SentimentalJ;

public class SentimentAnalyser extends Verticle {

    Logger logger = container.logger();

    @Override
    public void start() {
        EventBus eb = vertx.eventBus();


        Handler<Message> sentimentHandler = new Handler<Message>() {
            public void handle(Message message) {
                logger.debug("message received " + message.body());
                SentimentalJ sj = new SentimentalJ();
                Sentiment s = sj.positivity(message.body().toString());

                //write to event bus here.

            }
        };

        eb.registerHandler("com.github.weaselworks.happiness.sentimentanalyser", sentimentHandler);


    }
}
