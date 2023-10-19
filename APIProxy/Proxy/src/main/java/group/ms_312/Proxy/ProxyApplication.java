package group.ms_312.Proxy;

import group.ms_312.Proxy.Messages.Message;
import group.ms_312.Proxy.Providers.MessageProvider;
import group.ms_312.Proxy.Providers.MessageProviderRepository;
import group.ms_312.Proxy.Providers.TokenBasedProvider;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.json.HTTP;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;

@SpringBootApplication
@RestController
public class ProxyApplication {
	/**
	 * Hardcoded long id for the token based provider
	 */
	private static final long TOKENBASED_ID = 0x4368616F730AL;

	@Autowired
	MessageProviderRepository providerRepository;


	/**
	 * Add a Message to be stored by the token based provider
	 *
	 * @param messageJSON The JSON object of the Message to store
	 * @param username The String username to store the Message associated with
	 */
	private void loadTokenBasedMessage(LinkedHashMap<Object, Object> messageJSON, String username){
		// Get the token based provider
		MessageProvider tokenBasedProvider = providerRepository.findByID(TOKENBASED_ID);

		//If the token based provided doesn't exist create one
		if(tokenBasedProvider == null){
			tokenBasedProvider = new TokenBasedProvider();
		}

		//Add the message
		tokenBasedProvider.loadMessage(new Message(messageJSON), username);

		//Save the provider
		providerRepository.save(tokenBasedProvider);
	}


	public static void main(String[] args) {
		SpringApplication.run(ProxyApplication.class, args);
	}


	/**
	 * Take in a JSON Array of Messages to load into the different Providers
	 *
	 * @param toLoad The JSON array of Messages (and associated information) to add into the providers
	 *
	 * @return
	 * 201 - If the messages in the body are successfully loaded
	 * 400 - If the JSON cannot be parsed or important fields are missing
	 */
	//Unchecked Casts come from interactions with JSON API
	@SuppressWarnings("unchecked")
	@PostMapping("/messages/load")
	public ResponseEntity<String> loadMessages(@RequestBody String toLoad){
		//Parse the request body as JSON
		JSONParser parser = new JSONParser(toLoad);
		ArrayList<Object> messagesToLoad;
		try{
			messagesToLoad = parser.parseArray();
		}
		catch (ParseException e){
			//Return 400 is the body cannot be parsed
			return new ResponseEntity<>("Could not parse request body as JSON array", HttpStatus.BAD_REQUEST);
		}

		// Go through every object in the Array
		for (Object o : messagesToLoad) {
			// Get the current object and cast it to a LinkedHashMap
			LinkedHashMap<Object, Object> currObj = (LinkedHashMap<Object, Object>) o;

			//Get the username, the message objects, and the Provider name (String used to indicate which provider this should be added to)
			 // Provider name = TOKEN |
			String username = (String) currObj.get("username");
			String providerName = (String) currObj.get("service");
			LinkedHashMap<Object, Object> messageObj = (LinkedHashMap<Object, Object>) currObj.get("message");

			//Return 400 if any of the needed fields are missing
			if(username == null ){
				return new ResponseEntity<>("Request body was missing required field: username", HttpStatus.BAD_REQUEST);
			}
			else if(providerName == null){
				return new ResponseEntity<>("Request body was missing required field: service", HttpStatus.BAD_REQUEST);
			}
			else if(messageObj == null){
				return new ResponseEntity<>("Request body was missing required field: message", HttpStatus.BAD_REQUEST);
			}


			// Add to proper provider based on given provider name
			switch (providerName.toLowerCase()){
				case "chaos":
					loadTokenBasedMessage(messageObj, username);
					break;
				case "crews":
					break;
			}

		}

		return new ResponseEntity<>("Data successfully loaded", HttpStatus.CREATED);
	}

	@GetMapping("/test")
	public Message[] testTokenBased(){
		MessageProvider tokenBased = providerRepository.findByID(TOKENBASED_ID);
		return tokenBased.getAllMessagesForUser("BaseballBob");
	}


}
