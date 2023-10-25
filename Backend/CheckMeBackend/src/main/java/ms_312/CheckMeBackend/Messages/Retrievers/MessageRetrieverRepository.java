package ms_312.CheckMeBackend.Messages.Retrievers;

import ms_312.CheckMeBackend.Messages.Message;
import ms_312.CheckMeBackend.Messages.Retrievers.MessageRetriever;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interface used by the JPA for persisting MessageRetriever objects
 */
public interface MessageRetrieverRepository extends JpaRepository<MessageRetriever, Long> {
    MessageRetriever findById(int ID);

}
