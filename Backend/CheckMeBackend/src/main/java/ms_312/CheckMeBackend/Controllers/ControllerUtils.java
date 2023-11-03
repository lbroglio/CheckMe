package ms_312.CheckMeBackend.Controllers;

import ms_312.CheckMeBackend.Messages.Message;
import ms_312.CheckMeBackend.Users.User;
import ms_312.CheckMeBackend.Users.UserRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;

/**
 * Class which stores utility Methods used by the different Controllers
 */
public class ControllerUtils {

    /**
     * Object used for comparing two {@link Message}s based on the times there were sent
     */
    public static class MessageDateComp implements Comparator<Message> {
        @Override
        public int compare(Message o1, Message o2) {
            return o1.getSendTime().compareTo(o2.getSendTime());
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
    public static Boolean checkPassword(User user, String givenPassword) throws NoSuchAlgorithmException {

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
    public static String parseBasicAuthHeader(String authHeader){
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
    public static boolean checkBasicAuth(String encodedAuth, UserRepository userRepository) throws NoSuchAlgorithmException{
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

    /**
     * Verifies if a login passed via a Base64 String (HTTP Basic Authentication) is correct.
     * Handles decoding the given String verifying if the USer exists and if the password is correct.
     *
     * @param encodedAuth A Base64 encoded string in the form of <br/>{username}:{password} --
     * <a href="https://en.wikipedia.org/wiki/Basic_access_authentication">For more Information</a>
     * @param authAs The user that the Basic Auth string is being used to authorize as. The encoded username and password
     * must match those saved for this User.
     *
     * @return
     * true if the User was correctly logged in  -- User exists, the correct password was given and they both matched
     * the user to authorize as
     * false if the login  failed for any reason
     *
     * @throws NoSuchAlgorithmException This exception indicates an invalid algorithm name was given to a
     * {@link MessageDigest} object. The algorithm in this function is hard coded and this should NEVER occur.
     */
    public static boolean checkBasicAuth(String encodedAuth, User authAs, UserRepository userRepository) throws NoSuchAlgorithmException{
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

        // Confirm that the User is the same as the that is being authorized as
        if(!authUser.equals(authAs)){
            return false;
        }

        // Return true if the given password is correct and false if it isn't
        return checkPassword(authUser, password);
    }

    /**
     * Get the {@link User} with the given username from a Base64 encoded HTTP Basic Authentication String
     * @param encodedAuth
     * @param userRepository
     * @return The {@link User} with the given username
     */
    public static User getUsername(String encodedAuth, UserRepository userRepository){
        byte[] authBytes = Base64.getDecoder().decode(encodedAuth);
        String auth = new String(authBytes);

        //Separate the Username and password
        int authSplit = auth.lastIndexOf(':');
        String username = auth.substring(0, authSplit);

        //Find the User with the given Username
        User authUser = userRepository.findByName(username);
        return authUser;
    }

}
