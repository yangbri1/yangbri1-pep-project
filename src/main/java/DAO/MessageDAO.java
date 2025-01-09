package DAO;

import Util.ConnectionUtil;
import Model.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A DAO is a class that mediates the transformation of data between the format of objects in Java to rows in a
 * database. The methods here are mostly filled out, you will just need to add a SQL statement.
 */

public class MessageDAO {
    /**
     * TODO: API should be able to retrieve all messages from 'message' table
     * You only need to change the sql String.
     * @return all Books.
     */
    public List<Message> getAllMessages(){
        Connection connection = ConnectionUtil.getConnection();
        List<Message> messages = new ArrayList<>();
        try {
            // write/create SQL String 
            String sql = "SELECT * FROM message";
            // Note: Use Wildcard * to shorthand select all columns in 'message' table to show
            // utilizing 'PreparedStatement' interface over 'Statement' interface in executing SQL statement b/c PS pre-compiles SQL & lower likelihood of SQL Injection from User input
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                Message msg = new Message(rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        // rs.getLong("time_posted_epoch"),
                        rs.getInt("account_id"));
                messages.add(msg);
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        // return List of 'messages' w/ Obj type of String (Recall: Lists only take Complex Obj Types -- NOT primitives)
        return messages;
    }
}
