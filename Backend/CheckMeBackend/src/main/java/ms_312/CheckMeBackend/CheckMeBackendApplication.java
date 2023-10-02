package ms_312.CheckMeBackend;

import ms_312.CheckMeBackend.Users.User;
import ms_312.CheckMeBackend.Users.UserRepository;
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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.LinkedHashMap;

@SpringBootApplication
@RestController
public class CheckMeBackendApplication {
	@Autowired
	UserRepository userRepository;

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

	public static void main(String[] args) {
		SpringApplication.run(CheckMeBackendApplication.class, args);
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
		User existingUser = userRepository.findByUsername(username);

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
		User toReturn = userRepository.findByUsername(username);

		// Throw an exception if the user doesn't exist
		if(toReturn == null){
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,"There is no user with the given username");
		}

		return toReturn;
	}


	@GetMapping("/user/{username}")
	public User seeUser(@PathVariable String username, @RequestHeader(HttpHeaders.AUTHORIZATION) String password) throws NoSuchAlgorithmException {
		// Get the user object for the given username
		User requested = userRepository.findByUsername(username);

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
		User toUpdate = userRepository.findByUsername(username);

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
		User toReturn = userRepository.findByUsername(username);

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
		User user = userRepository.findByUsername(username);

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









}
