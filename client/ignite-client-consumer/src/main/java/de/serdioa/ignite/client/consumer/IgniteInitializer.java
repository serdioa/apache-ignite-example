package de.serdioa.ignite.client.consumer;

import de.serdioa.ignite.test.UserConsumerIgniteCache;
import de.serdioa.ignite.test.UserIgniteCacheService;
import de.serdioa.ignite.test.UserRepositoryService;
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
    public UserConsumerIgniteCache userConsumerIgniteCache() {
        return new UserConsumerIgniteCache();
    }
}
