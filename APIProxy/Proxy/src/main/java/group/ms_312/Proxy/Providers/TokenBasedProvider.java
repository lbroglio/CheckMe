package group.ms_312.Proxy.Providers;

import group.ms_312.Proxy.Resources.Bimap;
import jakarta.persistence.*;

import java.security.SecureRandom;
import java.util.Map;

@Entity
public class TokenBasedProvider extends MessageProvider{

    /**
     * Store the bearer tokens for users by associating them with the username
     */
    @ElementCollection
    @CollectionTable(name = "token_username_mapping",
            joinColumns = {@JoinColumn(name = "tokenbasedeprovider_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "token")
    @Column(name = "username")
    private Map<Integer, String> tokenMapping;

    /**
     * Primary Constructor for creating a new TokenBasedProvider
     * The ID of a TokenBasedProvider is hardcoded to be 101
     */
    public TokenBasedProvider() {
        super(0x4368616F730AL);
        tokenMapping = new Bimap<>();
    }

    /**
     * Confirm whether a given token is valid
     *
     * @param token The token to check
     *
     * @return True
     */
    public boolean authenticate(int token){
        return tokenMapping.containsKey(token);
    }

    /**
     * Add a new User to have Messages stored by this provider
     *
     * @param username The username of the user to add
     * @return The 16 digit bearer token generated for the User
     */
    public int addUser(String username){
        SecureRandom secRand = new SecureRandom();

        // generate a random bearer token
        int token = 0;
        // Add 16 digits (128 bit)
        for(int i=0; i <  16; i++){
            //For each new digit add it to the number multiplied to the correct place
            int newDigit = secRand.nextInt(0, 10);
            token += ((i * 10) * newDigit);
        }

        // Add the new token and user mapping
        tokenMapping.put(token, username);

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
    public int getTokenForUser(String username){
        return ((Bimap<Integer,String>) tokenMapping).getKey(username);
    }

    /**
     * Return the Username associated with a specific token
     *
     * @param token The token for the desired user
     *
     * @return The String username associated with the given token
     */
    public String getUsernameFromToken(int token){
        return tokenMapping.get(token);
    }


}
