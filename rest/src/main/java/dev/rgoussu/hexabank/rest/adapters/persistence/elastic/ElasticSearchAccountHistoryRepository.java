package dev.rgoussu.hexabank.rest.adapters.persistence.elastic;

import dev.rgoussu.hexabank.rest.adapters.persistence.model.AccountOperationSummaryRecord;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * An elasticsearch repository for the account operations history.
 */
@Repository
public interface ElasticSearchAccountHistoryRepository
    extends ElasticsearchRepository<AccountOperationSummaryRecord, String> {

  Page<AccountOperationSummaryRecord> findByAccountId(String accountId, Pageable page);

}
