package ms_312.CheckMeBackend.Users;

import jakarta.persistence.*;
import ms_312.CheckMeBackend.Messages.DemoRetriever;
import ms_312.CheckMeBackend.Messages.MessageRetriever;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="USERS")
public class User {

    /**
     * The username for this user.
     */
    @Id
    //@Column(name="username")
    private String username;

    /**
     * A cryptographic hash of this account's password.
     */
    private byte[] passwordHash;

    /**
     * Randomly generated salt applied to this user's password.
     */
    private byte[] salt;

    /**
     * JSON which represents any previously configured settings set by this user related to their account.
     */
    private String profileSettings;

    /**
     * Map storing all the of {@link MessageRetriever} objects that get the messages for the services this user
     * has configured. The Retrievers are stored associated with their platform's name stored as a String.
     */
    @OneToMany(cascade = CascadeType.ALL)
    //TODO - Integrate with "actual" (not my quick test) message serving objects
    private List<MessageRetriever> messageRetrievers;

    /**
     * Construct a new empty user with the given username and password hash.
     *
     * @param username     A string storing the username to identify this account by.
     * @param passwordHash A cryptographic hash of this account's password.
     * @param salt The salt used to hash this user's password
     */
    public User(String username, byte[] passwordHash, byte[] salt) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        messageRetrievers = new ArrayList<>();
    }

    /**
     * Default constructor used by the persistence API
     */
    private User(){
        messageRetrievers = new ArrayList<>();
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
    public byte[] getPasswordHash() {
        return passwordHash;
    }

    /**
     * @return Any configured preferences/settings this user has set stored as a JSON  String.
     */
    public String getProfileSettings() {
        return profileSettings;
    }

    /**
     * Update the saved profile settings of a user to a new JSON String
     *
     * @param profileSettings The JSON String to save as the new profile settings.
     */
    public void setProfileSettings(String profileSettings) {
        this.profileSettings = profileSettings;
    }

    /**
     * Adds a new {@link MessageRetriever} to get messages for this User.
     *
     * @param APIEndpoint Complete URL to the API endpoint to request for messages
     */
    //@param platformName Which platform (Gmail, Discord, ETC) the new retriever gets messages from
    public void newMessageSource(String APIEndpoint){
        MessageRetriever temp = new DemoRetriever(APIEndpoint, this);
        messageRetrievers.add(temp);
    }


    /**
     * Function to update the List of MessageRetrievers used by the persistence API
     *
     * @param messageRetrievers The {@link List} of {@link MessageRetriever]s holding this users retrievers
     */
    public void setMessageRetrievers(List<MessageRetriever> messageRetrievers) {
        this.messageRetrievers = messageRetrievers;
    }

    public List<MessageRetriever> getMessageRetrievers() {
        return messageRetrievers;
    }

    /**
     * @return The salt used for this user's password hash
     */
    public byte[] getSalt() {
        return salt;
    }


}
