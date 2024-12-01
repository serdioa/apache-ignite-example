package de.serdioa.ignite.server;

import de.serdioa.ignite.spring.config.IgniteCustomizer;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class IgniteInitializer {

    @Bean
    public IgniteCustomizer testIgniteCustomizer() {
        return ignite -> {
            IgniteConfiguration config = ignite.configuration();

            System.out.println("===== Cache Configurations =====");
            final CacheConfiguration<?, ?>[] cacheConfigurations = config.getCacheConfiguration();
            if (cacheConfigurations != null) {
                for (CacheConfiguration<?, ?> cacheConfig : cacheConfigurations) {
                    System.out.println("!!! " + cacheConfig);
                }
            }
            System.out.println("================================");
        };
    }
}
