package DAO;

import Util.ConnectionUtil;
import Model.Account;
// import Model.Message;
// import Model.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
// import java.util.ArrayList;
// import java.util.List;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {
    // to retrieve List of registered accounts --- no req. but for testing on Thunder-Client/POSTMAN
    public List<Account> getAllAccounts(){
        Connection connection = ConnectionUtil.getConnection();
        List<Account> accounts = new ArrayList<>();
        try {
            // write/create SQL String 
            String sql = "SELECT * FROM account";
            // Note: Use Wildcard * to shorthand select all columns in 'account' table to show
            // utilizing 'PreparedStatement' interface over 'Statement' interface in executing SQL statement b/c PS pre-compiles SQL & lower likelihood of SQL Injection from User input
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                Account acc = new Account(
                    rs.getInt("account_id"),
                    rs.getString("username"),
                    rs.getString("password"));
                    
                accounts.add(acc);
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        // return List of 'accounts' w/ Obj type of String (Recall: Lists only take Complex Obj Types -- NOT primitives)
        return accounts;
    }

    // ## 1: Our API should be able to process new User registrations (create).
    public Account createAccount(Account acc){
        // create a connection linking to database (where 'account' table is at)
        Connection connection = ConnectionUtil.getConnection();
        try {
            // write/create SQL statement (reflective of 'PreparedStatement' Interface
            /* readme.md -- "The body will contain a representation of a JSON Account, but will not contain an account_id." */
            // aka only need to define the other two values 'username' & 'password' (both of variable-length character type)
            String sql = "INSERT INTO account (username, password) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            /* write preparedStatement's setString methods here for respective placeholders */ 
            // call .getUsername() method from 'Account.java' class obj to retrieve input username, convert to compatible SQL datatype & place at 1st (?) placeholder
            preparedStatement.setString(1, acc.getUsername());
            // for 2nd (?) statement use 'Account' class getter method .getPassword() on inputted arg 'acc' obj to retrieve respective data as well
            preparedStatement.setString(2, acc.getPassword());
            // send SQL query to DB
            preparedStatement.executeUpdate();
            ResultSet pkeyResultSet = preparedStatement.getGeneratedKeys();
            // ResultSet.next() method will points to next row/record -- yields false if at end of table
            if(pkeyResultSet.next()){
                // .getLong() method from ResultSet retrieves data from selected column -- 1 (account_id is 1st column of 'account' table -- account.java) & cast to int type
                int newAccId = (int) pkeyResultSet.getLong(1);
                // create new Account obj record w/ account_id attached onto other values username, password  
                Account newAccount = new Account(newAccId, acc.getUsername(), acc.getPassword());
                
                // return newly created Account row/record
                return newAccount;
            }
        }
        // catch any thrown Exceptions
        catch(SQLException e){
            // console log out any encountered Exception messages to CLI
            System.out.println(e.getMessage());
        }
        return null;
    }
    // ## 1a) Helper function/method to check if 'username' already exists in 'account' table
    public boolean accountAlreadyExist(String username){
        Connection connection = ConnectionUtil.getConnection();
        try {
            // write/create SQL query String
            String sql = "SELECT * FROM account WHERE username = ?";
            
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            //write preparedStatement's setString and setInt methods here.
            preparedStatement.setString(1, username);    // at 1st (?) set to arg's 'username'
            // execute/send SQL queries to DB
            ResultSet rs = preparedStatement.executeQuery();
            // check if ResultSet is not empty -- contains records to move cursor to (same as while(rs.next(){ return true; } just more direct))
            if(rs.next()){  
                // 'username' already exists in the DB table
                return true;
            }
        // if any Exceptions are caught
        }catch(SQLException e){
            // console log out to terminal
            System.out.println(e.getMessage());
        }
        // otw return false indicates 'username' DNE in DB table
        return false;
    }
    // ## 2a) Helper function/method to check if 'password' already exists in 'account' table
    public boolean accCheckPassword(String password){
        Connection connection = ConnectionUtil.getConnection();
        try {
            // write/create SQL query String
            String sql = "SELECT * FROM account WHERE password = ?";
            
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            //write preparedStatement's setString and setInt methods here.
            preparedStatement.setString(1, password);    // at 1st (?) set to arg's 'password'
            // execute/send SQL queries to DB
            ResultSet rs = preparedStatement.executeQuery();
            // check if ResultSet is not empty -- contains records to move cursor to (same as while(rs.next(){ return true; } just more direct))
            if(rs.next()){  
                // username already exists in the DB table
                return true;
            }
        // if any Exceptions are caught
        }catch(SQLException e){
            // console log out to terminal
            System.out.println(e.getMessage());
        }
        // otw return false indicates 'password' DNE in DB table
        return false;
    }
    // ## 2: Our API should be able to process User logins.

    // ## 3) Helper function/method to check if user's id ('posted_by / 'account_id') already exists in 'account' table
    /* Could use method overloading (same method name as 'accountAlreadyExist()' above w/ dif. # of args or arg data types but want to easier differentiate */
    public boolean accCheckUserId(int userId){
        Connection connection = ConnectionUtil.getConnection();
        try {
            // write/create SQL query String
            String sql = "SELECT * FROM account WHERE account_id = ?";
            
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            // write preparedStatement's setString and setInt methods here.
            preparedStatement.setInt(1, userId);    // at 1st (?) set to arg's 'userId'
            // execute/send SQL queries to DB
            ResultSet rs = preparedStatement.executeQuery();
            // check if ResultSet is not empty -- contains records to move cursor to (same as while(rs.next(){ return true; } just more direct))
            if(rs.next()){  
                // 'account_id' already exists in the DB table
                return true;
            }
        // if any Exceptions are caught
        }catch(SQLException e){
            // console log out to terminal
            System.out.println(e.getMessage());
        }
        // otw return false indicates username DNE in DB table
        return false;
    }

    // ## 2: Our API should be able to process User logins.
    // public Account loginAccountGetAccId(Account acc){
    //     // connect account table in database
    //     Connection connection = ConnectionUtil.getConnection();
    //     // try-catch block for error-handling
    //     try {
    //         // start of SQL logic
    //         // write/create SQL query String using wildcard (*) to include all columns & filtering w/ 'username', 'password' conditions
    //         // String sql = "SELECT * FROM account WHERE username = ? AND password = ?";
    //         // create SQL statement -- from 'AccountDAO.java' 
    //         String sql = "INSERT INTO account (username, password) VALUES (?, ?)" ;
    //         PreparedStatement preparedStatement = connection.prepareStatement(sql);

    //         // call .getUsername() & .getPassword() getter methods from 'Account.java' class to retrieve spective data
    //         // set parameterized queries w/ respective resource data
    //         preparedStatement.setString(1, acc.getUsername()); 
    //         preparedStatement.setString(2, acc.getPassword());      
    //         // execute/send SQL queries to DB
    //         ResultSet rs = preparedStatement.executeQuery();
    //         // under the impression ResultSet not being empty (next row/record is NOT empty)
    //         while(rs.next()){
    //             // create an instance of Account obj -- retrieve from Message obj
    //             Account accFound = new Account(
    //                 rs.getInt("account_id"), 
    //                 rs.getString("username"),
    //                 rs.getString("password"));
    //                 /* thought could chain them too (from lecture example) --- syntax may be off */
    //                 // rs.getInt("account_id")
    //                 // .getString("username")
    //                 // .getString("password"));
    //             // return wanted account obj credentials
    //             return accFound;
    //         }
    //     // if any Exceptions are caught
    //     }catch(SQLException e){
    //         // console log out to terminal
    //         System.out.println(e.getMessage());
    //     }
    //     // otherwise return null status code 404 NOT FOUND -- if data not in DB
    //     return null;
    // }

    // ## 2b) find an account by its username
    public Account getAccountByUsername(String username){
        Connection connection = ConnectionUtil.getConnection();
        try {
            // write/create SQL query String
            String sql = "SELECT * FROM account WHERE username = ?";
            
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            //write preparedStatement's setString and setInt methods here.
            preparedStatement.setString(1, username);    // at 1st (?) set to argument 'username'
            // execute/send SQL queries to DB
            ResultSet rs = preparedStatement.executeQuery();
            // while ResultSet is not empty (more records) to point to next record/row
            while(rs.next()){
                // create an instance of Account obj -- retrieve from Account obj
                Account acc = new Account(
                    rs.getInt("account_id"),
                    rs.getString("username"),
                    rs.getString("password"));
                // return 'acc' connected to given arg 'usernane'
                return acc;  
                // return acc.password;    // returns password field linked to this 'acc'
            }
        // if any Exceptions are caught
        }catch(SQLException e){
            // console log out to terminal
            System.out.println(e.getMessage());
        }
        // otherwise return UNAUTHORIZED 401 status code  -- if data not in DB
        return null;
    }
    
}
