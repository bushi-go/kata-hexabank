package dev.rgoussu.hexabank.rest.adapters.persistence.elastic.config;

import dev.rgoussu.hexabank.rest.adapters.persistence.elastic.ElasticSearchAccountHistoryRepository;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * Configuration for the Elastic search integration with spring boot.
 */
@EnableElasticsearchRepositories(basePackageClasses = {ElasticSearchAccountHistoryRepository.class})
public class ElasticConfig {
}
