package Service;
// import related 'Account' classes in for use
import Model.Account;
import DAO.AccountDAO;

// import java.util.List;

public class AccountService {
    // declare an instance variable 'accountDAO'
    AccountDAO accountDAO;

    /**
     * No-args constructor for a flightService instantiates a plain flightDAO.
     * There is no need to modify this constructor.
     */
    /* not args constructor */
    public AccountService(){
        // instantiate 'AccountDAO' obj within 'AccountService' class
        accountDAO = new AccountDAO();
    } 

    /**
     * Constructor for a flightService when a flightDAO is provided.
     * This is used for when a mock flightDAO that exhibits mock behavior is used in the test cases.
     * This would allow the testing of FlightService independently of FlightDAO.
     * There is no need to modify this constructor.
     * @param flightDAO
     */
    // constructor instantiating above instance variable 'accountDAO' by taking in an 'AccountDAO' obj
    /* introduce 'AccountDAO' obj in 'AccountService' obj -- emables 'AccountService' class to use 'AccountDAO' functionalities */
    public AccountService(AccountDAO accountDAO){
        // 'this' keyword points to the instance variable 'accountDAO' (outside of this constructor)
        // & assign arg's 'accountDAO' value to it
        this.accountDAO = accountDAO;
    }
    // ## 1: Our API should be able to process new User registrations.
    // method signature identical to '.createAccount()' method in 'AccountDAO.java' class
    public Account addAccount(Account account){
        // initialize variables to w/ respective values from their calling .getUsername() & .getPassword() methods
        String username = account.getUsername();
        String password = account.getPassword();

        /* sometimes there will be some form validation on the front-end --- this is on the backend ... */ 
        // if provided 'password' < 4 in length ...
        if(password.length() < 4){
            // return falsy value
            return null;
        }
        // else if nothing was detected in the 'username' field ...
        else if(username.isEmpty()){
            // account creation has failed
            return null;
        }
        // else if username already taken
        else if(accountDAO.accountAlreadyExist(username)){
            // indicate so
            return null;
        }
        // if both 'username' & 'password' fields pass the set constraints --- register/create the new account
        // call on AccountDAO's .createAccount() method to INSERT (SQL DML) a new row/record into existing 'account' DB TABLE
        // return .createAccount() as it is since return Account type from that yields the newly INSERT-ed row 
        return accountDAO.createAccount(account);
    }
    // ## 2: Our API should be able to process User logins.
    /* The login will be successful if and only if the username and password 
    provided in the request body JSON match a real account existing on the database. */
    // method signature identical to namesake in 'AccountDAO.java' class
    public Account loginAccountGetAccId(Account account){
        // assign retrieved username, password, account_id from arg obj 'account' to String variables
        String username = account.getUsername();
        String password = account.getPassword();
        // int accountId = account.getAccount_id();
        /* pass in above initialized 'username' & 'password' variables as parameters to ...
        'AccountDAO' class' accountAlreadyExist() & accCheckPassword() to check if 'username' & 'password' values are in 'Account' table */
        boolean usernameInDB = accountDAO.accountAlreadyExist(username);    // 'username' boolean
        boolean passInDB = accountDAO.accCheckPassword(password);           // 'password' boolean

        /* PRELIMINARY CHECK:  Since in order to create/register an account, the username & password were subjected to some constraints ...
         * ... under the ASSUMPTION that all records INSERT INTO 'Account' DB table so far has been following these guidelines ...
         * ... it only fits to ASSUME if any of the arg obj 'account' being tested also should fit in as well.
         * ------------------------------------------------------------------------------------------------- */ 
        // if provided 'password' < 4 in length ...
        if(password.length() < 4){
            // return falsy value
            return null;
        }
        // else if nothing was detected in the 'username' field ...
        else if(username.isEmpty()){
            // account creation has failed
            return null;
        }
        // else if username already taken
        else if(accountDAO.accountAlreadyExist(username)){   // OMITTED: Since we are looking for matches now
            // indicate so
            return null;
        }
        /* ------------------------------------ END OF PRE-CHECK -------------------------------------------- */
        // else if 'username' DNE in 'Account' DB table
        if(usernameInDB == false){
            return null;
        }
        // or f 'password' DNE in 'Account' DB table
        else if(passInDB == false){
            return null;
        }
        
        // otw if given username & password matches their fields' credentials in existing 'Account' DB table
        // incite .loginAccountGetAccId() method
        return accountDAO.loginAccountGetAccId(account);
    }
}
