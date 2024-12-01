package de.serdioa.ignite.server;

import de.serdioa.ignite.test.UserReader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class TestInitializer {

    @ConditionalOnProperty(prefix = "user.ignite", name = "enabled")
    @ConfigurationProperties("user.ignite")
    @Bean
    public UserReader userConsumerIgniteService() {
        return new UserReader();
    }
}
