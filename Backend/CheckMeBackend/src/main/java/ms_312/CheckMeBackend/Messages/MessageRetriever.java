package ms_312.CheckMeBackend.Messages;

import jakarta.persistence.Entity;

/**
 * Abstract class that contains the API used by platform specific classes for retrieving messages
 * from different sources.
 */
public abstract class MessageRetriever {
    /**
     * A string containing the complete URL for the API endpoint to retrieve the Messages from
     */
    protected String source;

    /**
     * @param source A complete URL pointing to the API location this Retriever should get Messages from
     */
    public MessageRetriever(String source) {
        this.source = source;
    }

    /**
     * Get all the Messages provided by the {@link MessageRetriever#source} for this Retriever
     *
     * @return An array of {@link Message} objects corresponding to all the retrieved messages.
     */
    public abstract Message[] getAll();


}
