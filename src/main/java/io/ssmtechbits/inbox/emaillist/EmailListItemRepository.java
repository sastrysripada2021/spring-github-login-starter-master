package io.ssmtechbits.inbox.emaillist;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;

public interface EmailListItemRepository extends CassandraRepository<EmailListItemEntity, EmailListItemKey> {
 
// List<EmailListItem> findAllById(EmailListItemKey id);

 List<EmailListItemEntity> findAllByKey_UserIdAndKey_Label(String id, String label); 

}
