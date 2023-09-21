package ms_312.CheckMeBackend.Messages;


import jakarta.persistence.Entity;

/**
 * A small test version of the object used to retrieve messages for a user. Used for building out the user class.
 * Retrieves messages from a custom endpoint.
 */
@Entity
public class DemoRetriever extends MessageRetriever {
    /**
     * @param source A complete URL pointing to the API location this Retriever should get Messages from
     */
    public DemoRetriever(String source) {
        super(source);
    }

    /**
     * Default constructor for Persistence API
     */
    public DemoRetriever (){
        super();
    }

    @Override
    public Message[] getAll() {
        return new Message[0];
    }
}
