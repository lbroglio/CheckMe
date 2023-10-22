package group.ms_312.Proxy.Providers;

import group.ms_312.Proxy.Providers.Storage.AuthMapper;
import jakarta.persistence.*;


import java.security.SecureRandom;

@Entity
public class TokenBasedProvider extends MessageProvider {



    /**
     * Primary Constructor for creating a new TokenBasedProvider
     * The ID of a TokenBasedProvider is hardcoded to be 0x4368616F730A
     */
    public TokenBasedProvider() {
        super(0x4368616F730AL);
    }


    /**
     * Confirm whether a given token is valid
     *
     * @param token The token to check
     *
     * @return True
     */
    public boolean authenticate(long token){
        return tokenMapping.containsKey(Long.toString(token));
    }

    /**
     * Add a new User to have Messages stored by this provider
     *
     * @param username The username of the user to add
     * @return The 16 digit bearer token generated for the User
     */
    public long addUser(String username){
        SecureRandom secRand = new SecureRandom();

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

        // Add the new token and user mapping
        tokenMapping.put(Long.toString(token), username);

        // Return the token, so it can be given to the user
        return token;
    }

    /**
     * Return the bearer token issued to a specific user based on username
     * Used for development because this is a proxy application
     *
     * @param username The username to get the token for
     *
     * @return The 16 digit bearer token for the given user
     */
    public long getTokenForUser(String username){
        return  Long.parseLong(tokenMapping.getKey(username));
    }

    /**
     * Return the Username associated with a specific token
     *
     * @param token The token for the desired user
     *
     * @return The String username associated with the given token
     */
    public String getUsernameFromToken(long token){
        return tokenMapping.get(Long.toString(token));
    }

    /**
     * Check if a given token exists for this Provider
     *
     * @param token To token to check if exists
     *
     * @return
     * true -  if the token exists
     * false - if the token doesn't exist
     */
    public boolean tokenExists(long token){
        return tokenMapping.containsKey(Long.toString(token));
    }


}
