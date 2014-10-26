package com.github.weaselworks.happiness.twitter;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;
import com.nhncorp.mods.socket.io.SocketIOServer;
import com.nhncorp.mods.socket.io.SocketIOSocket;
import com.nhncorp.mods.socket.io.impl.DefaultSocketIOServer;

/**
 * Created by nick on 25/10/2014.
 */
public class Server extends Verticle {

    @Override
    public void start() {

        int port = 9090;
        HttpServer server = vertx.createHttpServer();
        SocketIOServer io = new DefaultSocketIOServer(vertx, server);

        io.sockets().onConnection(new Handler<SocketIOSocket>() {

            public void handle(final SocketIOSocket socket) {

                socket.on("timer", new Handler<JsonObject>() {

                    public void handle(JsonObject event) {
                        socket.emit("timer", event);
                    }

                });

                // Define behaviour for 'start tweets' channel
                socket.on("start tweets", new Handler<JsonObject>() {

                    public void handle(JsonObject event) {

                        System.out.println("Start Tweets received");

                        // Placeholder for Twitter Stream initialisation?

                    }

                });


            }

        });

        System.out.println("Happiness server is running on port " + port);
        server.listen(port);

    }

}
