package ms_312.CheckMeBackend.Messages;

import java.time.LocalDateTime;

/**
 * Interface for the object that interacts with an existing message service to retrieve messages for the user.
 */
public interface MessageRetriever {
    //This will only stay as a String if that is what's best for adding into request headers
    //TODO -- Asses if this should be changed to better integrate with headers
    protected String getAccess();

    /**
     * Retrieve all the Messages from the platform that were sent after the given date
     * @param sentAfter A {@link LocalDateTime} with the time that all the messages to be retrieved form the platform were sent after.
     * @return An array of {@link Message} objects representing the messages retrieved from the platform.
     */
    public Message[] retrieveMessages(LocalDateTime sentAfter);


     /**
     * Retrieve all the Messages from the platform that were sent after a given method
     * @param sentAfter A {@link Message} that all the retrieved messages should have been sent after.
     * @return An array of {@link Message} objects representing the messages retrieved from the platform.
     * @throws IllegalArgumentException If the given message is not found by the retriever erzs
     */
    public Message[] retrieveMessages(Message sentAfter) throws IllegalArgumentException;


}
