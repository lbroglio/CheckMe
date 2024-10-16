package ms_312.CheckMeBackend.LiveChat;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;


//TODO will be implemented in the future to store chat history

public interface ChatRepository extends JpaRepository<Chat, Long>{

    Chat findByID(int ID);

    Chat findBySender(String sender);

    @Transactional
    void deleteByID(int ID);




}
