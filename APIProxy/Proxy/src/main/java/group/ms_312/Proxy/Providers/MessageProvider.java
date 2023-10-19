package group.ms_312.Proxy.Providers;

import group.ms_312.Proxy.Messages.Message;
import group.ms_312.Proxy.Messages.MessageBucket;
import group.ms_312.Proxy.Resources.Sorting;
import jakarta.persistence.*;

import java.util.*;

@Entity
public abstract class MessageProvider {
    @Id
    private long ID;

    @ElementCollection
    @CollectionTable(name = "provider_bucket_Mapping",
            joinColumns = {@JoinColumn(name = "messageprovider_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "username")
    @Column(name = "message_bucket")
    @OneToMany(cascade = CascadeType.ALL)
    protected Map<String, MessageBucket> messagesByUser;

    /**
     * Object to compare two Messages based on date
     */
    private static class MessageDateComparator implements Comparator<Message>{
        /**
         * Compare two messages based on date.
         *
         * @param m1 The first message to compare
         * @param m2 The message to compare the first two
         *
         * @return
         * -1 - if m1 was sent before  m2
         * 0 - if m1 and m2 were sent at the same time
         * 1 - if m2 was sent before m1
         */
        public int compare(Message m1, Message m2){
            if (m1.getSendTime().isBefore(m2.getSendTime())){
                return m1.getSendTime().isEqual(m2.getSendTime()) ? 0 : -1;
            }
            else{
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
         *
         * @return
         * -1  - if m1 has a sender lexicographically before m2
         * 0 - if m1 and m2 have equal senders
         * 1 - if m2 has a sender lexicographically before m1
         */
        public int compare(Message m1, Message m2){
            return m1.getSender().compareTo(m2.getSender());
        }
    }



    /**
     * Default constructor for MessageProvider used by JPA
     */
    private MessageProvider(){
        messagesByUser = new HashMap<>();
    }

    /**
     * Primary Constructor includes passage of id
     *
     * @param id The long link to use when persisting this Provider in the JPA
     */
    protected MessageProvider(long id){
        this.ID = id;
        messagesByUser = new HashMap<>();
    }

    /**
     * Get all the messages stored for a given Username
     *
     * @param username The string username to get messages for
     *
     * @return An array of {@link Message} objects containing the requested messages --
     * Not sorted in any particular order
     */
    public Message[] getAllMessagesForUser(String username){
        int listSize = messagesByUser.get(username).size();
        return messagesByUser.get(username).toArray(new Message[listSize]);
    }

    /**
     * Get all the messages stored for a specific user in an indicated order.
     *
     * @param username The string username to get messages for
     * @param order {@link MessageOrdering} enum indicating what order the returned messages for by sorted by
     *
     * @return An array of {@link Message} objects containing the requested messages --
     * Sorted in the order indicated by the order parameter
     */
    public Message[] getAllMessagesForUser(String username, MessageOrdering order){
        // Get the list of Messages
        int listSize = messagesByUser.get(username).size();
        Message[] messageList = messagesByUser.get(username).toArray(new Message[listSize]);

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
     * Add a Message to be provided by this provided object
     * @param toLoad The Message to add
     * @param username The user to serve the message to
     */
    public void loadMessage(Message toLoad, String username){
        // If there is no List for this user's Messages add one
        this.messagesByUser.computeIfAbsent(username, k -> new MessageBucket());

        // Add this Message to the list for the User
        this.messagesByUser.get(username).add(toLoad);
    }

}
