package dev.rgoussu.hexabank.rest.adapters.persistence.mongo.config;

import dev.rgoussu.hexabank.rest.adapters.persistence.mongo.MongoAccountPersistenceRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Mongo configuration for the api.
 */
@Configuration
@EnableMongoRepositories(basePackageClasses = {MongoAccountPersistenceRepository.class})
public class MongoConfig {
}
