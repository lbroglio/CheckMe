package ms_312.CheckMeBackend.Messages.Retrievers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
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
import java.util.LinkedHashMap;


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

            toReturn[i] = new Message(JSONMap);
        }

        return toReturn;
    }

}
