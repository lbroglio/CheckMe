package group.ms_312.Proxy.Messages;


import group.ms_312.Proxy.Messages.MessageBucket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageBucketRepository extends JpaRepository<MessageBucket, Long> {
    MessageBucket findByID(Long ID);

}

