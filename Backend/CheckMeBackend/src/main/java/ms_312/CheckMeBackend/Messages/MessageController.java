package ms_312.CheckMeBackend.Messages;


import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;

@SpringBootApplication
@RestController
@ComponentScan(basePackages = {"ms_312.CheckMeBackend"})
public class MessageController {
    @Autowired
    MessageRepository messageRepository;


    @PostMapping("/message")
    public ResponseEntity<String> createMessage(@RequestBody String messageInfo) throws NoSuchAlgorithmException {
        // Parse the JSON body of the post request
        JSONParser parseBody = new JSONParser(messageInfo);

        LinkedHashMap<Object, Object> messageJSON;

        // Catch the exception if the JSON can not be parsed and return a bad request response
        try{
            // Parse the JSON to a LinkedHashMap -- this is an unchecked cast but is necessary because of the JSON API
            messageJSON = (LinkedHashMap<Object, Object>) parseBody.parse();
        }
        catch(ParseException e){
            return new ResponseEntity<>("JSON in request body could not be parsed.", HttpStatus.BAD_REQUEST);
        }

        String sender = (String) messageJSON.get("sender");
        String recipient = (String) messageJSON.get("recipient");
        String contents = (String) messageJSON.get("contents");
        String subject = (String) messageJSON.get("subject");
//		String platform = (String) messageJSON.get("platform");
//		LocalDateTime sendTime = LocalDateTime.parse((String) messageJSON.get("sendTime"));


        Message createdMessage = new Message(sender, recipient, contents, subject);
        messageRepository.save(createdMessage);

        return new ResponseEntity<>("Saved message: " + createdMessage.getID(),HttpStatus.OK );
    }

    @GetMapping("/message/id/{id}")
    public String seeMessage(@PathVariable int id){
        Message requested = messageRepository.findByID(id);

        // Return 404 if the requested user doesn't exist
        if(requested == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"There is no message with the given ID");

        }

        return requested.toString();

    }

    @GetMapping("/message/user/{user}")
    public String userMessages(@PathVariable String user){
        Message requested = messageRepository.findByRecipient(user);

        // Return 404 if the requested user doesn't exist
        if(requested == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"There is no message with the given ID");

        }

        return requested.toString();

    }


}
