package ms_312.CheckMeBackend.Controllers;

import ms_312.CheckMeBackend.Messages.Retrievers.ChaosRetriever;
import ms_312.CheckMeBackend.Messages.Retrievers.CrewsRetriever;
import ms_312.CheckMeBackend.Messages.Retrievers.MessageRetriever;
import ms_312.CheckMeBackend.Users.User;
import ms_312.CheckMeBackend.Users.UserRepository;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;

@RestController
public class MessageController {
    @Autowired
    UserRepository userRepository;

    /**
     * Endpoint to add a new {@link MessageRetriever} to a specified User account.
     *
     * @param username Username for the CheckMe account to add the Retriever to.
     * @param authHeader The User's username and password using <a href="https://en.wikipedia.org/wiki/Basic_access_authentication">HTTP Basic Authentication</a>
     * Info should be sent in the form of Basic: {username}:{password}
     * @param body The body for the http request must contain the fields needed for adding the Retriever
     *
     * @return
     * 200 status - If the retriever was successfully added <br/>
     * 400 status - If the request body is missing a required field <br/>
     * 401 status - If the login given in the Authorization header is incorrect <br/>
     * 404 status - If the user to add the retriever to does not exists <br/>
     *
     * @throws NoSuchAlgorithmException This exception indicates an invalid algorithm name was given to a
     * {@link MessageDigest} object. The algorithm in this function is hard coded and this should NEVER occur.
     */
    //Unchecked Casts come from interacting with JSON API
    @SuppressWarnings("unchecked")
    @PutMapping("/user/{username}/connect-account")
    public ResponseEntity<String> setupServiceAccount(@PathVariable String username, @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader, @RequestBody String body) throws NoSuchAlgorithmException{
        // Get the user to add the retriever for
        User toAdd = userRepository.findByName(username);

        // Return 404 if the User to add is null
        if(toAdd == null){
            return new ResponseEntity<>("No User exists with name " + username, HttpStatus.NOT_FOUND);
        }

        // Confirm that the proper authentication was passed to make changes for the indicate user
        // Parse the encoded Base64 String from the Authorization Header and verify it's accurate for the given user
        if(!ControllerUtils.checkBasicAuth(ControllerUtils.parseBasicAuthHeader(authHeader),toAdd, userRepository)){
            return new ResponseEntity<>("Incorrect Username or Password.", HttpStatus.UNAUTHORIZED);
        }

        // Parse the request body as JSON
        JSONParser parser = new JSONParser(body);
        LinkedHashMap<String, String> bodyJSON;

        // If the body can't be parsed as JSON return 400
        try{
            bodyJSON = (LinkedHashMap<String, String>) parser.parse();
        }
        catch (ParseException e){
            return new ResponseEntity<>("Request Body could not be parsed as JSON", HttpStatus.BAD_REQUEST);
        }

        // Get the message service to create a retriever for
        String msgService = bodyJSON.get("message-service");

        // If the service is missing return 400
        if(msgService == null){
            return new ResponseEntity<>("Body is missing required field: message-service", HttpStatus.BAD_REQUEST);

        }

        // Get the URL endpoint to Retrieve Messages from the request body
        String apiEndpoint = bodyJSON.get("service-url");

        // Return 400 if the endpoint is missing
        if(apiEndpoint == null){
            return new ResponseEntity<>("Body is missing required field: service-url", HttpStatus.BAD_REQUEST);
        }


        // Create the new Retriever depending on what service was indicated

        // Chaos service
        if(msgService.equalsIgnoreCase("chaos")){
            // Get the API token for the Chaos service from request body
            String chaosToken = bodyJSON.get("chaos-token");

            // Return 400 if the API token is missing
            if(chaosToken == null){
                return new ResponseEntity<>("Body is missing required field: chaos-token", HttpStatus.BAD_REQUEST);
            }

            // Add the new retriever to the User
            toAdd.newMessageSource(new ChaosRetriever(apiEndpoint, chaosToken, toAdd));

        }
        // Crews Service
        else if(msgService.equalsIgnoreCase("crews")){
            // Get the username and password used for the crews account from the request body
            String crewsUsername = bodyJSON.get("crews-username");
            String crewsPassword = bodyJSON.get("crews-password");

            // If the username or password if missing return 400
            if(crewsUsername == null){
                return new ResponseEntity<>("Body is missing required field: crews-username", HttpStatus.BAD_REQUEST);

            }
            else if(crewsPassword == null){
                return new ResponseEntity<>("Body is missing required field: crews-password", HttpStatus.BAD_REQUEST);

            }

            // Add the new retriever to the User
            toAdd.newMessageSource(new CrewsRetriever(apiEndpoint, crewsUsername, crewsPassword, toAdd));

        }
        // Cmail service
        else if(msgService.equalsIgnoreCase("cmail")){
            // Get the username and password used for the Cmail account from the request body
            String cmailUsername = bodyJSON.get("cmail-username");
            String cmailPassword = bodyJSON.get("cmail-password");

            // If the username or password if missing return 400
            if(cmailUsername == null){
                return new ResponseEntity<>("Body is missing required field: cmail-username", HttpStatus.BAD_REQUEST);

            }
            else if(cmailPassword == null){
                return new ResponseEntity<>("Body is missing required field: cmail-password", HttpStatus.BAD_REQUEST);

            }

            // Add the new retriever to the User
            toAdd.newMessageSource(new CrewsRetriever(apiEndpoint, cmailUsername, cmailPassword, toAdd));
        }
        // Invalid service name
        else{
            return new ResponseEntity<>("Invalid message service must included in the following list{chaos, crews, cmail}", HttpStatus.BAD_REQUEST);

        }

        //Save the user
        userRepository.save(toAdd);

        // Return 200 to indicate success
        return new ResponseEntity<>("Retriever Added", HttpStatus.OK);
    }

}
