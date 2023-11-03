package ms_312.CheckMeBackend.LiveChat;

import jakarta.annotation.PostConstruct;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import ms_312.CheckMeBackend.Controllers.ControllerUtils;
import ms_312.CheckMeBackend.Users.Group;
import ms_312.CheckMeBackend.Users.GroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ms_312.CheckMeBackend.Users.User;
import ms_312.CheckMeBackend.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.NoSuchAlgorithmException;
import java.util.*;

//@ComponentScan(basePackages = {"ms_312.CheckMeBackend"})
@ServerEndpoint("/livechat/{auth}/{group}")
@Component
public class ChatRoomController {


    private final Logger logger = LoggerFactory.getLogger(ChatRoomController.class);

    private static Map < Session, String > sessionUsernameMap = new Hashtable < > ();
    private static Map < String, Session > usernameSessionMap = new Hashtable < > ();

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    /**
     *
     */


    /**
     *
     * @param session
     * @param authorization
     * @throws NoSuchAlgorithmException
     * @throws ResponseStatusException
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("auth") String authorization, @PathParam("group") String group) throws NoSuchAlgorithmException, ResponseStatusException {
        System.out.println("Entered into open");

        // Get the username from the auth string
        String decodedAuth = new String(Base64.getDecoder().decode(authorization));
        int authSplit = decodedAuth.lastIndexOf(':');
        String username = decodedAuth.substring(0, authSplit);
        // Get the password from the auth string
        String password = decodedAuth.substring(authSplit +1);

        System.out.println("Got user");
        System.out.println("Opening for user: " + username);

        // Request to the Login endpoint
        // Body for the request
        String body = "{\n\"username\": \"" + username + "\",\n\"password\": \"" + password + "\"\n}";

        //Build a request to the login endpoint
        HttpClient HTTPCLIENT = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(body)).uri(URI.create("http://coms-309-047.class.las.iastate.edu:8080/user/login")).build();

        //Send the request and save the response
        HttpResponse<String> response;
        try{
            response = HTTPCLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        }
        catch (IOException | InterruptedException e){
            throw new RuntimeException("Could not make request to Chaos API. Root Cause: " + e);
        }

        // Parse the response as a boolean
        boolean checkAuth = Boolean.parseBoolean(response.body());

        if(!checkAuth){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or Password was incorrect");
        }

        sessionUsernameMap.put(session, username);
        usernameSessionMap.put(username, session);
        String message = "User:" + username + " has Joined the Chat";
        broadcast(message);
        System.out.println("Opened");
    }


    /**
     *
     * @param session
     * @throws IOException
     */
    @OnClose
    public void onClose(Session session) throws IOException {
        logger.info("Entered into Close");

        String username = sessionUsernameMap.get(session);

        sessionUsernameMap.remove(session);
        usernameSessionMap.remove(username);

        String message = username + " disconnected";
        broadcast(message);
    }

    @OnMessage
    public void onMessage(Session session, String message, @PathParam("username") String username){

    }


    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
        logger.info("Entered into Error");
    }

    private void broadcast(String message) {
        sessionUsernameMap.forEach((session, username) -> {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                logger.info("Exception: " + e.getMessage().toString());
                e.printStackTrace();
            }

        });

    }

}

