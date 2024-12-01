package de.serdioa.ignite.thinclient.web;

import org.apache.ignite.configuration.ClientConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@Import({de.serdioa.ignite.springdata.IgniteRepositoryConfiguration.class})
public class IgniteClientInitializer {

    @Bean
    @ConfigurationProperties(prefix = "ignite.client")
    public ClientConfiguration igniteClientConfiguration() {
        ClientConfiguration cfg = new ClientConfiguration();

        return cfg;
    }
}
