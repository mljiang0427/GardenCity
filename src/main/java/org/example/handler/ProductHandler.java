package org.example.handler;

import com.google.gson.Gson;
import org.example.db.DatabaseFactory;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

@Path("product")
public class ProductHandler {

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProducts() {
        try (Connection connection = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT products.id, products.price, products.name, p_images.img FROM products JOIN p_images on products.id = p_images.productId");
             ResultSet rs = ps.executeQuery()) {
             JSONArray result = new JSONArray();
             while(rs.next()) {
                 JSONObject o = new JSONObject();
                 o.put("id", rs.getString("id"));
                 o.put("price", rs.getString("price"));
                 o.put("name", rs.getString("name"));
                 o.put("image", rs.getBytes("img"));
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
            @FormDataParam("image") InputStream image,
            @QueryParam("name") String name,
            @QueryParam("price") String price) {
        try {
            UUID uuid = UUID.randomUUID();
            String uuidString = uuid.toString();
            try (Connection connection = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement ps = connection.prepareStatement("INSERT INTO products(id, name, price) values (?, ?, ?);");) {
                ps.setString(1, uuidString);
                ps.setString(2, name);
                ps.setDouble(3, Double.parseDouble(price));
                ps.execute();
                try(PreparedStatement ps1 = connection.prepareStatement("INSERT INTO p_images(id, productId, img) values(uuid(), ?, ?);");) {
                    ps1.setString(1, uuidString);
                    ps1.setBlob(2, image);
                    ps1.execute();
                }
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

//    @GET
//    @Path("/getImg/{}") //path param
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getProductImg() {
//
//    }
}