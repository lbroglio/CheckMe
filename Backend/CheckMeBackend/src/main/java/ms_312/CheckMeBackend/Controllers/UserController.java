package ms_312.CheckMeBackend.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import ms_312.CheckMeBackend.Users.Group;
import ms_312.CheckMeBackend.Users.User;
import ms_312.CheckMeBackend.Users.UserRepository;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.List;

// VIEW DOCS AT - http://localhost:8080/swagger-ui/index.html
@Tag(name = "UserAPI", description = "Rest API used for Creating and managing User accounts within the CheckMe service.")
@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;


    /** THIS IS A DESIGNED FOR USE IN DEVELOPMENT AND WILL NOT / SHOULD NOT BE EXPOSED IN A PRODUCTION SCENARIO
     * Get the information for a given username
     *
     * @param username The username of a user in the database
     *
     * @return The {@link User} object for the requested User
     *
     * @throws ResponseStatusException Will be thrown if no user exists with the passed username
     */
    @GetMapping("/dev/user/{username}")
    @Operation(description  = "Get a specific User's information without authorizing as that User -- THIS IS A DESIGNED FOR USE IN DEVELOPMENT AND WILL NOT / SHOULD NOT BE EXPOSED IN A PRODUCTION SCENARIO",  tags = "seeUserDev")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" , description =  "Success|OK"),
            @ApiResponse(responseCode = "404" , description =  "No user with the given Username", content = @Content) })
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
    @Operation(description  = "Create a new User.",  tags = "createUser")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201" , description =  "Success|OK", content = @Content(schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(description = "Success", value = "Created new user: User101"))),
            @ApiResponse(responseCode = "400" , description =  "JSON in request body could not be parsed or is missing required fields", content = @Content(schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(description = "Code 400", value = "JSON in request body could not be parsed. OR Could not find {missing field} in request body"))),
            @ApiResponse(responseCode = "409" , description =  "The given username is taken.", content = @Content(schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(description = "Code 409", value = "The given username is taken.")))})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Information to create an account with",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {
                            @ExampleObject(
                                    name = "An example request for this endpoint describing each field",
                                    value = """
                                            {
                                                "username": "The unique username that will be used for the created user",
                                                "email_address": "Email to be associated with the created user",
                                                "password": "THe new user's  password "
                                            }""",
                                    summary = "Description of each field needed for a request to create a user"),
                            @ExampleObject(
                                    name = "An example request for this endpoint with sample data.",
                                    value = """
                                            {
                                                "username": "User101",
                                                "email_address": "example@gamil.com",
                                                "password": "Password123"
                                            }""",
                                    summary = "Example body of a Request made for creating a User")
                    }))
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
    @Operation(description  = "Get the information for a specific User object",  tags = "seeUser")
    @Parameter(name = "username", in = ParameterIn.PATH, description = "The username of the User to retrieve", required = true, examples = {
            @ExampleObject(name = "example", value = "User101")})
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, description = "The password to the User account being requested", required = true, examples = {
            @ExampleObject(name = "example", value = "Password123")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" , description =  "Success|OK"),
            @ApiResponse(responseCode = "400" , description =  "Missing authorization header", content = @Content),
            @ApiResponse(responseCode = "401" , description =  "Incorrect Password", content = @Content),
            @ApiResponse(responseCode = "404" , description =  "There is no user with the given username", content = @Content),})
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
        Boolean passwordMatch = ControllerUtils.checkPassword(requested, password);

        // If the wrong password was given return
        if(!passwordMatch){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Incorrect Password");
        }

        //Return the  user
        return requested;

    }

    /**
     * API endpoint to update the profile settings of a user. Allows for the caching of whatever information needed to
     * by the frontend application.
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
    @Operation(description  = "Set  the profile settings of a user. Allows for the caching of whatever information needed to by the frontend application.",  tags = "updateUserSettings")
    @Parameter(name = "username", in = ParameterIn.PATH, description = "The username of the User to update the settings of", required = true, examples = {
            @ExampleObject(name = "example", value = "User101")})
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, description = "The password to the User account to be updated", required = true, examples = {
            @ExampleObject(name = "example", value = "Password123")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" , description =  "Success|OK", content = @Content(schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(description = "Success", value = "Updated User:  User101"))),
            @ApiResponse(responseCode = "400" , description =  "Missing authorization header", content = @Content(schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(description = "Code - 400", value = "Missing Authentication Header"))),
            @ApiResponse(responseCode = "401" , description =  "Incorrect Password", content = @Content(schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(description = "Code - 401", value = "Incorrect Password"))),
            @ApiResponse(responseCode = "404" , description =  "No user exists with the given username", content = @Content(schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(description = "Code - 404", value = "No user exists with the given username")))})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "JSON String to store whatever values the frontend needs persisted",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {
                            @ExampleObject(
                                    name = "An example request for this endpoint describing each field",
                                    value = """
                                            {
                                            "ExampleSetting": "on"
                                            }
                                            """,
                                    summary = "Description of each field needed for a request to create a user")
                    }))
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
        Boolean passwordMatch = ControllerUtils.checkPassword(toUpdate, password);

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
    @Operation(description  = "Get the profile settings of a user. Used for caching of whatever information needed by the frontend application.",  tags = "getUserSettings")
    @Parameter(name = "username", in = ParameterIn.PATH, description = "The username of the User to update the settings of", required = true, examples = {
            @ExampleObject(name = "example", value = "User101")})
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, description = "The password to the User account to be updated", required = true, examples = {
            @ExampleObject(name = "example", value = "Password123")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" , description =  "Success|OK", content = @Content(schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(description = "Success", value = "{\n\"ExampleSettings\": \"on\"\n}"))),
            @ApiResponse(responseCode = "400" , description =  "Missing authorization header", content = @Content(schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(description = "Code 400", value = "Missing Authentication Header"))),
            @ApiResponse(responseCode = "401" , description =  "Incorrect Password", content = @Content(schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(description = "Code 401", value = "Incorrect Password"))),
            @ApiResponse(responseCode = "404" , description =  "No user exists with the given username", content = @Content(schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(description = "Code 404", value = "No user exists with the given username")))})
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
        Boolean passwordMatch = ControllerUtils.checkPassword(toReturn, password);

        // If the wrong password was given return 401
        if(!passwordMatch){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Incorrect Password");
        }


        //Return the requested settings
        return toReturn.getProfileSettings();

    }

    /**
     * API endpoint to retrieve the groups a User is a member of
     *
     * @param username The username of the user to update the account settings for -- passed as a path variable
     * in the HTTP request.
     * @param password The password used to authenticate as the User
     *
     * @return
     * If the GET was successful (Existing user and correct password were given)
     * 	Return the list of Groups the User is a member of and status code 200
     * If the GET was unsuccessful throw an exception and return the following status codes
     * 	404 If no user exists with the given username
     * 	400 If no password was sent in the header
     * 	401 If the password was incorrect
     *
     * @throws NoSuchAlgorithmException This exception indicates an invalid algorithm name was given to a
     * 	 * {@link MessageDigest} object. The algorithm in this function is hard coded and this should NEVER occur.
     */
    @GetMapping("/user/{username}/groups")
    @Operation(description  = "Get the list of Groups a User is in.",  tags = "getUserGroups")
    @Parameter(name = "username", in = ParameterIn.PATH, description = "The username of the User to update the settings of", required = true, examples = {
            @ExampleObject(name = "example", value = "User101")})
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, description = "The password to the User account to be updated", required = true, examples = {
            @ExampleObject(name = "example", value = "Password123")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" , description =  "Success|OK"),
            @ApiResponse(responseCode = "400" , description =  "Missing authorization header", content = @Content),
            @ApiResponse(responseCode = "401" , description =  "Incorrect Password", content = @Content),
            @ApiResponse(responseCode = "404" , description =  "No user exists with the given username", content = @Content),})
    public List<Group> getUserGroups(@PathVariable String username, @RequestHeader(HttpHeaders.AUTHORIZATION) String password) throws NoSuchAlgorithmException {
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
        Boolean passwordMatch = ControllerUtils.checkPassword(toReturn, password);

        // If the wrong password was given return 401
        if(!passwordMatch){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Incorrect Password");
        }


        //Return the requested settings
        return toReturn.getGroups();

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
    @Operation(description  = "Verify if the login information for a User is correct. Returns true if the login was successful; false if it was not",  tags = "getUserGroups")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" , description =  "Success|OK", content = @Content(schema = @Schema(implementation = Boolean.class),
                    examples = @ExampleObject(description = "Success", value = "true"))),
            @ApiResponse(responseCode = "400" , description =  "JSON in request body could not be parsed or is missing required fields", content = @Content(schema = @Schema(implementation = Boolean.class),
                    examples = @ExampleObject(description = "Code 400", value = "false"))),
            @ApiResponse(responseCode = "401" , description =  "Incorrect Password - false", content = @Content(schema = @Schema(implementation = Boolean.class),
                    examples = @ExampleObject(description = "Code 401", value = "false"))),
            @ApiResponse(responseCode = "404" , description =  "No user exists with the given username - false", content = @Content(schema = @Schema(implementation = Boolean.class),
                    examples = @ExampleObject(description = "Code 404", value = "false")))})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Login  information for a CheckMe account",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {
                            @ExampleObject(
                                    name = "An example request for this endpoint describing each field",
                                    value = """
                                            {
                                                "username": "The username of the account to authorize as",
                                                "password": "The password for the account to log into "
                                            }""",
                                    summary = "Description of each field needed for a request for login"),
                            @ExampleObject(
                                    name = "An example request for this endpoint with sample data.",
                                    value = """
                                            {
                                                "username": "User101",
                                                "password": "Password123"
                                            }""",
                                    summary = "Example body of a Request made for login ")
                    }))
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
        Boolean correctPass = ControllerUtils.checkPassword(user,password);

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
