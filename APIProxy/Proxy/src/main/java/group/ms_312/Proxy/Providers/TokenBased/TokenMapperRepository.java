package group.ms_312.Proxy.Providers.TokenBased;

import group.ms_312.Proxy.Messages.MessageBucket;
import group.ms_312.Proxy.Providers.AuthMapper;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenMapperRepository extends JpaRepository<MessageBucket, Long> {
    AuthMapper findByID(Long ID);
}
