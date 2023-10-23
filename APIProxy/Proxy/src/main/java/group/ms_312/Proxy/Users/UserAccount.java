package group.ms_312.Proxy.Users;

import group.ms_312.Proxy.Messages.Message;
import group.ms_312.Proxy.Providers.MessageProvider;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a User account made with one of the simulated services. Owns Messages and tracks its auth string
 */
@Entity
public class UserAccount {
    /**
     * Automatically generated id used by the JPA
     */
    @Id
    private String  username;

    /**
     * The string assigned for authentication with this User account. What exactly this consists of depends on the type
     * of Provider this is owned by
     */
    private String authString;


    @OneToMany(cascade = CascadeType.ALL)
    private List<Message> messages;

    @ManyToOne
    private MessageProvider owner;

    /**
     * Create a new UserAccount
     *
     * @param username The username to use as the id for this USer (Must be unique)
     * @param authString The string assigned for authentication with this User account. What exactly this consists
     * of depends on the type of Provider this account owned by
     */
    public UserAccount(String username, String authString){
        this.username = username;
        this.authString = authString;
        messages = new ArrayList<>();

    }

    /**
     * Empty Constructor used by the JPA
     */
    private UserAccount(){
        messages = new ArrayList<>();
    }

    /**
     * @return The username used as the ID for this UserAccount
     */
    public  String getUsername(){
        return username;
    }


    /**
     * @return The List of Messages stored for this user
     */
    public List<Message> getMessages() {
        return messages;
    }

    /**
     * Set the List of Messages for this user to a new List
     *
     * @param messages The List to set as the list of messages for this user
     */
    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    /**
     * @return The String assigned for authentication with this user
     */
    public String getAuthString(){
        return authString;
    }


}
