package ms_312.CheckMeBackend.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, String> {
        Group findByName(String name);
        Group findByJoinCode(String joinCode);
}

