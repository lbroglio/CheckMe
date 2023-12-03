package ms_312.CheckMeBackend.Controllers;


import ms_312.CheckMeBackend.Users.User;
import ms_312.CheckMeBackend.Users.UserRepository;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashSet;
import java.util.LinkedHashMap;

@RestController
public class AdminUserController {
    @Autowired
    private UserRepository userRepository;

    private boolean checkAdmin(String username) {
        User user = userRepository.findByName(username);
        return user.getUserType() == User.UserType.ADMIN;
    }

    @DeleteMapping("/delete/{userToDelete}")
    public ResponseEntity<String> deleteUser(@PathVariable String userToDelete, @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        if(userRepository.findByName(userToDelete) == null){
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        String decodedAuth = new String(Base64.getDecoder().decode(authHeader));
        int authSplit = decodedAuth.lastIndexOf(':');
        String username = decodedAuth.substring(0, authSplit);

        if (checkAdmin(username)) {
            userRepository.deleteByName(userToDelete);
            return new ResponseEntity<>("User deleted", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("User is not an admin", HttpStatus.UNAUTHORIZED);
        }
    }


    @PutMapping("/promote/{userToPromote}")
    public ResponseEntity<String> promoteUser(@PathVariable String userToPromote, @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        User promoteUser = userRepository.findByName(userToPromote);
        if (promoteUser == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        String decodedAuth = new String(Base64.getDecoder().decode(authHeader));
        int authSplit = decodedAuth.lastIndexOf(':');
        String username = decodedAuth.substring(0, authSplit);

        if (checkAdmin(username)) {
            if (promoteUser.getUserType() == User.UserType.ADMIN) {
                return new ResponseEntity<>("User is already an admin", HttpStatus.BAD_REQUEST);
            }
            promoteUser.setUserType(User.UserType.ADMIN);
            userRepository.save(promoteUser);
            return new ResponseEntity<>("User promoted", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User is not an admin", HttpStatus.UNAUTHORIZED);
        }
    }

        @PostMapping("/create")
        @SuppressWarnings("unchecked")
        public ResponseEntity<String> createUser(@RequestBody String userInfo, @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) throws NoSuchAlgorithmException {
            String decodedAuth = new String(Base64.getDecoder().decode(authHeader));
            int authSplit = decodedAuth.lastIndexOf(':');
            String adminUser = decodedAuth.substring(0, authSplit);

            if (checkAdmin(adminUser)) {
                // Parse the JSON body of the post request
                JSONParser parseBody = new JSONParser(userInfo);

                LinkedHashMap<Object, Object> userJSON;

                // Catch the exception if the JSON can not be parsed and return a bad request response
                try {
                    // Parse the JSON to a LinkedHashMap -- this is an unchecked cast but is necessary because of the JSON API
                    userJSON = (LinkedHashMap<Object, Object>) parseBody.parse();
                } catch (ParseException e) {
                    return new ResponseEntity<>("JSON in request body could not be parsed.", HttpStatus.BAD_REQUEST);
                }

                // Get the Username given in the request
                String username = (String) userJSON.get("username");

                //  Return 400 if the username wasn't included
                if (username == null) {
                    return new ResponseEntity<>("Could not find username in request body", HttpStatus.BAD_REQUEST);
                }

                //Confirm that the username is unqiue
                User existingUser = userRepository.findByName(username);

                // If the search found a user
                if (existingUser != null) {
                    return new ResponseEntity<>("The given username is taken.", HttpStatus.CONFLICT);
                }

                // This will only be reached if the username is allowed

                //Hash the given password
                //Get the user's password in plaintext
                String password = (String) userJSON.get("password");

                // Return 400 if the password was not included
                if (password == null) {
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
                if (email == null) {
                    return new ResponseEntity<>("Could not find email_address in request body", HttpStatus.BAD_REQUEST);
                }

                User.UserType userType = User.UserType.valueOf((String) userJSON.get("user_type"));

                //Store the hash and the salt
                // Create the new User
                User createdUser = new User(username, email, hashedPassword, salt, userType);

                userRepository.save(createdUser);

                return new ResponseEntity<>("Created new user: " + username, HttpStatus.CREATED);
            }else{
                return new ResponseEntity<>("User is not an admin", HttpStatus.UNAUTHORIZED);
            }
        }

        //TODO CREATE/DELETE GROUPS, EDIT USER GROUPS


}
