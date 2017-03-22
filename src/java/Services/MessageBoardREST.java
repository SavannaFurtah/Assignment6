/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Services;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import static javax.ws.rs.HttpMethod.POST;
import static javax.ws.rs.HttpMethod.PUT;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 *
 * @author c0656308
 */
@Path("/messages")
@ApplicationScoped
public class MessageBoardREST {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    @Inject
    private MessageController msgCtrl;
    
    @GET
    @Produces("application/json")
    public JsonArray getAll() {
        return msgCtrl.getAllJson();

    }

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public JsonObject postJson(JsonObject json) {
        return msgCtrl.addJson(json);
        
    }
    
    @GET
    @Path("{id}")
    @Produces("application/json")
    public JsonObject getId(@PathParam("id") int id){
    JsonObject json = msgCtrl.getIdJson(id);
    if (json != null){
        return json;
    }
    return null;
    }
    
    @GET
    @Path("{date1}/{date2}")
    @Produces("application/json")
    public JsonArray getDate(@PathParam("date1") String date1,@PathParam("date2") String date2 ) throws ParseException{
       try{
           return msgCtrl.getByDateJson(sdf.parse(date1),sdf.parse(date2));
       }
       catch (ParseException ex){
          return (JsonArray) (JsonObject) Response.status(Response.Status.NOT_FOUND).build(); 
       }
    }
    
    @PUT
    @Path("{id}")
    @Consumes("application/json")
    @Produces("application/json")
    public JsonObject editJson(@PathParam("id") int id, JsonObject json){
         JsonObject json2 = msgCtrl.editJson(id, json);
         if (json2 != null){
             return json2;
         }
          return null;
    } 
    
    @DELETE
    @Path("{id}")
    @Consumes("application/json")
    @Produces("application/json")
    public Response delete(@PathParam("id") int id){
        if (msgCtrl.deleteById(id)){
            return Response.ok().build();
        }
        else return Response.status(Response.Status.NOT_FOUND).build();
    }
}
