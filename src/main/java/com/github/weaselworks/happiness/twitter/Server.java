package com.github.weaselworks.happiness.twitter;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

/**
 * Created by nick on 25/10/2014.
 */
public class Server extends Verticle {

    @Override
    public void start() {

        int port = 9090;
        HttpServer server = vertx.createHttpServer();

        server.requestHandler(new Handler<HttpServerRequest>() {

            @Override
            public void handle(HttpServerRequest req) {

                // Serve up the indexold.html
                if (req.path().equals("/")) {
                    req.response().sendFile("src/main/resources/web/index.html");
                }

                // Should this really need to be here?!?
                else if (req.path().equals("/vertxbus-2.1.js")) {
                    req.response().sendFile("src/main/resources/web/vertxbus-2.1.js");
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
        long timerID = vertx.setPeriodic(5000, new Handler<Long>() {
            public void handle(Long timerID) {

                System.out.println("Sending heartbeat");
                vertx.eventBus().send("msg.server",new JsonObject().putString("msg", "Heartbeat - timestamp at server: " + System.currentTimeMillis()));
            }
        });

        System.out.println("Happiness server is running on port " + port);
        server.listen(port);

    }

}
