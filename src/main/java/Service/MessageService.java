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
        /* double check if message String was empty using both built-in 
        1) String.isEmpty() & 2) equality operator (==) (probably should've used .equals()) */
        if(message.isEmpty() || message == ""){  
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
    /* Aside:'messageDAO.getMessageByMsgId()' method in 'deleteMessagebyMsgId()' & 'updateMessagebyMsgId()' too */
    public Message getMessageByMsgId(int msgId){
        // call .getMessageByMsgId() to retrieve message connected to arg 'id'
        return messageDAO.getMessageByMsgId(msgId);
    }

    /* Wonder if both deleteMessagebyId() & updateMessageById() methods 
    could be OMITTED as they are of VOID return type ... */
    
    /* The deletion of an existing message should remove an existing message from the database. 
    1) If the message existed, the response body should contain the now-deleted message.
    2) If the message did not exist, the response status should be 200 (done in 'SocialMediaController.java'), 
    but the response body should be empty */
    // ## 6: Our API should be able to delete a message identified by a message ID.
    public Message deleteMessagebyMsgId(int msgId){
         // create a Message variable 'selectedMsg' assigned w/ values -- retrieve from existing msg by its ID
         Message selectedMsg = messageDAO.getMessageByMsgId(msgId);

         // if 'msgId' DOES NOT EXIST as a 'message_id' in 'message' DB table 
        if(!messageDAO.msgCheckMsgId(msgId)){
            return null;
        }
        // if there are no such msg ...
        // if(selectedMsg == null){
        //     return null;
        // }
        // otw if there is a record/row of msg in 'message' table w/ this specific 'account_id'
        else{
            // invoke .deleteMessageByMsgId() to perform DELETE on record
            messageDAO.deleteMessagebyMsgId(msgId);
        }
        // after DELETE calling .getMessageByMsgId() show the record AFTER DELETE operation
        // return(messageDAO.getMessageByMsgId(msgId));

        /*  to show the 'msg' record b4 DELETE return 'selectedMsg' var 
        -- which was initialized w/ previous row fields BEFORE DELETE operation */
        return selectedMsg;
    }

    // messageDAO.deleteMessagebyId(msg);
    /* The update of a message should be successful if and only if the 
    1) message id already exists and the 
    2) new message_text is not blank and 
    3) is not over 255 characters. */
    // ## 7: Our API should be able to update a message text identified by a message ID.
    public Message updateMessagebyMsgId(int msgId, Message msg){
        // create a Message variable 'selectedMsg' assigned w/ values -- retrieve from existing msg by its ID
        Message selectedMsg = messageDAO.getMessageByMsgId(msgId);
        // assign 'message_text' of 'msg' record to 'currentMsg'
        String currentMsg = msg.getMessage_text();

        // if 'msgId' DOES NOT EXIST as a 'message_id' in 'message' DB table 
        if(!messageDAO.msgCheckMsgId(msgId)){
            return null;
        }
        // else if record EXIST but ... there are no such msg (no 'message_text' tied to given 'message_id')...
        else if(currentMsg.isEmpty()){  // Note: use .isEmpty() over --> else if(selectedMsg == null){
            return null;
        }
        // else if record EXIST but ... 'message_text' exceeds char limit ... 
        else if(currentMsg.length() > 255){
            return null;
        }

        // utilize messageDAO.updateMessageByMsgId() method to UPDATE row/record for selected 'msgId'
        messageDAO.updateMessageByMsgId(msgId, selectedMsg);

        // calling .getMessageByMsgId() to get UPDATE-d record
        return(messageDAO.getMessageByMsgId(msgId));
    }

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
