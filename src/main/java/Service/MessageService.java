package Service;

/* import 'ConnectUtil' class from custom 'Util' package 
-- responsible for maintaining active connection to DB */
// import Util.ConnectionUtil;

// import related 'Message' classes in for use
import Model.Message;
import DAO.MessageDAO;

// import both 'Account' classes since some methods use them
import Model.Account;
import DAO.AccountDAO;

// import 'PreparedStatement' for executing SQL queries/statements
import java.sql.PreparedStatement;
// import 'ResultSet' for navigating in a DB table
import java.sql.ResultSet;
// import 'SQLException' to classify any potential SQL exceptions that arose
import java.sql.SQLException;
// import 'ArrayList' class to allow dynamic appending of elements to ArrayList
import java.util.ArrayList;
// import List class from java.util package to store large collections of data 
// ex. later used in GET functionality when retrieving msgs
import java.util.List;

public class MessageService {
    // declare an instance variable 'messageDAO' to be instantiated later
    MessageDAO messageDAO;
    
    // create an instance of 'AccountDAO' class obj -- experimental!
    AccountDAO accountDAO = new AccountDAO();

    /* not args constructor */
    public MessageService(){
        // instantiate 'MessageDAO' obj within 'MessageService' (this!) class
        messageDAO = new MessageDAO();
    } 

    // constructor instantiating above instance variable 'messageDAO' by taking in an 'MessageDAO' obj
    /* introduce 'MessageDAO' obj in 'MessageService' obj -- emables 'MessageService' class to use 'MessageDAO' functionalities */
    public MessageService(MessageDAO messageDAO){
        // 'this' keyword points to the instance variable 'messageDAO' (outside of this constructor)
        // & assign arg's 'messageDAO' value to it
        this.messageDAO = messageDAO;
    }

    // ## 3: Our API should be able to process the creation of new messages.
    /* The creation of the message will be successful if and only if the message_text is
    1) is not blank, 
    2) is not over 255 characters, 
    3) and posted_by refers to a real, existing user. */
    // same method signature as 'createMessage()' in 'MessageDAO.java' class
    public Message createMessage(Message msg){
        // assign 'message_text' String value to String variable 'message'
        String message = msg.getMessage_text();
        // assign 'posted_by' int value to int variable 'userId' ...
        // (as 'posted_by' is a FOREIGN KEY REFERENCES-ing to PRIMARY KEY 'account_id' in Account table)
        int userId = msg.getPosted_by();

        // if 'message_text' field is empty ...
        if(message != ""){
            // return falsy value
            return null;
        }
        // else if message exceeds 255 char limit
        else if(message.length() > 255){
            // return falsy value
            return null;
        }
        // else if message's 'posted_by' is NOT FOUND as an 'account_id' in DB table ...
        else if(!accountDAO.accCheckUserId(userId)){
            // indicate so
            return null;
        }
        // otw if all above verifications succeeds, CREATE message
        // invoke .createMessage() in 'MessageDAO.java' class to create a 'msg' in 'message' DB table
        return messageDAO.createMessage(msg);
    }

    // ## 4: Our API should be able to retrieve all messages.
    // retrieve all messages from 'message' DB table
    public List<Message> getAllMessages() {
        // call upon messageDAO's .getAllMessages() function to retrieve all messages in TABLE 'message'
        return messageDAO.getAllMessages();
    }

    // ## 5: Our API should be able to retrieve a message by its ID.
    // fetch specific message by 'message_id'
    public String getMessageById(int msgId){
        // call .getMessageById() to retrieve message connected to arg 'id'
        return messageDAO.getMessageById(msgId);
    }

    /* Wonder if both deleteMessagebyId() & updateMessageById() methods 
    could be OMITTED as they are of VOID return type ... */
    
    // ## 6: Our API should be able to delete a message identified by a message ID.
    public void deleteMessagebyId(int msgId){
        /* OMITTED! deleteMessagebyId() returns void (no return statement) */
        // return messageDAO.deleteMessagebyId(msgId);
    }

    // messageDAO.deleteMessagebyId(msg);
    
    // ## 7: Our API should be able to update a message text identified by a message ID.
    public void updateMessageById(int msgId, Message msg){
        /* OMITTED! updateMessageById() returns void (no return statement) */
        // return messageDAO.updateMessageById(msgId, msg);
    }

    // messageDAO.updateMessageById(msgId, msg);

    // ## 8: Our API should be able to retrieve all messages written by a particular user.
    public List<Message> getAllMessagesByUserId(int userId){
        // obtain messages to arg's 'userId' by calling getAllMessagesByUserId() method in 'MessageDAO' class on given arg's 'userId'
        return messageDAO.getAllMessagesByUserId(userId);
    }

    /* Note: #8, 5, 4 all GET/READ returning a List<Message> obj 
    -- JUST IN CASE: if need to include an empty List<Message> obj when there are NO RECORDS in DB table 'message' 
    -- probably implement it within 'MessageDAO.java' class? 
    -- Use accCheckUserId()? */

}
