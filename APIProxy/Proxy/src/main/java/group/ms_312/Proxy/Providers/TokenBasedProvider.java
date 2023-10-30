package group.ms_312.Proxy.Providers;

import group.ms_312.Proxy.Users.UserAcnt;
import jakarta.persistence.*;


import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

/**
 * Provider which simulates a message service which uses a Bearer Token to authorize a User. In the API this maps to the
 * Chaos Message Service
 */
@Entity
public class TokenBasedProvider extends MessageProvider {

    /**
     * Maps the Bearer Token of a User to the username
     */
    @ElementCollection
    private Map<String, String> tokenMap;

    /**
     * Primary Constructor for creating a new TokenBasedProvider
     * The ID of a TokenBasedProvider is hardcoded to be 0x4368616F730A
     */
    public TokenBasedProvider() {
        super(0x4368616F730AL);
        tokenMap = new HashMap<>();
    }


    /**
     * Add a new User to have Messages stored by this provider
     *
     * @param accountInfo A map containing the information needed to create an account with this provider.<br/>
     * Must contain the following field(s) { <pre>
     *  - username: The username for the user to create
     * <br/>}
     * </pre>
     *
     * @return The 16 digit bearer token generated for the User
     */
    @Override
    public String addUser(Map<String, String> accountInfo){
        SecureRandom secRand = new SecureRandom();

        // Get the username from the account info Map
        String username = accountInfo.get("username");

        // generate a random bearer token
        long token = 0;
        //Used to shift a number by additional places if  the previous digit was zero
        int placeShift = 0;
        // Add 16 digits (128 bit)
        for(int i=0; i <  16; i++){
            //Securely generate a new digit
            int newDigit = secRand.nextInt(0, 10);

            // If the new digit is zero indicate that the next digit should be shifted in place instead of adding a
            // digit this round
            if(newDigit == 0){
                placeShift++;
            }
            // Add the next digit
            else{
                // Calculate the number to add to insert the digit
                // - The digit is shifted i places to the left by multiply it be 10 ^ i
                // - The digit is also shifted to left placeShift places to insert any zeroes from previous rounds
                // behind it
                // - The resulting number is added to the token
                token += ((Math.pow(10, i + placeShift)) * newDigit );
                placeShift = 0;
            }
        }

        // Convert the token to a string
        String strToken = Long.toString(token);

        // Add the new user associated with its username
        acntMap.put(username, new UserAcnt(username, strToken));
        //Store the username associated with the token
        tokenMap.put(strToken, username);

        // Return the token, so it can be given to the user
        return strToken;
    }

    @Override
    public boolean authenticate(String username, String authString) {
        return tokenMap.get(authString).equals(username);
    }

    /**
     * Return the bearer token issued to a specific user based on username
     * Used for development because this is a proxy application
     *
     * @param username The username to get the token for
     *
     * @return The 16 digit bearer token for the given user
     */
    public String getTokenForUser(String username){
        return acntMap.get(username).getAuthString();
    }

    /**
     * Check if a given token exists for this Provider
     *
     * @param token The token to check if exists
     *
     * @return
     * true -  if the token exists
     * false - if the token doesn't exist
     */
    public boolean tokenExists(String token){
        return tokenMap.containsKey(token);
    }


}
