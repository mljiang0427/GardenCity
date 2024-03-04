package org.example;

import io.muserver.*;
import io.muserver.rest.RestHandlerBuilder;
import org.example.handler.UserHandler;

import static io.muserver.ContextHandlerBuilder.context;
import static io.muserver.MuServerBuilder.httpServer;

public class App
{

    public static void main( String[] args ) {
        try {
            MuServer server = httpServer()
                    .withHttpPort(8080)
                    .addHandler(context("garden_city")
                            .addHandler(RestHandlerBuilder.restHandler(new UserHandler())))
                    .start();
            System.out.println("Service is running on..." + server.uri());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
