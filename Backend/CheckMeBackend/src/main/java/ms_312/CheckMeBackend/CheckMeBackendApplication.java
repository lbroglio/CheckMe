package ms_312.CheckMeBackend;


import jakarta.annotation.PostConstruct;
import ms_312.CheckMeBackend.Users.Group;
import ms_312.CheckMeBackend.Users.GroupRepository;

import ms_312.CheckMeBackend.Messages.Message;
import ms_312.CheckMeBackend.Messages.MessageRepository;

import ms_312.CheckMeBackend.Users.User;
import ms_312.CheckMeBackend.Users.UserRepository;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import java.util.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;


@SpringBootApplication
@RestController
public class CheckMeBackendApplication {
	@Autowired
	UserRepository userRepository;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	MessageRepository messageRepository;

	@PostConstruct
	private void rebuildStatics(){
		List<Group> allGroups = groupRepository.findAll();

		for (Group allGroup : allGroups) {
			allGroup.fillCodeList();
		}

	}



	/**
	 * Hashes a string and compares it to the saved hash belonging to a given {@link User}
	 *
	 * @param user The user to see if the given password is correct for.
	 * @param givenPassword The password to attempt to match with the user
	 * @return true if the password is correct false if it is not
	 *
	 * @throws NoSuchAlgorithmException This exception indicates an invalid algorithm name was given to a
	 * 	 * {@link MessageDigest} object. The algorithm in this function is hard coded and this should NEVER occur.
	 */
	private static Boolean checkPassword(User user, String givenPassword) throws NoSuchAlgorithmException {

		//Get the salt for this hash
		byte[] salt = user.getSalt();

		//Convert the saved hash to bytes for comparison
		byte[] savedHash = user.getPasswordHash();

		// Get a MessageDigest object for SHA-512
		MessageDigest digest = MessageDigest.getInstance("SHA-512");

		// Use the salt in the password hashing
		digest.update(salt);

		// Hash the given password
		byte[] givenPassHash = digest.digest(givenPassword.getBytes(StandardCharsets.UTF_8));

		//Compare the two hashes and return the result
		return Arrays.equals(savedHash, givenPassHash);

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
	 * Verifies if a login passed via a Base64 String (HTTP Basic Authentication) is correct.
	 * Handles decoding the given String verifying if the USer exists and if the password is correct.
	 *
	 * @param encodedAuth A Base64 encoded string in the form of <br/>{username}:{password} --
	 * <a href="https://en.wikipedia.org/wiki/Basic_access_authentication">For more Information</a>
	 *
	 * @return
	 * true if the User was correctly logged in  -- User exists and  the correct password was given
	 * false if the login  failed for any reason
	 *
	 * @throws NoSuchAlgorithmException This exception indicates an invalid algorithm name was given to a
	 * {@link MessageDigest} object. The algorithm in this function is hard coded and this should NEVER occur.
	 */
	private boolean checkBasicAuth(String encodedAuth) throws NoSuchAlgorithmException{
		//Decode the authorization header
		byte[] authBytes = Base64.getDecoder().decode(encodedAuth);
		String auth = new String(authBytes);

		//Separate the Username and password
		int authSplit = auth.lastIndexOf(':');
		String username = auth.substring(0, authSplit);
		String password = auth.substring(authSplit +1);

		//Find the User with the given Username
		User authUser = userRepository.findByName(username);

		//Return false if no such User exists
		if(authUser == null){
			return false;
		}

		// Return true if the given password is correct and false if it isn't
		return checkPassword(authUser, password);
	}


	public static void main(String[] args) {
		SpringApplication.run(CheckMeBackendApplication.class, args);
	}

	/** THIS IS A DESIGNED IN USE IN DEVELOPMENT AND WILL / SHOULD NOT BE EXPOSED IN A PRODUCTION SCENARIO
	 * Get the information for a given username
	 *
	 * @param username The username of a user in the database
	 *
	 * @return The {@link User} object for the requested User
	 *
	 * @throws ResponseStatusException Will be thrown if no user exists with the passed username
	 */
	@GetMapping("/dev/user/{username}")
	public User seeUserDev(@PathVariable String username){
		User toReturn = userRepository.findByName(username);

		// Throw an exception if the user doesn't exist
		if(toReturn == null){
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,"There is no user with the given username");
		}

		return toReturn;
	}

	/**
	 * Creates a new {@link User} in the database with the given username and password.
	 * Returns a 201 status code if the username is created.
	 * Returns a 400 status code if the JSON is malformed or missing required fields.
	 * Returns a 409 status code if the username is taken.
	 *
	 * @param userInfo JSON body included in the POST request with the username and password of the user to be created.
	 *
	 * @return A {@link ResponseEntity} with a 201 status code indicating the user was created, a
	 * 409 status code indicating the username was taken, or a 400 code if the request body's JSON is malformed or
	 * is missing a field.
	 *
	 * @throws NoSuchAlgorithmException This exception indicates an invalid algorithm name was given to a
	 * {@link MessageDigest} object. The algorithm in this function is hard coded and this should NEVER occur.
	 */
	@PostMapping("/user")
	// Unchecked casts are from interacting with the JSON API
	@SuppressWarnings("unchecked")
	public ResponseEntity<String> createUser(@RequestBody String userInfo) throws NoSuchAlgorithmException {
		// Parse the JSON body of the post request
		JSONParser parseBody = new JSONParser(userInfo);

		LinkedHashMap<Object, Object> userJSON;

		// Catch the exception if the JSON can not be parsed and return a bad request response
		try{
			// Parse the JSON to a LinkedHashMap -- this is an unchecked cast but is necessary because of the JSON API
			userJSON = (LinkedHashMap<Object, Object>) parseBody.parse();
		}
		catch(ParseException e){
			return new ResponseEntity<>("JSON in request body could not be parsed.", HttpStatus.BAD_REQUEST);
		}

		// Get the Username given in the request
		String username = (String) userJSON.get("username");

		//  Return 400 if the username wasn't included
		if(username == null){
			return new ResponseEntity<>("Could not find username in request body", HttpStatus.BAD_REQUEST);
		}

		//Confirm that the username is unqiue
		User existingUser = userRepository.findByName(username);

		// If the search found a user
		if(existingUser != null){
			return new ResponseEntity<>("The given username is taken.", HttpStatus.CONFLICT);
		}

		// This will only be reached if the username is allowed

		//Hash the given password
		//Get the user's password in plaintext
		String password = (String) userJSON.get("password");

		// Return 400 if the password was not included
		if(password == null){
			return new ResponseEntity<>("Could not find password in request body", HttpStatus.BAD_REQUEST);
		}

		// Generate an 8 byte salt
		SecureRandom secRan = new SecureRandom();

		byte[] salt = new byte[8];
		secRan.nextBytes(salt);


		// Get a MessageDigest object for SHA-512
		MessageDigest digest = MessageDigest.getInstance("SHA-512");

		// Use the salt in the password hashing
		digest.update(salt);

		// Hash the password
		byte[] hashedPassword = digest.digest(password.getBytes(StandardCharsets.UTF_8));

		// Read the email from the HTTP Request
		String email = (String) userJSON.get("email_address");

		//Return 400 if the email address wasn't included
		if(email == null){
			return new ResponseEntity<>("Could not find email_address in request body", HttpStatus.BAD_REQUEST);
		}

		//Store the hash and the salt
		// Create the new User
		User createdUser = new User(username, email, hashedPassword, salt);

		userRepository.save(createdUser);

		return new ResponseEntity<>("Created new user: " + username,HttpStatus.CREATED );
	}
	@GetMapping("/user/{username}")
	public User seeUser(@PathVariable String username, @RequestHeader(HttpHeaders.AUTHORIZATION) String password) throws NoSuchAlgorithmException {
		// Get the user object for the given username
		User requested = userRepository.findByName(username);

		// Return 404 if the requested user doesn't exist
		if(requested == null){
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,"There is no user with the given username");

		}

		// Return 400 if a password wasn't given
		if(password == null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Missing authorization header");
		}

		//Check if the given password was correct
		Boolean passwordMatch = checkPassword(requested, password);

		// If the wrong password was given return
		if(!passwordMatch){
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Incorrect Password");
		}

		//Return the  user
		return requested;

	}

	/**
	 * API endpoint to update the profile settings of a user. Allows for the caching of whatever information needed to
	 * by the frontend.
	 *
	 * @param username The username of the user to update the account settings for -- passed as a path variable
	 * in the HTTP request.
	 * @param password The password used to authenticate as the User
	 * @param profileSettings A string containing  whatever information should be saved for this user's profile
	 *
	 * @return
	 * 200 If the update was successful
	 * 404 If no user exists with the given username
	 * 400 If no password was sent in the header
	 * 401 If the password was incorrect
	 *
	 * @throws NoSuchAlgorithmException This exception indicates an invalid algorithm name was given to a
	 * 	 * {@link MessageDigest} object. The algorithm in this function is hard coded and this should NEVER occur.
	 */
	@PutMapping("/user/{username}/account_settings")
	public ResponseEntity<String> updateUserSettings(@PathVariable String username, @RequestHeader(HttpHeaders.AUTHORIZATION) String password, @RequestBody String profileSettings) throws NoSuchAlgorithmException {
		// Get the User to update the account settings for
		User toUpdate = userRepository.findByName(username);

		// Return 404 if the no User exists with the given Username
		if(toUpdate == null){
			return new ResponseEntity<>("No user  with the username " + username +" exists.",HttpStatus.NOT_FOUND);
		}

		// Return 400 if a password wasn't given
		if(password == null){
			return new ResponseEntity<>("Missing Authentication Header",HttpStatus.BAD_REQUEST);
		}

		//Check if the given password was correct
		Boolean passwordMatch = checkPassword(toUpdate, password);

		// If the wrong password was given return 401
		if(!passwordMatch){
			return new ResponseEntity<>("Incorrect Password",HttpStatus.UNAUTHORIZED);
		}

		//Update the User in the database
		toUpdate.setProfileSettings(profileSettings);
		userRepository.save(toUpdate);

		//Return 200
		return new ResponseEntity<>("Updated User: " + username,HttpStatus.OK);

	}

	/**
	 * API endpoint to retrieve the profile settings of a user.
	 *
	 * @param username The username of the user to update the account settings for -- passed as a path variable
	 * in the HTTP request.
	 * @param password The password used to authenticate as the User
	 *
	 * @return
	 * If the GET was successful (Existing user and correct password were given)
	 * 	Return the String of cached user profile data and status code 200
	 * If the GET was unsuccessful throw an exception and return the following status codes
	 * 	404 If no user exists with the given username
	 * 	400 If no password was sent in the header
	 * 	401 If the password was incorrect
	 *
	 * @throws NoSuchAlgorithmException This exception indicates an invalid algorithm name was given to a
	 * 	 * {@link MessageDigest} object. The algorithm in this function is hard coded and this should NEVER occur.
	 */
	@GetMapping("/user/{username}/account_settings")
	public String getUserSettings(@PathVariable String username, @RequestHeader(HttpHeaders.AUTHORIZATION) String password) throws NoSuchAlgorithmException {
		// Get the User to update the account settings for
		User toReturn = userRepository.findByName(username);

		// Return 404 if the no User exists with the given Username
		if(toReturn == null){
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No user  with the username "  + username + "  exists.");
		}

		// Return 400 if a password wasn't given
		if(password == null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Missing Authentication Header");
		}

		//Check if the given password was correct
		Boolean passwordMatch = checkPassword(toReturn, password);

		// If the wrong password was given return 401
		if(!passwordMatch){
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Incorrect Password");
		}


		//Return the requested settings
		return toReturn.getProfileSettings();

	}

	/**
	 * Takes in login information in the request body and returns true if a user login was successful
	 * and false if it was not.
	 *
	 * @param body JSON Request Body with a username and password field.
	 *
	 * @return
	 * If the login was successful returns true with status code 200
	 * If the username or password is missing returns false with code 400
	 * If no user with the given username exists return false with code 404
	 * If the password is incorrect return false with code 401
	 *
	 * @throws NoSuchAlgorithmException This exception indicates an invalid algorithm name was given to a
	 *{@link MessageDigest} object. The algorithm in this function is hard coded and this should NEVER occur.
	 */
	@PostMapping("/user/login")
	// Unchecked casts are from interacting with the JSON API
	@SuppressWarnings("unchecked")
	public ResponseEntity<Boolean> login(@RequestBody String body) throws NoSuchAlgorithmException {
		// Parses the JSON body of the post request
		JSONParser parseBody = new JSONParser(body);

		LinkedHashMap<Object, Object> userJSON;

		// Catch the exception if the JSON can not be parsed and return a bad request response
		try{
			// Parse the JSON to a LinkedHashMap -- this is an unchecked cast but is necessary because of the JSON API
			userJSON = (LinkedHashMap<Object, Object>) parseBody.parse();
		}
		catch(ParseException e){
			return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
		}

		// Get the Username given in the request
		String username = (String) userJSON.get("username");

		//  Return 400 if the username wasn't included
		if(username == null){
			return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
		}

		//Get the User
		User user = userRepository.findByName(username);

		// Return 404 if the user doesn't exist
		if(user == null){
			return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
		}

		//Get the given password
		String password = (String) userJSON.get("password");

		// Return 400 if the password was not included
		if(password == null){
			return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
		}

		//Confirm that the password is correct
		Boolean correctPass = checkPassword(user,password);

		//If the password is correct
		if(correctPass){
			//Return a successful login
			return new ResponseEntity<>(true, HttpStatus.OK);
		}
		// If the password  was incorrect
		else {
			return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);

		}

	}

	/**
	 * API endpoint to create a new Group. Will add a new {@link Group} object to the database corresponding to the
	 * created Group. The User making this request will be added to the Group as an admin.
	 *
	 * @param groupInfo Request body in JSON form -- should include a field "name" with the name to give the newly
	 * created group.
	 * @param userAuth The requesting User's username and password using <a href="https://en.wikipedia.org/wiki/Basic_access_authentication">HTTP Basic Authentication</a>
	 * Info should be sent in the form of Basic: {username}:{password}
	 *
	 * @return
	 * 201 Status If the group was succesfully created
	 * 400 Status If the request's body is invalid JSON or if the JSOn does not contain a "name" field
	 * 401 Status If the User making the request could not be properly authenticated
	 * 407 Status If a Group with the given name already exists
	 *
	 * @throws NoSuchAlgorithmException This exception indicates an invalid algorithm name was given to a
	 * {@link MessageDigest} object. The algorithm in this function is hard coded and this should NEVER occur.
	 */
	// Unchecked casts are from interacting with the JSON API
	@SuppressWarnings("unchecked")
	@PostMapping("/group")
	public ResponseEntity<String> createGroup(@RequestBody String groupInfo, @RequestHeader(HttpHeaders.AUTHORIZATION) String userAuth) throws NoSuchAlgorithmException{
		//Authorize that the request to create this group comes from a valid user and they are authenticated
		String authString = parseBasicAuthHeader(userAuth);
		boolean authenticated = checkBasicAuth(authString);

		//Return 401  if the User could not be  authenticated
		if(!authenticated){
			return new ResponseEntity<>("Username or Password was incorrect.",HttpStatus.UNAUTHORIZED);
		}

		//Get the User making the request
		//Decode the authorization header
		byte[] authBytes = Base64.getDecoder().decode(authString);
		String auth = new String(authBytes);
		//Separate the Username and password
		int authSplit = auth.lastIndexOf(':');
		String username = auth.substring(0, authSplit);
		User requestUser = userRepository.findByName(username);

		//Read the name of the  Group from the request body
		JSONParser parser = new JSONParser(groupInfo);
		LinkedHashMap<Object, Object> requestBody;

		//Attempt to Parse body returning 400 if Invalid JSON was given
		try{
			requestBody = (LinkedHashMap<Object, Object>) parser.parse();

		}
		catch(ParseException e){
			return new ResponseEntity<>("Could not parse JSON included in request body",HttpStatus.BAD_REQUEST);
		}

		String name = (String) requestBody.get("name");

		if(name == null){
			return new ResponseEntity<>("No Group name included in request body",HttpStatus.BAD_REQUEST);
		}

		//Confirm no group with the  given name exists
		if(groupRepository.findByName(name) != null){
			return new ResponseEntity<>("Group name " + name + " is taken." ,HttpStatus.CONFLICT);
		}

		//Create the group
		Group createdGroup = new Group(name, requestUser);
		//Add the group to the creator
		requestUser.joinGroup(createdGroup);

		//Save
		userRepository.save(requestUser);
		groupRepository.save(createdGroup);

		//Return success indicator
		return new ResponseEntity<>("Created Group: "+name,HttpStatus.CREATED);

	}


	/**
	 * API Endpoint for retrieving a {@link Group} object using the randomly generated name of the group.
	 *
	 * @param name A string holding the assigned name of this group
	 * @param authorization The requesting User's username and password using <a href="https://en.wikipedia.org/wiki/Basic_access_authentication">HTTP Basic Authentication</a>
	 * Info should be sent in the form of Basic: {username}:{password}
	 *
	 * @return The {@link  Group} object requested
	 *
	 * @throws ResponseStatusException
	 * 401 If a User's account could not be signed in to with the given Authentication info.
	 * 403 If the requesting User is not a member of the requested Group
	 * 404 If no group with the given name exists
	 *
	 * @throws NoSuchAlgorithmException This exception indicates an invalid algorithm name was given to a
	 * {@link MessageDigest} object. The algorithm in this function is hard coded and this should NEVER occur.
	 */

	@GetMapping("/group/name/{name}")
	public Group getGroupByName(@PathVariable String name, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) throws NoSuchAlgorithmException, ResponseStatusException {
		//Separate the Base64 string from the rest of the authentication header
		authorization = parseBasicAuthHeader(authorization);

		//Check if the User's authentication is correct
		boolean checkAuth = checkBasicAuth(authorization);

		if(!checkAuth){
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or Password was incorrect");
		}

		//Get a group with the requested name
		Group requested = groupRepository.findByName(name);

		// Return 404 if the requested group doesn't exist
		if(requested == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no group with the given name");

		}

		//Get the user making the request
		//Decode the authorization header
		byte[] authBytes = Base64.getDecoder().decode(authorization);
		String auth = new String(authBytes);

		//Separate the Username and password
		int authSplit = auth.lastIndexOf(':');
		String username = auth.substring(0, authSplit);

		User requestUser = userRepository.findByName(username);

		//Check if the User is a member of the group and return 403 if they are not
		if(!requested.getMembers().contains(requestUser)){
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this group");
		}

		return requested;
	}

	/**
	 * API Endpoint for retrieving a {@link Group} object using the randomly generated Join code of the group.
	 *
	 * @param joinCode The 8 character randomly generated String used for joining this Group.
	 * @param authorization The requesting User's username and password using <a href="https://en.wikipedia.org/wiki/Basic_access_authentication">HTTP Basic Authentication</a>
	 * Info should be sent in the form of Basic: {username}:{password}
	 *
	 * @return The {@link  Group} object requested
	 *
	 * @throws ResponseStatusException
	 * 401 If a User's account could not be signed in to with the given Authentication info.
	 * 403 If the requesting User is not a member of the requested Group
	 * 404 If no group with the Given join code exists
	 *
	 * @throws NoSuchAlgorithmException This exception indicates an invalid algorithm name was given to a
	 * {@link MessageDigest} object. The algorithm in this function is hard coded and this should NEVER occur.
	 */
	@GetMapping("/group/code/{joinCode}")
	public Group getGroupByJoinCode(@PathVariable String joinCode, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) throws NoSuchAlgorithmException, ResponseStatusException {
		//Separate the Base64 string from the rest of the authentication header
		authorization = parseBasicAuthHeader(authorization);

		//Check if the User's authentication is correct
		boolean checkAuth = checkBasicAuth(authorization);

		if(!checkAuth){
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or Password was incorrect");
		}

		//Get a group with the requested join code
		Group requested = groupRepository.findByJoinCode(joinCode);

		// Return 404 if the requested group doesn't exist
		if(requested == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no group with the given join code");

		}

		//Get the user making the request
		//Decode the authorization header
		byte[] authBytes = Base64.getDecoder().decode(authorization);
		String auth = new String(authBytes);

		//Separate the Username and password
		int authSplit = auth.lastIndexOf(':');
		String username = auth.substring(0, authSplit);

		User requestUser = userRepository.findByName(username);

		//Check if the User is a member of the group and return 403 if they are not
		if(!requested.getMembers().contains(requestUser)){
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this group");
		}

		return requested;
	}

	/**
	 * Adds a member to a Group
	 *
	 * @param joinCode The join code of the Group to join
	 * @param body JSON body containing the login of the User to add to the Group

	 * @return
	 * 200 Status If the Group joins the member
	 * 400 Status If the request's body is invalid JSON
	 * 401 Status If the User making the request could not be properly authenticated
	 * 404 Status If either the member making the request does not exist or there is no Group with the given join code
	 *
	 * @throws NoSuchAlgorithmException This exception indicates an invalid algorithm name was given to a
	 * {@link MessageDigest} object. The algorithm in this function is hard coded and this should NEVER occur.
	 */
	// Unchecked casts are from interacting with the JSON API
	@SuppressWarnings("unchecked")
	@PutMapping("/group/join/{joinCode}")
	public ResponseEntity<String> joinGroup(@PathVariable String joinCode, @RequestBody String body) throws NoSuchAlgorithmException {
		// Get the group to join
		Group toJoin = groupRepository.findByJoinCode(joinCode);

		//Return 404 if the Group doesn't exist
		if(toJoin == null){
			return new ResponseEntity<>("There is no Group with the given join code", HttpStatus.NOT_FOUND);
		}


		// Create object to parse the body of the request
		JSONParser parser = new JSONParser(body);

		//Attempt to parse the JSON -- Return 400 if it cannot be parsed
		LinkedHashMap<Object, Object> requestBody;
		try{
			//Parse the JSON
			requestBody = (LinkedHashMap<Object, Object>) parser.parse();
		}
		catch(ParseException e){
			return new ResponseEntity<>("Could not parse JSON included in request body", HttpStatus.BAD_REQUEST);
		}

		//Get the username and password from the request body
		String username = (String) requestBody.get("username");
		String password = (String) requestBody.get("password");

		//Retrieve the User object for the given Username
		User toPut = userRepository.findByName(username);

		//Return 404 if the given User does not exist
		if(toPut == null){
			return new ResponseEntity<>("There is no User with the given username", HttpStatus.NOT_FOUND);
		}

		//Verify that the User's login (password) is correct
		boolean loggedIn = checkPassword(toPut, password);

		// Return 401 if the login is incorrect
		if(!loggedIn){
			return new ResponseEntity<>("Incorrect Password", HttpStatus.UNAUTHORIZED);

		}

		//Add the User to the group and vice versa
		toPut.joinGroup(toJoin);
		toJoin.addMember(toPut);

		userRepository.save(toPut);
		groupRepository.save(toJoin);

		//Return 200
		return new ResponseEntity<>("Group Joined", HttpStatus.OK);

	}

	/**
	 * Promotes a member of a Group to have admin powers
	 *
	 * @param groupName The name of the Group to operate on
	 * @param body JSON body containing the Username of the User to promote

	 * @return
	 * 200 Status If the member was successfully promoted
	 * 400 Status If the request's body is invalid JSON or the member to promote is not a member of the group
	 * 401 Status If the User making the request could not be properly authenticated
	 * 403 Status If the User making the request is not an admin of the Group the being operated on
	 * 404 Status If the member to promote or the group to operate on
	 * do not exist
	 *
	 * @throws NoSuchAlgorithmException This exception indicates an invalid algorithm name was given to a
	 * {@link MessageDigest} object. The algorithm in this function is hard coded and this should NEVER occur.
	 */
	// Unchecked casts are from interacting with the JSON API
	@SuppressWarnings("unchecked")
	@PutMapping("/group/promote/{groupName}")
	public ResponseEntity<String> promoteMember(@PathVariable String groupName,  @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @RequestBody String body) throws NoSuchAlgorithmException {
		// Get the group to join
		Group group = groupRepository.findByName(groupName);

		//Return 404 if the Group doesn't exist
		if(group == null){
			return new ResponseEntity<>("There is no Group with the given join code", HttpStatus.NOT_FOUND);
		}

		//Separate the Base64 string from the rest of the authentication header
		authorization = parseBasicAuthHeader(authorization);

		//Check if the User's authentication is correct
		boolean checkAuth = checkBasicAuth(authorization);

		if(!checkAuth){
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or Password was incorrect");
		}

		//Get the User making the request
		//Decode the authorization header
		byte[] authBytes = Base64.getDecoder().decode(authorization);
		String auth = new String(authBytes);
		//Separate the Username and password
		int authSplit = auth.lastIndexOf(':');
		String username = auth.substring(0, authSplit);

		//Check that the requesting User is an admin
		if(!group.getAdmins().contains(username)){
			return new ResponseEntity<>("User does not have permission to perform this action", HttpStatus.FORBIDDEN);
		}

		// Create object to parse the body of the request
		JSONParser parser = new JSONParser(body);

		//Attempt to parse the JSON -- Return 400 if it cannot be parsed
		LinkedHashMap<Object, Object> requestBody;
		try{
			//Parse the JSON
			requestBody = (LinkedHashMap<Object, Object>) parser.parse();
		}
		catch(ParseException e){
			return new ResponseEntity<>("Could not parse JSON included in request body", HttpStatus.BAD_REQUEST);
		}



		// Get the Username of the member to promote
		String toPromoteName = (String) requestBody.get("toPromote");
		User toPromote = userRepository.findByName(toPromoteName);

		//Return 404 if the user to promote does not exist
		if(toPromote == null){
			return new ResponseEntity<>("User to promote does not exist", HttpStatus.NOT_FOUND);

		}

		//Return 400 if the User to promote is not a member of thr group
		if(!group.getMembers().contains(toPromote)){
			return new ResponseEntity<>("User " + toPromote.getName() + " is not a member of group " + group.getName(), HttpStatus.BAD_REQUEST);

		}

		// Add the member to the group
		group.addAdmin(toPromote);

		groupRepository.save(group);

		return new ResponseEntity<>("User " + toPromote.getName() + " added to admins of group " + group.getName(), HttpStatus.OK);

	}

	/**
	 * Remove a member from a Grpup
	 *
	 * @param groupName The name of the Group to operate on
	 * @param body JSON body containing the username of the User to remove

	 * @return
	 * 200 Status If the member was successfully removed
	 * 400 Status If the request's body is invalid JSON or the member to promote is not a member of the group
	 * 401 Status If the User making the request could not be properly authenticated
	 * 403 Status If the User making the request is not an admin of the Group the being operated on
	 * 404 Status If the member to promote or the group to operate on
	 * do not exist
	 *
	 * @throws NoSuchAlgorithmException This exception indicates an invalid algorithm name was given to a
	 * {@link MessageDigest} object. The algorithm in this function is hard coded and this should NEVER occur.
	 */
	// Unchecked casts are from interacting with the JSON API
	@SuppressWarnings("unchecked")
	@DeleteMapping("/group/remove/{groupName}")
	public ResponseEntity<String> removeMember(@PathVariable String groupName,  @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @RequestBody String body) throws NoSuchAlgorithmException {
		// Get the group to join
		Group group = groupRepository.findByName(groupName);

		//Return 404 if the Group doesn't exist
		if(group == null){
			return new ResponseEntity<>("There is no Group with the given join code", HttpStatus.NOT_FOUND);
		}

		//Separate the Base64 string from the rest of the authentication header
		authorization = parseBasicAuthHeader(authorization);

		//Check if the User's authentication is correct
		boolean checkAuth = checkBasicAuth(authorization);

		if(!checkAuth){
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or Password was incorrect");
		}

		//Get the User making the request
		//Decode the authorization header
		byte[] authBytes = Base64.getDecoder().decode(authorization);
		String auth = new String(authBytes);
		//Separate the Username and password
		int authSplit = auth.lastIndexOf(':');
		String username = auth.substring(0, authSplit);

		//Check that the requesting User is an admin
		if(!group.getAdmins().contains(username)){
			return new ResponseEntity<>("User does not have permission to perform this action", HttpStatus.FORBIDDEN);
		}

		// Create object to parse the body of the request
		JSONParser parser = new JSONParser(body);

		//Attempt to parse the JSON -- Return 400 if it cannot be parsed
		LinkedHashMap<Object, Object> requestBody;
		try{
			//Parse the JSON
			requestBody = (LinkedHashMap<Object, Object>) parser.parse();
		}
		catch(ParseException e){
			return new ResponseEntity<>("Could not parse JSON included in request body", HttpStatus.BAD_REQUEST);
		}



		// Get the Username of the member to promote
		String toRemoveName = (String) requestBody.get("toRemove");
		User toRemove = userRepository.findByName(toRemoveName);

		//Return 404 if the user to promote does not exist
		if(toRemove == null){
			return new ResponseEntity<>("User to promote does not exist", HttpStatus.NOT_FOUND);

		}

		//Return 400 if the User to remove is not a member of the group
		if(!group.getMembers().contains(toRemove)){
			return new ResponseEntity<>("User " + toRemove.getName() + " is not a member of group " + group.getName(), HttpStatus.BAD_REQUEST);
		}

		//Return 403 if the member to remove is an admin
		if(group.getAdmins().contains((toRemoveName))){
			return new ResponseEntity<>("User " + toRemove.getName() + " is a group admin and cannot be removed.", HttpStatus.FORBIDDEN);

		}

		// Remove the member from the Group
		group.removeMember(toRemove);

		//Remove the group from the User
		toRemove.removeGroup(group);

		userRepository.save(toRemove);
		groupRepository.save(group);

		return new ResponseEntity<>("User " + toRemove.getName() + " removed from the group " + group.getName(), HttpStatus.OK);

	}


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
