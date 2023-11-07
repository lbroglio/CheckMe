package ms_312.CheckMeBackend.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ms_312.CheckMeBackend.Users.Group;
import ms_312.CheckMeBackend.Users.GroupRepository;
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
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;

@Tag(name = "GroupAPI", description = "Rest API used for Creating and managing Groups within the CheckMe service.")
@RestController
public class GroupController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    GroupRepository groupRepository;



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
     * 409 Status If a Group with the given name already exists
     *
     * @throws NoSuchAlgorithmException This exception indicates an invalid algorithm name was given to a
     * {@link MessageDigest} object. The algorithm in this function is hard coded and this should NEVER occur.
     */
    // Unchecked casts are from interacting with the JSON API
    @SuppressWarnings("unchecked")
    @PostMapping("/group")
    @Operation(description  = "Create a new Group.",  tags = "createGroup")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201" , description =  "Success|OK", content = @Content(schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(description = "Success", value = "Created Group: Group 202"))),
            @ApiResponse(responseCode = "400" , description =  "JSON in request body could not be parsed or is missing required fields", content = @Content(schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(description = "Code 400", value = "JSON in request body could not be parsed. OR Could not find {missing field} in request body"))),
            @ApiResponse(responseCode = "401" , description =  "Username or Password was incorrect.", content = @Content(schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(description = "Code 401", value = "Username or Password was incorrect."))),
            @ApiResponse(responseCode = "409" , description =  "A Group with the given name already exists", content = @Content(schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(description = "Code 401", value = "A Group with the given name already exists")))})
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, description = "HTTP Basic Authentication with the account information for an existing CheckMe account", required = true, examples = {
            @ExampleObject(name = "example", value = "Basic VXNlcjEwMTpQYXNzd29yZDEyMw==")})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Information to create a group with",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {
                            @ExampleObject(
                                    name = "An example request for this endpoint describing each field",
                                    value = """
                                            {
                                                "name": "The unique name for the created group"
                                            }""",
                                    summary = "Description of each field needed for a request to create a user"),
                            @ExampleObject(
                                    name = "An example request for this endpoint with sample data.",
                                    value = """
                                            {
                                                "name": "Group202"
                                            }""",
                                    summary = "Example body of a Request made for creating a Group")
                    }))
    public ResponseEntity<String> createGroup(@RequestBody String groupInfo, @RequestHeader(HttpHeaders.AUTHORIZATION) String userAuth) throws NoSuchAlgorithmException{
        //Authorize that the request to create this group comes from a valid user and they are authenticated
        String authString = ControllerUtils.parseBasicAuthHeader(userAuth);
        boolean authenticated = ControllerUtils.checkBasicAuth(authString, userRepository);

        //Return 401  if the User could not be  authenticated
        if(!authenticated){
            return new ResponseEntity<>("Username or Password was incorrect.", HttpStatus.UNAUTHORIZED);
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
    @Operation(description  = "Get the information for a Group from its name",  tags = "getGroupByName")
    @Parameter(name = "name", in = ParameterIn.PATH, description = "The name of the Group to retrieve the information for", required = true, examples = {
            @ExampleObject(name = "example", value = "Group202")})
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, description = "HTTP Basic Authentication with the account information for an existing CheckMe account that is a member of the target Group", required = true, examples = {
            @ExampleObject(name = "example", value = "Basic VXNlcjEwMTpQYXNzd29yZDEyMw==")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" , description =  "Success|OK"),
            @ApiResponse(responseCode = "401" , description =  "Username or Password was incorrect", content = @Content),
            @ApiResponse(responseCode = "403" , description =  "User does not have access to this group", content = @Content),
            @ApiResponse(responseCode = "404" , description =  "No Group exists with the given name", content = @Content),})
    public Group getGroupByName(@PathVariable String name, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) throws NoSuchAlgorithmException, ResponseStatusException {
        //Separate the Base64 string from the rest of the authentication header
        authorization =  ControllerUtils.parseBasicAuthHeader(authorization);

        //Check if the User's authentication is correct
        boolean checkAuth =  ControllerUtils.checkBasicAuth(authorization, userRepository);

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
    @Operation(description  = "Get the information for a Group from the code used for joining it",  tags = "getGroupByJoinCode")
    @Parameter(name = "joinCode", in = ParameterIn.PATH, description = "The join code for the Group to retrieve the information for", required = true, examples = {
            @ExampleObject(name = "example", value = "ESDCACOY")})
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, description = "HTTP Basic Authentication with the account information for an existing CheckMe account that is a member of the target Group", required = true, examples = {
            @ExampleObject(name = "example", value = "Basic VXNlcjEwMTpQYXNzd29yZDEyMw==")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" , description =  "Success|OK"),
            @ApiResponse(responseCode = "401" , description =  "Username or Password was incorrect", content = @Content),
            @ApiResponse(responseCode = "403" , description =  "User does not have access to this group", content = @Content),
            @ApiResponse(responseCode = "404" , description =  "No Group exists with the given name", content = @Content),})
    public Group getGroupByJoinCode(@PathVariable String joinCode, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) throws NoSuchAlgorithmException, ResponseStatusException {
        //Separate the Base64 string from the rest of the authentication header
        authorization =  ControllerUtils.parseBasicAuthHeader(authorization);

        //Check if the User's authentication is correct
        boolean checkAuth =  ControllerUtils.checkBasicAuth(authorization, userRepository);

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
    @Operation(description  = "Add a User to a group",  tags = "createGroup")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201" , description =  "Success|OK", content = @Content(schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(description = "Success", value = "Group Joined"))),
            @ApiResponse(responseCode = "400" , description =  "JSON in request body could not be parsed", content = @Content(schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(description = "Code 400", value = "JSON in request body could not be parsed."))),
            @ApiResponse(responseCode = "401" , description =  "Username or Password was incorrect.", content = @Content(schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(description = "Code 401", value = "Incorrect Password"))),
            @ApiResponse(responseCode = "404" , description =  "There is no User with the given username", content = @Content(schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(description = "Code 404", value = "There is no User with the given username")))})
    @Parameter(name = "joinCode", in = ParameterIn.HEADER, description = "The join code for the group to join", required = true, examples = {
            @ExampleObject(name = "example", value = "ESDCACOY")})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Information to create a group with",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {
                            @ExampleObject(
                                    name = "An example request for this endpoint describing each field",
                                    value = """
                                            {
                                                "username": "The username of the account to add to the  Group",
                                                "password": "The password for the account"
                                            }""",
                                    summary = "Description of each field needed for a request to join a Group"),
                            @ExampleObject(
                                    name = "An example request for this endpoint with sample data.",
                                    value = """
                                            {
                                                "username": "User101",
                                                "password": "Password123"
                                            }""",
                                    summary = "Example body of a Request made for joining a group")
                    }))
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
        boolean loggedIn =  ControllerUtils.checkPassword(toPut, password);

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
    @Operation(description  = "Promote a member of a Group to admin",  tags = "promoteMember")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201" , description =  "Success|OK", content = @Content(schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(description = "Success", value = "User User102 added to admins of group Group202"))),
            @ApiResponse(responseCode = "400" , description =  "The request's body is invalid JSON or the member to promote is not a member of the group", content = @Content(schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(description = "Code 400", value = "The request's body is invalid JSON OR User User102 is not a member of Group202"))),
            @ApiResponse(responseCode = "401" , description =  "Username or Password was incorrect.", content = @Content(schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(description = "Code 401", value = "Username or Password was incorrect"))),
            @ApiResponse(responseCode = "403" , description =  "User does not have permission to perform this action", content = @Content(schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(description = "Code 403", value = "User does not have permission to perform this action"))),
            @ApiResponse(responseCode = "404" , description =  "The member to promote or the Group to operate on does not exist", content = @Content(schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(description = "Code 404", value = "There is no Group with the given group name OR User to promote does not exist")))})
    @Parameter(name = "groupName", in = ParameterIn.PATH, description = "The name of the Group to promote a member of", required = true, examples = {
            @ExampleObject(name = "example", value = "Group202")})
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, description = "HTTP Basic Authentication with the account information for an existing CheckMe account that is an admin member of the target Group", required = true, examples = {
            @ExampleObject(name = "example", value = "Basic VXNlcjEwMTpQYXNzd29yZDEyMw==")})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Information for promoting a member",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {
                            @ExampleObject(
                                    name = "An example request for this endpoint describing each field",
                                    value = """
                                            {
                                                "username": "The username of the member of the Group to promote to an admin"
                                            }""",
                                    summary = "Description of each field needed for a request to promote a member"),
                            @ExampleObject(
                                    name = "An example request for this endpoint with sample data.",
                                    value = """
                                            {
                                                "username": "User102"
                                            }""",
                                    summary = "Example body of a Request made for promoting a member")
                    }))
    public ResponseEntity<String> promoteMember(@PathVariable String groupName,  @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @RequestBody String body) throws NoSuchAlgorithmException {
        // Get the group to join
        Group group = groupRepository.findByName(groupName);

        //Return 404 if the Group doesn't exist
        if(group == null){
            return new ResponseEntity<>("There is no Group with the given group name", HttpStatus.NOT_FOUND);
        }

        //Separate the Base64 string from the rest of the authentication header
        authorization =  ControllerUtils.parseBasicAuthHeader(authorization);

        //Check if the User's authentication is correct
        boolean checkAuth =  ControllerUtils.checkBasicAuth(authorization, userRepository);

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
     * Remove a member from a Group
     *
     * @param groupName The name of the Group to operate on
     * @param body JSON body containing the username of the User to remove

     * @return
     * 200 Status If the member was successfully removed
     * 400 Status If the request's body is invalid JSON or the member to remove is not a member of the group
     * 401 Status If the User making the request could not be properly authenticated
     * 403 Status If the User making the request is not an admin of the Group the being operated on
     * 404 Status If the member to remove or the group to operate on
     * do not exist
     *
     * @throws NoSuchAlgorithmException This exception indicates an invalid algorithm name was given to a
     * {@link MessageDigest} object. The algorithm in this function is hard coded and this should NEVER occur.
     */
    // Unchecked casts are from interacting with the JSON API
    @SuppressWarnings("unchecked")
    @DeleteMapping("/group/remove/{groupName}")
    @Operation(description  = "Remove a member of a Group",  tags = "removeMember")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201" , description =  "Success|OK", content = @Content(schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(description = "Success", value = "User User102 removed from the group Group202"))),
            @ApiResponse(responseCode = "400" , description =  "The request's body is invalid JSON or the member to remove is not a member of the group", content = @Content(schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(description = "Code 400", value = "The request's body is invalid JSON OR User User102 is not a member of Group202"))),
            @ApiResponse(responseCode = "401" , description =  "Username or Password was incorrect.", content = @Content(schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(description = "Code 401", value = "Username or Password was incorrect"))),
            @ApiResponse(responseCode = "403" , description =  "User does not have permission to perform this action", content = @Content(schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(description = "Code 403", value = "User does not have permission to perform this action OR User User102 is a group admin and cannot be removed."))),
            @ApiResponse(responseCode = "404" , description =  "The member to promote or the Group to operate on does not exist", content = @Content(schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(description = "Code 404", value = "There is no Group with the given group name OR User to remove does not exist")))})
    @Parameter(name = "groupName", in = ParameterIn.PATH, description = "The name of the Group to remove a member from,", required = true, examples = {
            @ExampleObject(name = "example", value = "Group202")})
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, description = "HTTP Basic Authentication with the account information for an existing CheckMe account that is an admin member of the target Group", required = true, examples = {
            @ExampleObject(name = "example", value = "Basic VXNlcjEwMTpQYXNzd29yZDEyMw==")})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Information for promoting a member",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {
                            @ExampleObject(
                                    name = "An example request for this endpoint describing each field",
                                    value = """
                                            {
                                                "username": "The username of the member of the Group to remove from the group"
                                            }""",
                                    summary = "Description of each field needed for a request to promote a member"),
                            @ExampleObject(
                                    name = "An example request for this endpoint with sample data.",
                                    value = """
                                            {
                                                "username": "User102"
                                            }""",
                                    summary = "Example body of a Request made for removing a member")
                    }))
    public ResponseEntity<String> removeMember(@PathVariable String groupName,  @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @RequestBody String body) throws NoSuchAlgorithmException {
        // Get the group to join
        Group group = groupRepository.findByName(groupName);

        //Return 404 if the Group doesn't exist
        if(group == null){
            return new ResponseEntity<>("There is no Group with the given name", HttpStatus.NOT_FOUND);
        }

        //Separate the Base64 string from the rest of the authentication header
        authorization =  ControllerUtils.parseBasicAuthHeader(authorization);

        //Check if the User's authentication is correct
        boolean checkAuth =  ControllerUtils.checkBasicAuth(authorization, userRepository);

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
            return new ResponseEntity<>("User to remove does not exist", HttpStatus.NOT_FOUND);

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
}
