package ms_312.CheckMeBackend.Messages;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Message findByID(int ID);

    Message findByRecipient(String recipient);

    @Transactional
    void deleteByID(int ID);
}
