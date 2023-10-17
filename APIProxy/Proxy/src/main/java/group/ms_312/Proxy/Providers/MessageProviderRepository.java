package group.ms_312.Proxy.Providers;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageProviderRepository extends JpaRepository<MessageProvider, Long> {
    MessageProvider findByID(Long ID);

}
