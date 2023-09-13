package ms_312.CheckMeBackend.Messages;


/**
 * A small test version of the object used to retrieve messages for a user. Used for building out the user class.
 * Retrieves messages from a custom endpoint.
 */
public class DemoRetriever extends MessageRetriever {
    /**
     * @param source A complete URL pointing to the API location this Retriever should get Messages from
     */
    public DemoRetriever(String source) {
        super(source);
    }

    @Override
    public Message[] getAll() {
        return new Message[0];
    }
}
