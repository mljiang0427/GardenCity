package org.example.handler;

import io.muserver.MuRequest;
import org.example.db.DatabaseFactory;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.*;

@Path("user")
public class UserHandler {

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    Response test() {
        return Response.ok()
                .entity(new JSONObject().toString())
                .build();
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response userLogin(@Context MuRequest request) throws IOException {
        JSONObject json = new JSONObject(request.readBodyAsString());
        try (Connection connection = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM user WHERE email = ? AND password = ?");) {
             ps.setString(1, json.getString("Email"));
             ps.setString(2, json.getString("Password"));
             try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Response.ok()
                            .entity("Login successfully")
                            .build();
                } else {
                    return Response.status(Response.Status.UNAUTHORIZED)
                            .entity("Email or password is incorrect")
                            .build();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Service error")
                    .build();
        }
    }

    @POST
    @Path("/signup")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(@Context MuRequest request) throws IOException {
        JSONObject json = new JSONObject(request.readBodyAsString());
        try (Connection connection = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT COUNT(email) FROM user WHERE email = ?");
             PreparedStatement ps1 = connection.prepareStatement("INSERT INTO user (id, firstName, lastName, email, password) VALUE (uuid(),?,?,?,?)")) {
             ps.setString(1, json.getString("email"));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("User email has already been used")
                            .build();
                }
                ps1.setString(1, json.getString("firstName"));
                ps1.setString(2, json.getString("lastName"));
                ps1.setString(3, json.getString("email"));
                ps1.setString(4, json.getString("password"));
                ps1.execute();
            }
            return Response.ok()
                    .entity("User created successfully")
                    .build();
       } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Service error")
                    .build();
        }
    }
    @GET
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("username") String userName) {
        System.out.println("receive request....");
        JSONObject user = new JSONObject();
//        Response response =  Response.ok()
//                .header("Access-Control-Allow-Origin", "*")
//                .entity(user.toString())
//                .build();
//        Response.ResponseBuilder responseBuilder = response.newBuilder();
//        responseBuilder.header("Access-Control-Allow-Origin", "*");
        //try with resource
//        try(Connection connection = DatabaseFactory.getInstance().getConnection();
//            PreparedStatement ps = connection.prepareStatement("SELECT id FROM user WHERE id = ?;")) {
//            ps.setString(1, userName);
//            try(ResultSet rs = ps.executeQuery()) {
//                while(rs.next()) {
//                    user.put("id", rs.getString("id"));
//                }
                return Response.ok()
                        .header("Access-Control-Allow-Origin", "*")
                        .entity(user.toString())
                        .build();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
//                    .entity("Service error")
//                    .build();
//        }
    }

}