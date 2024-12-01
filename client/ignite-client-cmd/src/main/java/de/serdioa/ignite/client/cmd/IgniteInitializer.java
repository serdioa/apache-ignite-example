package de.serdioa.ignite.client.cmd;

import de.serdioa.ignite.test.UserIgniteCacheService;
import de.serdioa.ignite.test.UserIgniteCacheServiceTest;
import de.serdioa.ignite.test.UserRepositoryService;
import de.serdioa.ignite.test.UserRepositoryServiceTest;
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
    public UserIgniteCacheServiceTest userIgniteCacheServiceTest() {
        return new UserIgniteCacheServiceTest();
    }


    @Bean
    public UserRepositoryService userRepositoryService() {
        return new UserRepositoryService();
    }


    @Bean
    public UserRepositoryServiceTest userRepositoryServiceTest() {
        return new UserRepositoryServiceTest();
    }
}
