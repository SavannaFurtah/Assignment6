/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Services;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

/**
 *
 * @author c0656308
 */
@ApplicationScoped
public class MessageController {

    List<Message> messages = new ArrayList<>();
    private int count = 1;

    public MessageController() {

    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public JsonArray getAllJson() {
        JsonArrayBuilder json = Json.createArrayBuilder();
        for (Message m : messages) {
            json.add(m.toJson());
        }
        return json.build();
    }

    public JsonObject addJson(JsonObject json) {
        Message newMess = new Message(json);
        newMess.setId(count++);
        messages.add(newMess);
        return json;
    }

    public Message getId(int id) {
        for (Message m : messages) {
            if (m.getId() == id) {
                return m;
            }
        }
        return null;
    }

    public JsonObject getIdJson(int id) {
        Message m = getId(id);
        if (m != null) {
            return getId(id).toJson();
        } else {
            return null;
        }
    }

    public JsonArray getByDateJson(Date date1, Date date2) {
        JsonArrayBuilder json = Json.createArrayBuilder();
        messages.stream().filter((m) -> (m.getSenttime().equals(date1) || m.getSenttime().equals(date2) || m.getSenttime().after(date1) && m.getSenttime().before(date2))).forEachOrdered((m) -> {
            json.add(m.toJson());
        });
        return json.build();
    }
    
    public JsonObject editJson(int id, JsonObject json){
       Message m = getId(id);
       m.setTitle(json.getString("title", ""));
       m.setContents(json.getString("contents", ""));
       m.setAuthor(json.getString("author", ""));
       String time = json.getString("senttime", "");
       try{
           m.setSenttime(m.sdf.parse(time));
       } catch (ParseException ex) {
           m.setSenttime(new Date());
       }        
       return m.toJson();
    }
    
    public Boolean deleteById(int id){
        Message m = getId(id);
        if (m!= null){
            messages.remove(m);
            return true;
        } else
            return false;
    }
}
