package de.serdioa.ignite.thinclient.cmd;

import de.serdioa.ignite.test.UserIgniteClientService;
import de.serdioa.ignite.test.UserIgniteClientServiceTest;
import de.serdioa.ignite.test.UserRepositoryService;
import de.serdioa.ignite.test.UserRepositoryServiceTest;
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


    @Bean
    public UserIgniteClientService userIgniteClientService() {
        return new UserIgniteClientService();
    }


    @Bean
    public UserIgniteClientServiceTest userIgniteClientServiceTest() {
        return new UserIgniteClientServiceTest();
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
