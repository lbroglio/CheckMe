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
        System.out.println("User repository: " + userRepository);
        User user = ControllerUtils.getUsername(authorization, userRepository);
        System.out.println("Got user");
        String username = user.getName();
        System.out.println("Opening for user: " + username);
        //Separate the Base64 string from the rest of the authentication header
        authorization =  ControllerUtils.parseBasicAuthHeader(authorization);

        //Check if the User's authentication is correct
        boolean checkAuth =  ControllerUtils.checkBasicAuth(authorization, user, userRepository);

        if(!checkAuth){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or Password was incorrect");
        }

        sessionUsernameMap.put(session, username);
        usernameSessionMap.put(username, session);
        String message = "User:" + username + " has Joined the Chat";
//        broadcast(message);
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
//    public void onMessage(Session session, String message, )


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

