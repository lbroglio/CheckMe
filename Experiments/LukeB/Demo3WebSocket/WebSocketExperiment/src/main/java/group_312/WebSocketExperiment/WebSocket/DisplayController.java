package group_312.WebSocketExperiment.WebSocket;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

@ServerEndpoint("/display/{referenceName}")
@Component
public class DisplayController {

    // Store all socket session and their corresponding reference names
    // Two maps for the ease of retrieval by key
    private static Map<Session, String > sessionReferenceMap = new Hashtable< >();
    private static Map < String, Session > referenceSessionMap = new Hashtable < > ();

    // server side logger
    private final Logger logger = LoggerFactory.getLogger(DisplayController.class);



    @OnOpen
    public void onOpen(Session session, @PathParam("referenceName") String referenceName) throws IOException {

        // server side log
        logger.info("[onOpen] " + referenceName);

        // Handle the case of a duplicate username
        if (referenceSessionMap.containsKey(referenceName)) {
            session.getBasicRemote().sendText("Username already exists");
            session.close();
        }
        else {
            // map current session with username
            sessionReferenceMap.put(session, referenceName);

            // map current username with session
            referenceSessionMap.put(referenceName, session);
        }
    }

    @OnClose
    public void onClose(Session session) throws IOException {

        // get the username from session-username mapping
        String referenceName = sessionReferenceMap.get(session);

        // server side log
        logger.info("[onClose] " + referenceName);

        // remove user from memory mappings
        sessionReferenceMap.remove(session);
        referenceSessionMap.remove(referenceName);

    }


    @OnMessage
    public void onMessage(Session session, String request) throws IOException {

        // get the username by session
        String referenceName = sessionReferenceMap.get(session);

        // server side log
        logger.info("[onMessage] request from " + referenceName);

        // Randomly Generate new location and color change
        Random rand = new Random();
        int newX = rand.nextInt(0, 100);
        int newY = rand.nextInt(0, 100);
        int newR = rand.nextInt(0, 256);
        int newG = rand.nextInt(0, 256);
        int newB = rand.nextInt(0, 256);

        // Save the color vals as hex
        String sendR = Integer.toHexString(newR);
        String sendG = Integer.toHexString(newG);
        String sendB = Integer.toHexString(newB);


        // Build msg to send from the values
        String toSend = "[" + newX + "," + newY + "," + "\"#"+ sendR + sendG + sendB + "\"]";

        // Send the newly generated information
        sendToParticularSession(referenceName, toSend);
    }


    private void sendToParticularSession(String referenceName, String message) {
        try {
            referenceSessionMap.get(referenceName).getBasicRemote().sendText(message);
        } catch (IOException e) {
            logger.info("[DM Exception] " + e.getMessage());
        }
    }



}
