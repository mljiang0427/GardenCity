package org.example.handler;

import org.example.db.DatabaseFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;



@Path("product")
public class ProductHandler {

    @GET
    @Path("/img/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getImg(@PathParam("name") String imgName) {
        try(InputStream inputStream =  ProductHandler.class.getClassLoader().getResourceAsStream("imgs/" + imgName)) {
            return Response.status(Response.Status.OK)
                    .entity(inputStream.readAllBytes())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Service error")
                    .build();
        }
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProducts() {
        try (Connection connection = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT id, name, price, image FROM products;");
             ResultSet rs = ps.executeQuery()) {
             JSONArray result = new JSONArray();
             while(rs.next()) {
                 JSONObject o = new JSONObject();
                 o.put("id", rs.getString("id"));
                 o.put("name", rs.getString("name"));
                 o.put("price", rs.getString("price"));
                 o.put("image", rs.getString("image"));
                 result.put(o);
             }
            return Response.status(Response.Status.OK)
                    .entity(result.toString())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Service error")
                    .build();
        }
    }


}

