package ms_312.CheckMeBackend.Users;

import jakarta.persistence.*;
import ms_312.CheckMeBackend.Messages.DemoRetriever;
import ms_312.CheckMeBackend.Messages.MessageRetriever;
import ms_312.CheckMeBackend.Messages.PlatformName;

import java.util.ArrayList;
import java.util.HashMap;
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
    private String passwordHash;
    /**
     * JSON which represents any previously configured settings set by this user related to their account.
     */
    private String profileSettings;

    /**
     * Map storing all the of {@link MessageRetriever} objects that get the messages for the services this user
     * has configured. The Retrievers are stored associated with their platform's name stored as a String.
     */
    @OneToMany//(cascade = CascadeType.ALL)
    private List<MessageRetriever> messageRetrievers;

    /**
     * Construct a new empty user with the given username and password hash.
     *
     * @param username A string storing the username to identify this account by.
     * @param passwordHash A cryptographic hash of this account's password.
     */
    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
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
    public String getPasswordHash() {
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
        MessageRetriever temp = new DemoRetriever(APIEndpoint);
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

}
