package group.ms_312.Proxy;

import group.ms_312.Proxy.Messages.Message;
import group.ms_312.Proxy.Providers.*;
import jakarta.annotation.PostConstruct;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;

@SpringBootApplication
@RestController
public class ProxyApplication {
	/**
	 * Hardcoded long id for the token based provider
	 */
	private static final long TOKENBASED_ID = 0x4368616F730AL;

	/**
	 * Hardcoded long id for the custom header provider
	 */
	private static final long CUSTOMHEADER_ID = 0x4372657773L;
	/**
	 * Hardcoded long id for the basic auth provider
	 */
	private static final long BASICAUTH_ID = 0x434D61696CL;


	@Autowired
	MessageProviderRepository providerRepository;

	/**
	 * If the providers haven't already been created; create them
	 */
	@PostConstruct
	private void createProviders(){
		//Try to get the TokenBasedProvider
		MessageProvider tokenBasedProvider = providerRepository.findByID(TOKENBASED_ID);

		// If the token based provider does not exist create it and save it to the database
		if(tokenBasedProvider == null){
			tokenBasedProvider = new TokenBasedProvider();
			providerRepository.save(tokenBasedProvider);
		}
		//Try to get the custom Header Provider
		MessageProvider customHeaderProvider = providerRepository.findByID(CUSTOMHEADER_ID);
		// If the custom header provider does not exist create it and save it to the database
		if(customHeaderProvider == null){
			customHeaderProvider = new BasicAuthProvider(CUSTOMHEADER_ID);
			providerRepository.save(customHeaderProvider);
		}
		//Try to get the Basic Auth Provider
		MessageProvider basicAuthProvider = providerRepository.findByID(BASICAUTH_ID);
		// If the custom header provider does not exist create it and save it to the database
		if(basicAuthProvider == null){
			basicAuthProvider = new BasicAuthProvider(BASICAUTH_ID);
			providerRepository.save(basicAuthProvider);
		}

	}

	/**
	 * Take in a string passed in the header of a request using <a href="https://en.wikipedia.org/wiki/Basic_access_authentication">HTTP Basic Authentification</a>
	 * and separate the base64 encoding from the Basic keyword
	 * @param authHeader The string passed in an HTTP request's authorization header containing Basic Auth to be parsed
	 *
	 * @return A string containing only the Base64 from the header not the Basic Keyword
	 */
	private static String parseBasicAuthHeader(String authHeader){
		String[] splitHeader = authHeader.split(" ");
		return splitHeader[1];
	}


	/**
	 * Add a Message to be stored by the token based provider
	 *
	 * @param messageJSON The JSON object of the Message to store
	 * @param username The String username to store the Message associated with
	 */
	private void loadTokenBasedMessage(LinkedHashMap<Object, Object> messageJSON, String username){
		// Get the token based provider
		MessageProvider tokenBasedProvider = providerRepository.findByID(TOKENBASED_ID);

		//Add the message
		tokenBasedProvider.loadMessage(new Message(messageJSON), username);

		//Save the provider
		providerRepository.save(tokenBasedProvider);
	}

	/**
	 * Add a Message to be stored by the BasicAuth provider
	 *
	 * @param messageJSON The JSON object of the Message to store
	 * @param username The String username to store the Message associated with
	 */
	private void loadBasicAuthMessage(LinkedHashMap<Object, Object> messageJSON, String username){
		// Get the token based provider
		MessageProvider provider = providerRepository.findByID(BASICAUTH_ID);

		//Add the message
		provider.loadMessage(new Message(messageJSON), username);

		//Save the provider
		providerRepository.save(provider);
	}

	/**
	 * Add a Message to be stored by the CustomHeader provider
	 *
	 * @param messageJSON The JSON object of the Message to store
	 * @param username The String username to store the Message associated with
	 */
	private void loadCustomHeaderMessage(LinkedHashMap<Object, Object> messageJSON, String username){
		// Get the token based provider
		MessageProvider provider = providerRepository.findByID(CUSTOMHEADER_ID);

		//Add the message
		provider.loadMessage(new Message(messageJSON), username);

		//Save the provider
		providerRepository.save(provider);
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
			switch (providerName.toLowerCase()) {
				case "chaos" -> loadTokenBasedMessage(messageObj, username);
				case "crews" -> loadCustomHeaderMessage(messageObj, username);
				case "cmail" -> loadBasicAuthMessage(messageObj, username);
			}

		}

		return new ResponseEntity<>("Data successfully loaded", HttpStatus.CREATED);
	}

	/**
	 * Retrieve the Messages for a specific User from the Token Based Provider ("Chaos Service"). The messages will
	 * be sorted in the order indicated by the sortBy parameter
	 *
	 * @param bearerToken The integer token issued to the User for retrieving Messages
	 * @param sortBy Either the string "date" or "sender" depending on how the messages should be ordered.
	 * If neither in included then the messages will be returned unordered
	 *
	 * @return An Array of {@link Message} objects for the messages to retrieve for the user
	 */
	@GetMapping("/chaos/messages/{bearerToken}")
	public Message[] getMessagesFromTokenBasedProvider(@PathVariable String bearerToken, @RequestParam(required = false) String sortBy){
		// Get the token based provider and downcast it
		TokenBasedProvider tokenBased = (TokenBasedProvider) providerRepository.findByID(TOKENBASED_ID);

		// Check if the given token exists within the provider
		if(!tokenBased.tokenExists(bearerToken)){
			// If the token doesn't exist return 404
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Given token does not exist");

		}

		//If sortBy isn't included return the Messages unordered
		if(sortBy == null){
			return tokenBased.getAllMessagesForUser(tokenBased.getUsernameFromToken(bearerToken), bearerToken);
		}
		// Return the messages in the indicated order
		if(sortBy.equals("date")){
			return tokenBased.getAllMessagesForUser(tokenBased.getUsernameFromToken(bearerToken), bearerToken, MessageOrdering.DATE);
		}
		else if(sortBy.equals("sender")){
			return tokenBased.getAllMessagesForUser(tokenBased.getUsernameFromToken(bearerToken), bearerToken, MessageOrdering.SENDER);
		}
		else{
			return tokenBased.getAllMessagesForUser(tokenBased.getUsernameFromToken(bearerToken), bearerToken);
		}

	}

	/**
	 * !! This endpoint only exists because this is a development tool -- It would not and should not exist in a
	 * production API !!
	 *<br/><br/>
	 * Retrieve the Bearer token associated with a given username
	 *
	 * @param username The username to retrieve the token for as a String
	 *
	 * @return The int Bearer token associated with the username
	 */
	@GetMapping("/chaos/tokens/{username}")
	public String getTokenForUser(@PathVariable String username){
		// Get the token based provider and downcast it
		TokenBasedProvider tokenBased = (TokenBasedProvider) providerRepository.findByID(TOKENBASED_ID);

		//Check if the User exists
		if(!tokenBased.userExists(username)){
			// If the user doesn't exist respond 404
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No user exists with username: " + username);
		}

		// Return the Bearer token associated for the given username
		return tokenBased.getTokenForUser(username);
	}

	/**
	 * Add a new User to the TokenBasedProvider -- equivalent to creating  an account and provisioning an API/bearer
	 * token in an actual service
	 *
	 * @param body The body of the post request -- should include a field "username" containing the username to add
	 * as a user
	 *
	 * @return The bearer token generated  for the newly added user
	 */
	// Unchecked casts are from interacting with JSON API
	@SuppressWarnings("unchecked")
	@PostMapping("/chaos/user")
	public String addUserTokenBased(@RequestBody String body){
		// Retrieve the username from  the request body
		JSONParser parser = new JSONParser(body);
		LinkedHashMap<Object,  Object> requestBody;
		// Attempt to parse the request body as JSON and respond 400 if it cannot be parsed
		try{
			requestBody = (LinkedHashMap<Object, Object>) parser.parse();
		} catch (ParseException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Could not parse request body as JSON");
		}

		// Read the username from the request body
		String username = (String) requestBody.get("username");

		//Respond 400 if the username wasn't included
		if( username == null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Missing required field username");
		}

		// Get the token based provider
		MessageProvider tokenBased =  providerRepository.findByID(TOKENBASED_ID);

		//Return 407 if the user to add already exists
		if( tokenBased.userExists(username)){
			throw new ResponseStatusException(HttpStatus.CONFLICT,"User " + username + " already exists.");
		}

		//Add the new user and get the generated token
		//Create the map containing the user account info (For this provider type it is only a username)
		HashMap<String, String> accountInfo = new HashMap<>();
		accountInfo.put("username", username);
		String token = tokenBased.addUser(accountInfo);

		// Save the provider
		providerRepository.save(tokenBased);

		// Return the token
		return token;
	}

	/**
	 * Retrieve the Messages for a specific User from the Custom Header Provider ("Crews Service"). The messages will
	 * be sorted in the order indicated by the sortBy parameter
	 *
	 * @param authString The username and password of the user encoded in the format {username:password} in Base64 in
	 * this is the same format as <a href="https://en.wikipedia.org/wiki/Basic_access_authentication">HTTP Basic Authentication</a>
	 * This should be passed in the HTTP Header under the field 'X-Crews-API'
	 * @param sortBy Either the string "date" or "sender" depending on how the messages should be ordered.
	 * If neither in included then the messages will be returned unordered
	 *
	 * @return An Array of {@link Message} objects for the messages to retrieve for the user
	 */
	@GetMapping("/crews/messages")
	public Message[] getMessagesFromCustomHeaderProvider(@RequestHeader("X-Crews-API") String authString, @RequestParam(required = false) String sortBy){
		// Get the custom header provider and downcast it
		BasicAuthProvider provider = (BasicAuthProvider) providerRepository.findByID(CUSTOMHEADER_ID);

		// Get the username from the authString
		//Decode the string
		String decodedAuth = new String(Base64.getDecoder().decode(authString));
		//Find the username
		int splitIndex = decodedAuth.lastIndexOf(':');
		//Get the substring containing the username
		String username = decodedAuth.substring(0,splitIndex);

		// Check if the given user exists within the provider
		if(!provider.userExists(username)){
			// If the token doesn't exist return 404
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No user exists with the given username");

		}

		//If sortBy isn't included return the Messages unordered
		if(sortBy == null){
			return provider.getAllMessagesForUser(username, authString);
		}
		// Return the messages in the indicated order
		else if(sortBy.equals("date")){
			return provider.getAllMessagesForUser(username, authString, MessageOrdering.DATE);
		}
		else if(sortBy.equals("sender")){
			return provider.getAllMessagesForUser(username, authString, MessageOrdering.SENDER);
		}
		else{
			return provider.getAllMessagesForUser(username, authString);
		}

	}

	/**
	 * Add a new User to the Cusotm Header Provider -- equivalent to creating an account for a user
	 *
	 * @param body The body of the post request -- should include a field "username" containing the username to add
	 * as a user and a field 'password' containing the password of that user
	 *
	 */
	// Unchecked casts are from interacting with JSON API
	@SuppressWarnings("unchecked")
	@PostMapping("/crews/user")
	public void addUserCustomHeader(@RequestBody String body){
		// Retrieve the username from  the request body
		JSONParser parser = new JSONParser(body);
		LinkedHashMap<Object,  Object> requestBody;
		// Attempt to parse the request body as JSON and respond 400 if it cannot be parsed
		try{
			requestBody = (LinkedHashMap<Object, Object>) parser.parse();
		} catch (ParseException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Could not parse request body as JSON");
		}

		// Read the username from the request body
		String username = (String) requestBody.get("username");

		//Respond 400 if the username wasn't included
		if( username == null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Missing required field username");
		}

		// Read the username from the request body
		String password = (String) requestBody.get("password");

		//Respond 400 if the username wasn't included
		if( password == null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Missing required field password");
		}


		// Get the custom header provider
		MessageProvider provider =  providerRepository.findByID(CUSTOMHEADER_ID);

		//Return 407 if the user to add already exists
		if( provider.userExists(username)){
			throw new ResponseStatusException(HttpStatus.CONFLICT,"User " + username + " already exists.");
		}

		//Add the new user and get the generated token
		//Create the map containing the user account info (For this provider type it is a username and a password)
		HashMap<String, String> accountInfo = new HashMap<>();
		accountInfo.put("username", username);
		accountInfo.put("password", password);
		provider.addUser(accountInfo);

		// Save the provider
		providerRepository.save(provider);

	}

	/**
	 * Retrieve the Messages for a specific User from the Basic Auth Provider ("CMail Service"). The messages will
	 * be sorted in the order indicated by the sortBy parameter
	 *
	 * @param authString The username and password of the user encoded in the format {username:password} in Base64 in
	 * this is the same format as <a href="https://en.wikipedia.org/wiki/Basic_access_authentication">HTTP Basic Authentication</a>
	 * This should be passed in the HTTP Header under the Authorization Header
	 * @param sortBy Either the string "date" or "sender" depending on how the messages should be ordered.
	 * If neither in included then the messages will be returned unordered
	 *
	 * @return An Array of {@link Message} objects for the messages to retrieve for the user
	 */
	@GetMapping("/cmail/messages")
	public Message[] getMessagesFromBasicAuthProvider(@RequestHeader(HttpHeaders.AUTHORIZATION) String authString, @RequestParam(required = false) String sortBy){
		// Get the custom header provider and downcast it
		BasicAuthProvider provider = (BasicAuthProvider) providerRepository.findByID(BASICAUTH_ID);

		// Get the username from the authString
		//Decode the string
		String basicAuthStr = parseBasicAuthHeader(authString);
		String decodedAuth = new String(Base64.getDecoder().decode(basicAuthStr));

		//Find the username
		int splitIndex = decodedAuth.lastIndexOf(':');
		//Get the substring containing the username
		String username = decodedAuth.substring(0,splitIndex);

		// Check if the given user exists within the provider
		if(!provider.userExists(username)){
			// If the token doesn't exist return 404
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No user exists with the given username");

		}

		//If sortBy isn't included return the Messages unordered
		if(sortBy == null){
			return provider.getAllMessagesForUser(username, basicAuthStr);
		}
		// Return the messages in the indicated order
		else if(sortBy.equals("date")){
			return provider.getAllMessagesForUser(username, basicAuthStr, MessageOrdering.DATE);
		}
		else if(sortBy.equals("sender")){
			return provider.getAllMessagesForUser(username, basicAuthStr, MessageOrdering.SENDER);
		}
		else{
			return provider.getAllMessagesForUser(username, basicAuthStr);
		}

	}

	/**
	 * Add a new User to the BasicAuthProvider -- equivalent to creating an account for a user
	 *
	 * @param body The body of the post request -- should include a field "username" containing the username to add
	 * as a user and a field 'password' containing the password of that user
	 *
	 */
	// Unchecked casts are from interacting with JSON API
	@SuppressWarnings("unchecked")
	@PostMapping("/cmail/user")
	public void addUserBasicAuth(@RequestBody String body){
		// Retrieve the username from  the request body
		JSONParser parser = new JSONParser(body);
		LinkedHashMap<Object,  Object> requestBody;
		// Attempt to parse the request body as JSON and respond 400 if it cannot be parsed
		try{
			requestBody = (LinkedHashMap<Object, Object>) parser.parse();
		} catch (ParseException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Could not parse request body as JSON");
		}

		// Read the username from the request body
		String username = (String) requestBody.get("username");

		//Respond 400 if the username wasn't included
		if( username == null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Missing required field username");
		}

		// Read the username from the request body
		String password = (String) requestBody.get("password");

		//Respond 400 if the username wasn't included
		if( password == null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Missing required field password");
		}


		// Get the custom header provider
		MessageProvider provider =  providerRepository.findByID(BASICAUTH_ID);

		//Return 407 if the user to add already exists
		if( provider.userExists(username)){
			throw new ResponseStatusException(HttpStatus.CONFLICT,"User " + username + " already exists.");
		}

		//Add the new user and get the generated token
		//Create the map containing the user account info (For this provider type it is a username and a password)
		HashMap<String, String> accountInfo = new HashMap<>();
		accountInfo.put("username", username);
		accountInfo.put("password", password);
		provider.addUser(accountInfo);

		// Save the provider
		providerRepository.save(provider);

	}



}
