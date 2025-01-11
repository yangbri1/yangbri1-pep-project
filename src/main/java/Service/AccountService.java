package Service;
// import related 'Account' classes in for use
import Model.Account;
import DAO.AccountDAO;

// import java.util.List;

//
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

    // public Account getAccount
}
