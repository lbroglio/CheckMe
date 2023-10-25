package ms_312.CheckMeBackend.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface used by the JPA for persisting Group objects
 */
@Repository
public interface GroupRepository extends JpaRepository<Group, String> {
        Group findByName(String name);
        Group findByJoinCode(String joinCode);
}

