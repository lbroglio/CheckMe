package ms_312.CheckMeBackend.Users;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface used by the JPA for persisting {@link Group} objects
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByName(String name);

    User findByEmail(String email);

    @Transactional
    void deleteByName(String name);
}

