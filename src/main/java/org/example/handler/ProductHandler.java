package org.example.handler;

import io.muserver.UploadedFile;
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
import java.util.UUID;

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
    @POST
    @Path("/add")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addProduct(
            @FormParam("name") String name,
            @FormParam("price") String price,
            @FormParam("image") UploadedFile image) {
        try {
            UUID uuid = UUID.randomUUID();
            String uuidString = uuid.toString();
            try (Connection connection = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement ps = connection.prepareStatement("INSERT INTO products(id, name, price, img) values (?, ?, ?, ?);");) {
                ps.setString(1, uuidString);
                ps.setString(2, name);
                ps.setDouble(3, Double.parseDouble(price));
                ps.setBytes(4, image.asBytes());
                ps.execute();
                return Response.status(Response.Status.OK)
                        .entity("Successfully added product with id: " + uuidString)
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Service error")
                    .build();
        }
    }

}