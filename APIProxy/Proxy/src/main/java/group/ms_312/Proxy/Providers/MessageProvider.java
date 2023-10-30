package group.ms_312.Proxy.Providers;

import group.ms_312.Proxy.Messages.Message;
import group.ms_312.Proxy.Resources.Sorting;
import group.ms_312.Proxy.Users.UserAcnt;
import jakarta.persistence.*;

import java.util.*;

/**
 * Abstract class for an Object which serves Messages via the API. Providers act as stand ins for
 */
@Entity
public abstract class MessageProvider {
    @Id
    private long ID;

    @ElementCollection
    @CollectionTable(name = "provider_user_Mapping",
            joinColumns = {@JoinColumn(name = "messageprovider_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "acnt_name")
    @Column(name = "user_acnt")
    @OneToMany(cascade = CascadeType.ALL)
    protected Map<String, UserAcnt> acntMap;

    /**
     * Object to compare two Messages based on date
     */
    private static class MessageDateComparator implements Comparator<Message> {
        /**
         * Compare two messages based on date.
         *
         * @param m1 The first message to compare
         * @param m2 The message to compare the first two
         * @return -1 - if m1 was sent before  m2
         * 0 - if m1 and m2 were sent at the same time
         * 1 - if m2 was sent before m1
         */
        public int compare(Message m1, Message m2) {
            if (m1.getSendTime().isBefore(m2.getSendTime())) {
                return m1.getSendTime().isEqual(m2.getSendTime()) ? 0 : -1;
            } else {
                return m1.getSendTime().isEqual(m2.getSendTime()) ? 0 : 1;
            }
        }
    }

    /**
     * Object to compare two Messages based on their sender
     */
    private static class MessageSenderComparator implements Comparator<Message> {
        /**
         * Compare two messages based on the sender.
         *
         * @param m1 The first message to compare
         * @param m2 The message to compare the first two
         * @return -1  - if m1 has a sender lexicographically before m2
         * 0 - if m1 and m2 have equal senders
         * 1 - if m2 has a sender lexicographically before m1
         */
        public int compare(Message m1, Message m2) {
            return m1.getSender().compareTo(m2.getSender());
        }
    }


    /**
     * Default constructor for MessageProvider used by JPA
     */
    protected MessageProvider() {
        acntMap = new HashMap<>();
    }

    /**
     * Primary Constructor includes passage of id
     *
     * @param id The long link to use when persisting this Provider in the JPA
     */
    protected MessageProvider(long id) {
        this.ID = id;
        acntMap = new HashMap<>();
    }



    /**
     * Abstract function to be implemented by the subclasses. Handles authenticating with this MessageProvider.
     *
     * @param username The name of the user to authenticate with
     * @param authString The string needed for authentication with this Provider
     *
     * @return
     * true - If authentication was successful <br/>
     * false - If authentication was unsuccessful
     */
    public abstract boolean authenticate(String username, String authString);

    /**
     * Abstract Method for adding a User account to this Provider.
     *
     * @param accountInfo A map containing the information needed to create an account with this provider. What fields
     * are specifically needed depends on type of provider.
     *
     * @return The auth string for the newly created user.0
     *
     */
    public abstract String addUser(Map<String, String> accountInfo);



    /**
     * Get all the messages stored for a given Username
     *
     * @param username The string username to get messages for
     * @param authString The string needed to authenticate as the given user with this provider
     *
     * @return An array of {@link Message} objects containing the requested messages --
     * Not sorted in any particular order
     * If the authentication for the user fails returns an empty array
     */
    public Message[] getAllMessagesForUser(String username, String authString){
        //
        if(!this.authenticate(username, authString)){
            return new Message[0];
        }

        List<Message> messages = acntMap.get(username).getMessages();
        int listSize = messages.size();
        return messages.toArray(new Message[listSize]);
    }

    /**
     * Get all the messages stored for a specific user in an indicated order.
     *
     * @param username The string username to get messages for
     * @param authString The string needed to authenticate as the given user with this provider
     * @param order {@link MessageOrdering} enum indicating what order the returned messages for by sorted by
     *
     * @return An array of {@link Message} objects containing the requested messages --
     * Sorted in the order indicated by the order parameter
     * If the authentication for the user fails returns an empty array
     */
    public Message[] getAllMessagesForUser(String username, String authString, MessageOrdering order){
        if(!this.authenticate(username, authString)){
            return new Message[0];
        }
        // Get the list of Messages
        List<Message> messages = acntMap.get(username).getMessages();
        int listSize = messages.size();
        Message[] messageList = messages.toArray(new Message[listSize]);

        //Sort the list based on the indicated order
       if(order == MessageOrdering.DATE){
           Sorting.qSort(messageList, 0, messageList.length -1, new MessageDateComparator());
       }
       else if(order == MessageOrdering.SENDER){
            Sorting.qSort(messageList, 0, messageList.length -1, new MessageSenderComparator());
        }

       return messageList;

    }

    /**
     * @return The numerical id assigned to this object by the JPA
     */
    public long getId(){
        return ID;
    }

    /**
     * Add a Message to be provided by this Provider object
     * @param toLoad The Message to add
     * @param username The user to serve the message to
     */
    public void loadMessage(Message toLoad, String username){
        // If there is no List for this user's Messages throw and exception
        if(!acntMap.containsKey(username)){
            throw new IllegalArgumentException("No user with the given username exists");
        }

        // Add this Message to the list for the User
        this.acntMap.get(username).getMessages().add(toLoad);
    }


    /**
     * Check if a given username has been created for this Provider
     *
     * @param username The username to check if exists
     *
     * @return
     * true - if the username has been added
     * false - if the username has not been added
     */
    public boolean userExists(String username){
        return  acntMap.containsKey(username);
    }

}
