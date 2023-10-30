package group.ms_312.Proxy.Providers;

import group.ms_312.Proxy.Users.UserAcnt;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * A MessageProvider which stands in for a Message service which authenticates a user by taking an <a href="https://en.wikipedia.org/wiki/Basic_access_authentication">HTTP Basic Authentication</a>
 * style string sent via any method.
 */
@Entity
public class BasicAuthProvider extends MessageProvider{
    /**
     * Converts an array of bytes to a string storing its entries as two hexits. Used to store hashes and normal strings
     * in the same container.
     *
     * @param toStore The byte array to put into string form
     *
     * @return The string representation of the passed array
     */
    private static String storeByteArray(byte[] toStore){

        // Go through the array and build a string
        StringBuilder buildStrRep = new StringBuilder();
        for (byte b : toStore) {
            String toAdd = String.format("%02x", b);
            buildStrRep.append(toAdd);
        }

        return buildStrRep.toString();
    }


    /**
     * Stores the salt for user passwords associated with the Username
     */
    @ElementCollection
    private Map<String, byte[]> saltTable;

    /**
     * Primary Constructor for creating a new BasicAuthProvider
     * The ID of a BasicAuthProvider is passed as a parameter
     *
     * @param id The long id to identify this Provider with
     */
    public BasicAuthProvider(long id) {
        super(id);
        saltTable = new HashMap<>();
    }

    /**
     * Private Default Constructor used by JPA
     */
    private BasicAuthProvider(){
        super();
        saltTable = new HashMap<>();

    }

    @Override
    public boolean authenticate(String username, String authString) {
        // Hash the AuthString
        // Retrieve the salt for this User
        byte[] salt = saltTable.get(username);

        // Get a MessageDigest object for SHA-512
        MessageDigest digest;
        try{
            digest = MessageDigest.getInstance("SHA-512");
        }
        //Because the algorithm is hardcoded this should never occur
        catch (NoSuchAlgorithmException e){
            return false;
        }

        //Add the salt to the digest object
        digest.update(salt);

        //Hash the auth string
        byte[] hashedAuth = digest.digest(authString.getBytes(StandardCharsets.UTF_8));

        //Compare the string representation of the Hash to the one for the user to authenticate as and return the result
        UserAcnt toAuth = acntMap.get(username);
        return toAuth.getAuthString().equals(storeByteArray(hashedAuth));
    }

    /**
     * Add a new User to have Messages stored by this provider
     *
     * @param accountInfo A map containing the information needed to create an account with this provider.<br/>
     * Must contain the following field(s) { <pre>
     *  - username: The username for the user to create
     *  - password: The password for authenticating with the created user
     * <br/>}
     * </pre>
     *
     * @return The 16 digit bearer token generated for the User
     */
    @Override
    public String addUser(Map<String, String> accountInfo) {
        // Get the username and password from accountInfo
        String username = accountInfo.get("username");
        String password = accountInfo.get("password");

        //Throw an exception if one the required fields is missing
        if(username == null || password==null){
            throw new IllegalArgumentException("Parameter - accountInfo is missing a required field");
        }

        //Construct the authString  -- username and password encoded in Base64 (Same format as HttP Basic Auth)
        String unencodedAuthString = username + ":" + password;
        //Encode the auth string in Base64
        String authString = Base64.getEncoder().encodeToString(unencodedAuthString.getBytes());


        // Hash the AuthString
        // Generate a salt
        byte[] salt = new byte[8];
        SecureRandom secRan = new SecureRandom();
        secRan.nextBytes(salt);


        // Get a MessageDigest object for SHA-512
        MessageDigest digest;
        try{
            digest = MessageDigest.getInstance("SHA-512");
        }
        //Because the algorithm is hardcoded this should never occur
        catch (NoSuchAlgorithmException e){
            return "";
        }
        //Store the salt in the salt table
        saltTable.put(username, salt);

        //Add the salt to the digest object
        digest.update(salt);

        // Hash the auth string
        byte[] hashedAuth = digest.digest(authString.getBytes());

        //Convert the Hash to a string representation for storage
        String hashAuthStr = storeByteArray(hashedAuth);

        // Add the new user
        acntMap.put(username, new UserAcnt(username, hashAuthStr));

        //Return the auth string
        return authString;

    }
}
