package ms_312.CheckMeBackend.Messages.Retrievers;

import jakarta.persistence.Entity;
import ms_312.CheckMeBackend.Messages.Message;
import ms_312.CheckMeBackend.Resources.Crypto;
import ms_312.CheckMeBackend.Users.RetrieverOwner;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;


/**
 * A MessageRetriever which retrieves information from the Cmail Service within the Proxy Applications
 */
@Entity
public class CmailRetriever extends MessageRetriever{
    /**
     * The username of the account in the Crews Service to retrieve Messages from
     */
    private String username;

    /**
     * The password of the account in the Crews Service to retrieve Messages from.
     * Stored encrypted
     */
    private byte[] password;

    /**
     * Create a new ChaosRetriever for a specific User.
     *
     * @param source The URL of the endpoint to retrieve messages.
     * @param username The username of the account this Retriever retrieves Messages from
     * @param password The password to the account this Retriever retrieves Messages from
     * @param owner The {@link RetrieverOwner} object which owns this Retriever
     *
     */
    public CmailRetriever(String source, String username, String password, RetrieverOwner owner) {
        super(source, owner);
        this.username = username;

        //Encrypt the Password using AES and save it
        this.password = Crypto.encryptStringAES(password,Integer.toString(this.id));
    }


    /**
     * @inheritDoc
     */
    //Uncheck casts come from interacting with the JSON API
    @SuppressWarnings("unchecked")
    @Override
    public Message[] getAll() {
        //Decrypt the Password for use in the request
        String unencryptedPassword = Crypto.decryptStringAES(password, Integer.toString(this.id));

        // Put the username and password into
        String unencodedAuthString = username + ":" + unencryptedPassword;

        //Encode the auth string in Base64
        String authString = Base64.getEncoder().encodeToString(unencodedAuthString.getBytes());

        //Build the request to the Cmail API
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(source+"?sortBy=date")).setHeader("Authorization",authString).build();

        //Send the request and save the response
        HttpResponse<String> response;
        try{
            response = HTTPCLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        }
        catch (IOException | InterruptedException e){
            throw new RuntimeException("Could not make request to Chaos API. Root Cause: " + e);
        }

        // Parse the response as JSON
        JSONParser parser = new JSONParser(response.body());

        //Parse the request
        ArrayList<Object> responseBody;
        try{
            responseBody = parser.parseArray();
        }
        catch (ParseException e){
            throw new RuntimeException("Invalid response from Chaos API. Root Cause: " + e);
        }

        // Convert the returned JSOn into an array of message objects
        //Create the Array
        Message[] toReturn =  new Message[responseBody.size()];
        for(int i=0; i < toReturn.length; i++){
            // Cast the object parsed by the JSON to a linked hash map
            LinkedHashMap<Object, Object> JSONMap = (LinkedHashMap<Object, Object>) responseBody.get(i);

            toReturn[i] = new Message(JSONMap);
        }

        return toReturn;
    }

}
