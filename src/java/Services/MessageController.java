/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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
import java.sql.*;

/**
 *
 * @author c0656308
 */
@ApplicationScoped
public class MessageController {

    List<Message> messages = new ArrayList<>();
    private Message currentMessage = new Message();
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
        try {
            Message newMess = new Message(json);
            newMess.setId(count++);
            messages.add(newMess);
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM messages WHERE id = ?");
            ResultSet rs = pstmt.executeQuery();
            pstmt.setInt(1,newMess.getId());
            while (rs.next()){
                newMess.setAuthor(rs.getString("author"));
                newMess.setTitle(rs.getString("title"));
                newMess.setContents(rs.getString("contents"));
                String senttime = rs.getString("senttime");
                messages.add(newMess);
            }
            return json;
        } catch (SQLException ex) {
           Logger.getLogger(MessageController.class.getName()).log(Level.SEVERE, null, ex);
        }
     return null;
    }

    public Message getId(int id) {
        for (Message m : messages) {
            if (m.getId() == id) {
                try {
                    Connection conn = getConnection();
                    Statement stmt = conn.createStatement();
                    PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM messages WHERE id = ?");
                    ResultSet rs = pstmt.executeQuery();
                    Message newMess = new Message();
                    pstmt.setInt(1,id);
                    while (rs.next()){
                        newMess.setAuthor(rs.getString("author"));
                        newMess.setTitle(rs.getString("title"));
                        newMess.setContents(rs.getString("contents"));
                        String senttime = rs.getString("senttime");
                    }
                    
                    return newMess;
                } catch (SQLException ex) {
                    Logger.getLogger(MessageController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }

    public JsonObject getIdJson(int id) {
        Message m;
        m = getId(id);
        if (m != null) {
            try {
                Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM messages WHERE id = m");
                return getId(id).toJson();
            } catch (SQLException ex) {
                Logger.getLogger(MessageController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
            return null;       
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
    
    public boolean deleteById(int id){
        Message m = getId(id);
        if (m!= null){
            try {
                Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                PreparedStatement pstmt = conn.prepareStatement("DELETE * FROM messages WHERE id = m");
                messages.remove(m);
                return true;
            } catch (SQLException ex) {
                Logger.getLogger(MessageController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
            return false;
    }
    
    private final static String studentNumber = "c0656308";
    
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            
        }
        String server = "ipro.lambton.on.ca";
        String username = studentNumber + "-java";
        String password = studentNumber;
        String database = username;
        String jdbc = String.format("jdbc:mysql://%s/%s", server, database);
        return DriverManager.getConnection(jdbc, username, password);
    }
}
