package ms_312.CheckMeBackend.Users;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import ms_312.CheckMeBackend.Messages.Retrievers.DemoRetriever;
import ms_312.CheckMeBackend.Messages.Retrievers.MessageRetriever;
import ms_312.CheckMeBackend.Messages.Message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * An Abstract class for an object that owns {@link MessageRetriever}s that provide it with {@link Message}s
 */
@Entity
public abstract class RetrieverOwner {
    /**
     * The assigned name of this RetrieverOwner. Used as the ID for the persistence API
     */
    @Id
    @Schema(type = "string", example = "Name")
    private String name;

    /**
     * List storing all the of {@link MessageRetriever} objects that get the messages for the services this configured
     * for this retriever owner
     */
    @OneToMany(cascade = CascadeType.ALL)
    private List<MessageRetriever> messageSources;

    /**
     * Default constructor used by the persistence API
     */
    protected RetrieverOwner(String name){
        this.name = name;
        messageSources = new ArrayList<>();
    }

    /**
     * Default constructor used by the persistence API
     */
    protected RetrieverOwner(){
        messageSources = new ArrayList<>();
    }


    /**
     * Adds a new {@link MessageRetriever} to get messages for this User.
     *
     * @param toAdd The Message Retriever to add as a source. Must be a Retriever owned by this RetrieverOwner
     */
    //@param platformName Which platform (Gmail, Discord, ETC) the new retriever gets messages from
    public void newMessageSource(MessageRetriever toAdd){
        if(!toAdd.getOwner().equals(this)){
            throw new IllegalArgumentException("Given MessageRetriever is not owned by this RetrieverOwner");
        }
        messageSources.add(toAdd);
    }

    /**
     * @return A list of the {@link MessageRetriever}s this RetrieverOwner uses to get Messages
     */
    public List<MessageRetriever> getMessageRetrievers() {
        return messageSources;
    }

    /**
     * Function to update the List of MessageRetrievers used by the persistence API
     *
     * @param messageRetrievers The {@link List} of {@link MessageRetriever]s holding this users retrievers
     */
    private void setMessageRetrievers(List<MessageRetriever> messageRetrievers) {
        this.messageSources = messageRetrievers;
    }

    /**
     * @return The name of this RetrieverOwners
     */
    public String getName() {
        return name;
    }

    /**
     * Removes all items stored in the MessageSources list. Leaves this RetrieverOwner with no Retrievers
     *
     */
    public void clearRetrievers(){
        Iterator<MessageRetriever> retrieverIterator = messageSources.iterator();
        while (retrieverIterator.hasNext()){
            retrieverIterator.next();
            retrieverIterator.remove();
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RetrieverOwner that)) return false;

        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
