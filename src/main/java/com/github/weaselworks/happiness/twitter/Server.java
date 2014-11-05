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

import static com.github.weaselworks.happiness.twitter.HappinessConstants.*;

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

                String path = req.path();

                // Serve up the index.html
                if (path.equals("/")) {
                    req.response().sendFile("web/index.html");
                }
                else req.response().sendFile("web/" + path);

            }

        });

        JsonObject config = new JsonObject();
        config.putString("prefix", "/eventbus");

        // Allow messages in from the client
        JsonArray inboundPermitted = new JsonArray();
        inboundPermitted.add(new JsonObject().putString("address", SERVER_CLIENT_INBOUND_ADDRESS));

        // Allow messages out to the client
        JsonArray outboundPermitted = new JsonArray();
        outboundPermitted.add(new JsonObject().putString("address", SERVER_CLIENT_OUTBOUND_ADDRESS));

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
        eb.registerHandler(SERVER_ADDRESS, new Handler<Message>() {

            @Override
            public void handle(Message event) {
                JsonObject tweet = (JsonObject)event.body();
                if (tweet.getInteger(SENTIMENT_SCORE_PROPERTY) != 0) {
                    eb.send(SERVER_CLIENT_OUTBOUND_ADDRESS, event.body());
                }
            }
        });

        System.out.println("Happiness server is running on port " + port);
        server.listen(port, host);

    }

}
