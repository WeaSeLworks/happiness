package com.github.weaselworks.happiness.twitter;

import org.slf4j.Logger;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

/**
 * Created by nick on 25/10/2014.
 */
public class Server extends Verticle {

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(TwitterVerticle.class);


    @Override
    public void start() {

        logger.info("STARTING HTTP SERVER *******************");

        String portEnv = getContainer().env().get("PORT");
        String hostEnv = getContainer().env().get("HOST");

        logger.info("Port ="+portEnv);
        int port = portEnv == null ? 9090 : Integer.parseInt(portEnv);
        String host = hostEnv ==null ? "0.0.0.0" : hostEnv;
        HttpServer server = vertx.createHttpServer();

        server.requestHandler(new Handler<HttpServerRequest>() {

            @Override
            public void handle(HttpServerRequest req) {

                // Serve up the index.html
                if (req.path().equals("/")) {
                    req.response().sendFile("web/index.html");
                }

                // Should this really need to be here?!?
                else if (req.path().equals("/vertxbus-2.1.js")) {
                    req.response().sendFile("web/vertxbus-2.1.js");
                }

                else if (req.path().equals("/small-dot-icon.png")) {
                    req.response().sendFile("web/small-dot-icon.png");
                }

                else if (req.path().equals("/gmaps-heatmap.js")) {
                    req.response().sendFile("web/gmaps-heatmap.js");
                }

                else if (req.path().equals("/heatmap.js")) {
                    req.response().sendFile("web/heatmap.js");
                }



            }

        });

        JsonObject config = new JsonObject();
        config.putString("prefix", "/eventbus");

        // Allow messages in from the client
        JsonArray inboundPermitted = new JsonArray();
        inboundPermitted.add(new JsonObject().putString("address", "msg.client"));

        // Allow messages out to the client
        JsonArray outboundPermitted = new JsonArray();
        outboundPermitted.add(new JsonObject().putString("address", "msg.server"));

        vertx.createSockJSServer(server).bridge(config, inboundPermitted, outboundPermitted);

        // Test connection by sending a heartbeat message every 5 seconds
//        long timerID = vertx.setPeriodic(5000, new Handler<Long>() {
//            public void handle(Long timerID) {
//
//                System.out.println("Sending heartbeat");
//                vertx.eventBus().send("msg.server",new JsonObject().putString("msg", "Heartbeat - timestamp at server: " + System.currentTimeMillis()));
//            }
//        });

        // Register a handler to listen for messages to this
        final EventBus eb = vertx.eventBus();
        eb.registerHandler("com.github.weaselworks.happiness.twitter.server", new Handler<Message>() {

            @Override
            public void handle(Message event) {
                JsonObject tweet = (JsonObject)event.body();
                if (tweet.getInteger("sentimentScore") != 0) {
                    eb.send("msg.server", event.body());
                }
            }
        });

        System.out.println("Happiness server is running on port " + port);
        server.listen(port, host);

    }

}
