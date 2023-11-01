package group.ms_312.Proxy.Users;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAcnt, Long> {
    UserAcnt findByID(long id);
}
