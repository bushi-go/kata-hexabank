package dev.rgoussu.hexabank.rest.adapters.persistence;

import dev.rgoussu.hexabank.rest.adapters.persistence.model.AccountRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Mongo repository for account data.
 */
@Repository
public interface MongoAccountPersistenceRepository extends MongoRepository<AccountRecord, String> {
}
