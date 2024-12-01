package de.serdioa.ignite.thinclient.producer;

import de.serdioa.ignite.test.UserIgniteCacheService;
import de.serdioa.ignite.test.UserIgniteClientService;
import de.serdioa.ignite.test.UserPublisher;
import de.serdioa.ignite.test.UserRepositoryService;
import org.apache.ignite.configuration.ClientConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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


    @Bean
    public UserIgniteClientService userIgniteClientService() {
        return new UserIgniteClientService();
    }


    @Bean
    public UserRepositoryService userRepositoryService() {
        return new UserRepositoryService();
    }


    @Bean
    @ConditionalOnProperty(prefix = "user.publisher.ignite", name = "enabled")
    @ConfigurationProperties("user.publisher.ignite")
    public UserPublisher igniteUserPublisher(UserIgniteClientService userService) {
        return new UserPublisher(userService);
    }


    @Bean
    @ConditionalOnProperty(prefix = "user.publisher.jpa", name = "enabled")
    @ConfigurationProperties("user.publisher.jpa")
    public UserPublisher jpaUserPublisher(UserRepositoryService userService) {
        return new UserPublisher(userService);
    }
}
