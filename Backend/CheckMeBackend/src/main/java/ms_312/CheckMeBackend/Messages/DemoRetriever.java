package ms_312.CheckMeBackend.Messages;


import jakarta.persistence.Entity;
import ms_312.CheckMeBackend.Messages.Retrievers.MessageRetriever;
import ms_312.CheckMeBackend.Users.RetrieverOwner;

/**
 * A small test version of the object used to retrieve messages for a user. Used for building out the user class.
 * Retrieves messages from a custom endpoint.
 */
@Entity
public class DemoRetriever extends MessageRetriever {
    /**
     * @param source A complete URL pointing to the API location this Retriever should get Messages from
     * @param owner The {@link RetrieverOwner} that this retriever should get messages for
     */
    public DemoRetriever(String source, RetrieverOwner owner) {
        super(source,owner);
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
