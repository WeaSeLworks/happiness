package com.github.weaselworks.happiness.twitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.platform.Verticle;

/**
 * Created by steve on 27/10/2014.
 */
public class MachineTranslation extends Verticle {

    Logger logger = LoggerFactory.getLogger(MachineTranslation.class);

    public void start(){
        super.start();

        final EventBus eb = vertx.eventBus();

        Handler<Message> machineTranslationHandler = new Handler<Message>() {
            public void handle(Message message) {

                logger.info("message received "+message);

                //perform some action in here to call the bluemix machine translation service
                //then either swap out the original text for the translation text or
                //append the translated text as an additional property


                eb.publish("com.github.weaselworks.happiness.sentimentanalyser", message);


            }
        };

        eb.registerHandler("com.github.weaselworks.happiness.machinetranslation", machineTranslationHandler);

    }
}
