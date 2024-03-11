package org.example.handler;

import io.muserver.MuRequest;
import org.example.db.DatabaseFactory;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.*;

@Path("user")
public class UserHandler {

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response userLogin(@Context MuRequest request) throws IOException {
        JSONObject json = new JSONObject(request.readBodyAsString());
        try (Connection connection = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM user WHERE email = ? AND password = ?");) {
             ps.setString(1, json.getString("email"));
             ps.setString(2, json.getString("password"));
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
             PreparedStatement ps = connection.prepareStatement("SELECT COUNT(username) FROM user WHERE username = ?");
             PreparedStatement ps1 = connection.prepareStatement("INSERT INTO user (id, username, password) VALUE (uuid(),?,?)")) {
             ps.setString(1, json.getString("userName"));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("User name has already been used")
                            .build();
                }
                ps1.setString(1, json.getString("userName"));
                ps1.setString(2, json.getString("password"));
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
        JSONObject user = new JSONObject();
        //try with resource
        try(Connection connection = DatabaseFactory.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT id, username FROM user WHERE username = ?;")) {
            ps.setString(1, userName);
            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    user.put("id", rs.getString("id"));
                    user.put("username", rs.getString("username"));
                }
                return Response.ok()
                        .entity(user.toString())
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