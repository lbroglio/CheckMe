package ms_312.CheckMeBackend.Users;

import ms_312.CheckMeBackend.Messages.MessageRetriever;
import org.json.JSONObject;

import java.util.HashMap;

public class User {
    /**
     * The username for this user.
     */
    private String username;
    /**
     * A cryptographic hash of this account's password.
     */
    private String passwordHash;
    /**
     * JSON which represents any previously configured settings set by this user related to their account.
     */
    private JSONObject profileSettings;
    /**
     * Map storing all the of {@link MessageRetriever} objects that get the messages for the services this user
     * has configured. The Retrievers are stored associated their platform's name stored as a String.
     */
    private HashMap<String, MessageRetriever> messageRetrievers;

    /**
     * Construct a new empty user with the given username and password hash.
     *
     * @param username A string storing the username to identify this account by.
     * @param passwordHash A cryptographic hash of this account's password.
     */
    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    /**
     * @return The username for this account.
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return A cryptographic hash of this account's password
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * @return Any configured preferences/settings this user has set stored as a JSON  String.
     */
    public JSONObject getProfileSettings() {
        return profileSettings;
    }
}
