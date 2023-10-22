package group.ms_312.Proxy.Providers.TokenBased;

import group.ms_312.Proxy.Messages.MessageBucket;
import group.ms_312.Proxy.Providers.TokenBased.TokenMapper;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenMapperRepository extends JpaRepository<MessageBucket, Long> {
    TokenMapper findByID(Long ID);
}
