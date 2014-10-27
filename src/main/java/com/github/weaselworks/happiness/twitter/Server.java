package com.github.weaselworks.happiness.twitter;

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.sockjs.SockJSServer;
import org.vertx.java.core.sockjs.SockJSSocket;
import org.vertx.java.platform.Verticle;

/**
 * Created by nick on 25/10/2014.
 */
public class Server extends Verticle {

    @Override
    public void start() {

        final EventBus eb = vertx.eventBus();
        eb.registerHandler("com.github.weaselworks.happiness.twitter.server", new Handler<Message<JsonObject>>() {

            @Override
            public void handle(Message<JsonObject> event) {

                JsonObject o = (JsonObject) event.body();



            }
        });

        int port = 9090;
        HttpServer server = vertx.createHttpServer();

        server.requestHandler(new Handler<HttpServerRequest>() {

            @Override
            public void handle(HttpServerRequest req) {

                // Serve up the index.html
                if (req.path().equals("/")) req.response().sendFile("src/main/resources/web/index.html");

            }

        });

        // Start SockJSSever
        SockJSServer sockServer = vertx.createSockJSServer(server);

        sockServer.installApp(new JsonObject().putString("prefix", "/happiness"), new Handler<SockJSSocket>() {

            @Override
            public void handle(final SockJSSocket event) {
                event.dataHandler(new Handler<Buffer>() {

                    @Override
                    public void handle(Buffer data) {

                        // Echo the message back to the client
                        event.write(data);
                    }
                });

            }

        });

        System.out.println("Happiness server is running on port " + port);
        server.listen(port);

    }

}
