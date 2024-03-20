package org.example;

import io.muserver.*;
import io.muserver.rest.RestHandlerBuilder;
import org.example.db.DatabaseFactory;
import org.example.handler.ProductHandler;
import org.example.handler.UserHandler;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;

import static io.muserver.ContextHandlerBuilder.context;
import static io.muserver.MuServerBuilder.httpServer;

public class App
{

    public static void main( String[] args ) {
        try {
            MuServer server = httpServer()
                    .withHttpPort(8080)
                    .addHandler(context("garden_city")
                            .addHandler(RestHandlerBuilder.restHandler(new UserHandler()))
                            .addHandler(RestHandlerBuilder.restHandler(new ProductHandler())))
                    .start();
            System.out.println("Service is running on..." + server.uri());
//            insertProducts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void insertProducts() {
        Path directory = Paths.get(App.class.getClassLoader().getResource("imgs").getFile());
        if (!Files.exists(directory) || !Files.isDirectory(directory)) {
            System.out.println("Directory does not exist or is not a directory.");
            return;
        }
        try {
            Files.list(directory).forEach(path -> {
                try(Connection connection = DatabaseFactory.getInstance().getConnection();
                    PreparedStatement ps = connection.prepareStatement("INSERT into products (id, name, price, image) values (uuid(), ?, ?, ?);")) {
                    ps.setString(1, path.getFileName().toString().substring(0, path.getFileName().toString().indexOf(".")));
                    ps.setDouble(2, (Math.random() + 3) + (Math.random() * 10 + Math.random()));
                    ps.setString(3, "http://localhost:8080/garden_city/product/img/" + path.getFileName());
                    ps.execute();
                    System.out.println("successfully added product " + path.getFileName().toString().substring(0, path.getFileName().toString().indexOf(".")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
