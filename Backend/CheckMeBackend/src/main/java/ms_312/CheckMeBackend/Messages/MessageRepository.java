package ms_312.CheckMeBackend.Messages;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Message findById(int id);

    @Transactional
    void deleteById(int id);
}
