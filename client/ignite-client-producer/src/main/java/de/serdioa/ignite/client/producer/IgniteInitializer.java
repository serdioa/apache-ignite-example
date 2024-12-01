package de.serdioa.ignite.client.producer;

import java.util.List;

import de.serdioa.ignite.test.UserIgniteCacheService;
import de.serdioa.ignite.test.UserPublisher;
import de.serdioa.ignite.test.UserRepositoryService;
import de.serdioa.ignite.test.UserService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@Import({de.serdioa.ignite.spring.autoconfigure.IgniteNodeAutoConfiguration.class,
    de.serdioa.ignite.springdata.IgniteRepositoryConfiguration.class})
public class IgniteInitializer {

    @Bean
    public UserIgniteCacheService userIgniteCacheService() {
        return new UserIgniteCacheService();
    }


    @Bean
    public UserRepositoryService userRepositoryService() {
        return new UserRepositoryService();
    }


    @Bean
    @ConditionalOnProperty(prefix = "user.publisher.ignite", name = "enabled")
    @ConfigurationProperties("user.publisher.ignite")
    public UserPublisher igniteUserPublisher(UserIgniteCacheService userService) {
        return new UserPublisher(userService);
    }


    @Bean
    @ConditionalOnProperty(prefix = "user.publisher.jpa", name = "enabled")
    @ConfigurationProperties("user.publisher.jpa")
    public UserPublisher jpaUserPublisher(UserRepositoryService userService) {
        return new UserPublisher(userService);
    }
}
