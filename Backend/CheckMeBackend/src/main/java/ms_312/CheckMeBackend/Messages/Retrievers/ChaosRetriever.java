package ms_312.CheckMeBackend.Messages.Retrievers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import ms_312.CheckMeBackend.Messages.Message;
import ms_312.CheckMeBackend.Resources.Crypto;
import ms_312.CheckMeBackend.Users.RetrieverOwner;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


/**
 * A MessageRetriever which retrieves information from the Chaos Service within the Proxy Applications
 */
@Entity
public class ChaosRetriever extends MessageRetriever{
    /**
     * The API token used by this retriever to get Messages from the Chaos service
     */
    private byte[] APIToken;

    /**
     * Store the reference name used for retrieving the cipher keys for this Retriever. <br/>
     * Found by combining the username of the owner with the current number of Retrievers owned by the user
     */
    private String cipherReferenceName;

    /**
     * Create a new ChaosRetriever for a specific User.
     *
     * @param source The URL of the endpoint to retrieve messages.
     * @param chaosAPIToken The API token needed to authenticate with the Chaos service
     * @param owner The {@link RetrieverOwner} object which owns this Retriever
     *
     */
    public ChaosRetriever(String source, String chaosAPIToken, RetrieverOwner owner) {
        super(source, owner);

        // Set the reference name for used to store the cipher key (and iv) for this retriever
        cipherReferenceName = owner.getName() + "_" + owner.getMessageRetrievers().size();

        //Encrypt the BearerToken using AES and save it
        this.APIToken = Crypto.encryptStringAES(chaosAPIToken, cipherReferenceName);
    }

    /**
     * Default Constructor used by the JPA
     */
    private ChaosRetriever(){
        super();
    }


    /**
     * @inheritDoc
     */
    //Uncheck casts come from interacting with the JSON API
    @SuppressWarnings("unchecked")
    @Override
    public Message[] getAll() {
        //Decrypt the API token for use in the request
        String bearerToken = Crypto.decryptStringAES(APIToken, cipherReferenceName);
        String authString = "Bearer " + bearerToken;

        //Build the request to the Chaos API
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

            toReturn[i] = new Message(JSONMap, this.id);
        }

        return toReturn;
    }

    @Override
    public boolean replyTo(String replyContents, Message msgReplyTo){
        //Decrypt the API token for use in the request
        String bearerToken = Crypto.decryptStringAES(APIToken, cipherReferenceName);
        String authString = "Bearer " + bearerToken;

        // Use the source to build the reply link
        ArrayList<String> splitSrc = new ArrayList<>(List.of(source.split("/")));
        int cutOffIndex = splitSrc.indexOf("messages");
        String chaosActName = splitSrc.get(cutOffIndex + 1);
        int strEndIndex = source.indexOf("messages");
        String replyURL = source.substring(0, strEndIndex) + chaosActName + "/reply";

        // Build the reply body
        LinkedHashMap<Object, Object> msgMap = new LinkedHashMap<>();
        msgMap.put("sender", msgReplyTo.getSender());
        msgMap.put("recipient", chaosActName);
        if(msgReplyTo.getSubject() != null){
            msgMap.put("subject", msgReplyTo.getSubject());
        }
        else{
            msgMap.put("subject", "None");
        }
        msgMap.put("contents", msgReplyTo.getContents());
        msgMap.put("sendTime", msgReplyTo.getSendTime().toString());


        JSONObject requestBody = new JSONObject();
        requestBody.put("content", replyContents);
        requestBody.put("reply-to", msgMap);

        //Build the request to the Chaos API
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(requestBody.toString())).uri(URI.create(replyURL)).setHeader("Authorization",authString).build();

        //Send the request and save the response
        HttpResponse<String> response;
        try{
            response = HTTPCLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        }
        catch (IOException | InterruptedException e){
            throw new RuntimeException("Could not make request to Chaos API. Root Cause: " + e);
        }

       // Check if the response is as expected
        // Return true on success
        // Return false on failure
        return response.body().equals("Reply sent: " + replyContents);
    }


}
