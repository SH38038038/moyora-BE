package com.project.moyora.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.nio.file.Paths;

/*
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.project.moyora.app.repository")
public class ElasticSearchConfig extends ElasticsearchConfiguration {

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo("localhost:9200")
                .usingSsl()
                .withBasicAuth("elastic", "elastic")
                .build();
    }
}
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.project.moyora.app.repository")
public class ElasticSearchConfig extends ElasticsearchConfiguration {

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo("localhost:9200")
                .withBasicAuth("elastic", "HK=hyQ7woLtjqVbSNlov")
                .build();
    }
}
