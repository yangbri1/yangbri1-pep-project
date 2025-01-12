package Controller;

/*
 * SocialMediaController.java class contains endpoints & handlers setup
 */

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    // declare a variable of 'AccountService' class type & a variable of 'MessageService' obj type too -- for instantiation later
    AccountService accountService;
    MessageService messageService;

    // Experiment: create an instance of 'MessageService' class obj -- combine above & below into 1 step 
    // MessageService messageService = new MessageService();

    // no args class constructor
    public SocialMediaController(){
        // instantiate 'accountService' & 'messageService' variables by creating instances to each corresponding class
        accountService = new AccountService();
        messageService = new MessageService();
    }

    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        // always start w/ .create() method to configure Javalin
        // .start() to start listening for HTTP request methods at PORT 8080
        Javalin app = Javalin.create();         // .start(8080); ---- .start() already in Main.java
        // add POST request handler at /register endpoint 
        app.post("/register", this::createAccountHandler); 
        // app.post("/login", this::);
        app.post("/messages", this::createMessageHandler);

        app.get("/messages", this::getAllMessagesHandler);  // readme.md said to use 'post'
        app.get("/messages/{message_id}", this::getMessageByMsgIdHandler);
        app.delete("/messages/{message_id}", this::deleteMessageByMsgIdHandler);
        app.patch("/messages/{message_id}", this::updateMessageByMsgIdHandler);

        app.get("/accounts/{account_id}/messages", this::getMessagesByUserIdHandler);
        // start server at PORT 8080
        // app.start(8080); // yields "IllegalStateException" from trying to call start() on Javalin instance when already done
        return app;
    }

    /**
     * Handler to post a new flight.
     * The Jackson ObjectMapper will automatically convert the JSON of the POST request into a Flight object.
     * If flightService returns a null flight (meaning posting a flight was unsuccessful, the API will return a 400
     * message (client error). There is no need to change anything in this method.
     * @param ctx the context object handles information HTTP requests and generates responses within Javalin. It will
     *            be available to this method automatically thanks to the app.post method.
     * @throws JsonProcessingException will be thrown if there is an issue converting JSON into an object.
     */
    // ## 1: Our API should be able to process new User registrations.
    private void createAccountHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);
        Account addedAccount = accountService.addAccount(account);
        if(addedAccount == null){
            ctx.status(400);
        }else{
            ctx.json(mapper.writeValueAsString(addedAccount));
            ctx.status(200);
        }
    }

    // ## 2: Our API should be able to process User logins.
    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    // private void exampleHandler(Context context) {
    //     context.json("sample text");
    // }

    // ## 3: Our API should be able to process the creation of new messages.
    private void createMessageHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(ctx.body(), Message.class);
        Message createdMsg = messageService.createMessage(message);
        if(createdMsg == null){
            ctx.status(400);
        }else{
            ctx.json(mapper.writeValueAsString(createdMsg));
            ctx.status(200);
        }
    }

    // ## 4: Our API should be able to retrieve all messages.
    // app.get("/messages", this::getAllMessagesHandler);  // readme.md said to use 'post'
    private void getAllMessagesHandler(Context ctx){
        ctx.json(messageService.getAllMessages());
    }
    // ## 5: Our API should be able to retrieve a message by its ID.
    // app.get("/messages/{message_id}", this::getMessageByMsgIdHandler);
    private void getMessageByMsgIdHandler(Context ctx){
        // ObjectMapper mapper = new ObjectMapper();
        // Message message = mapper.readValue(ctx.body(), Message.class);
        // parse out "message_id" (String) from path URL parameter & convert into 'int' data type
        int msg_id = Integer.parseInt(ctx.pathParam("message_id"));
        Message getOneMsgByMsgId = messageService.getMessageByMsgId(msg_id);
        System.out.println(getOneMsgByMsgId); // -------
        // if message did NOT EXIST ... DN include response body
        if(getOneMsgByMsgId == null){
            // explicitly set response status to 200 --- even if there's NO message
            ctx.status(200);
        }
        // else if message did EXIST ... 
        else{
            // include response body w/ record of the READ row
            // ctx.json(mapper.writeValueAsString(deletedMsg));
            ctx.json(messageService.getMessageByMsgId(msg_id));
            // set response status to 200 as instructed
            ctx.status(200);
        }
    }
    // ## 6: Our API should be able to delete a message identified by a message ID.
    // app.delete("/messages/{message_id}", this::deleteMessageByMsgIdHandler);
    private void deleteMessageByMsgIdHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        // Message message = mapper.readValue(ctx.body(), Message.class);
        // parse out "message_id" (String) from path URL parameter & convert into 'int' data type
        int msg_id = Integer.parseInt(ctx.pathParam("message_id"));
        Message deletedMsg = messageService.deleteMessagebyMsgId(msg_id);
        System.out.println(deletedMsg); // -------
        // if message did NOT EXIST ... DN include response body
        if(deletedMsg == null){
            // explicitly set response status to 200 
            ctx.status(200);
        }
        // else if message did EXIST ... 
        else{
            // include response body w/ record of the DELETE row
            ctx.json(mapper.writeValueAsString(deletedMsg));
            // set response status to default succeed / OK -- 200
            ctx.status(200);
        }
    }
    // ## 7: Our API should be able to update a message text identified by a message ID.
    // app.patch("/messages/{message_id}", this::updateMessageByMsgIdHandler);
    private void updateMessageByMsgIdHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(ctx.body(), Message.class);
        int msg_id = Integer.parseInt(ctx.pathParam("message_id"));
        Message updatedMsg = messageService.updateMessagebyMsgId(msg_id, message);
        System.out.println(updatedMsg); // -------
        if(updatedMsg == null){
            ctx.status(400);
        }else{
            ctx.json(mapper.writeValueAsString(updatedMsg));
            ctx.status(200);
        }
    }
    // ## 8: Our API should be able to retrieve all messages written by a particular user.
    // app.get("/accounts/{account_id}/messages", this::getMessageByUserIdHandler)
    private void getMessagesByUserIdHandler(Context ctx){
        // ctx.json(messageService.getAllMessagesByUserId(ctx.pathParam("posted_by")));
        
        /* Referred to Week 6, Day 2 > "Handlers" > "Implementation" & Java .parseInt() look up */
        // parse query parameter {account_id} out from path URL String & type-cast to 'int' type
        int userId = Integer.parseInt(ctx.pathParam("account_id"));
        // after converting 'account_id' to int format -- use as params in .getAllMessagesByUserId() call to retrieve msgs by user
        ctx.json(messageService.getAllMessagesByUserId(userId));
        ctx.status(200);
        
        // UserDao dao = UserDao.instance();
        // Optional<User> user = dao.getUserById(id);

        // // Return the user object in the response
        // if (user.isPresent()) {
        //     ctx.json( user.get() );
        // } else {
        //     ctx.html("Not Found");
        // }
    }
}