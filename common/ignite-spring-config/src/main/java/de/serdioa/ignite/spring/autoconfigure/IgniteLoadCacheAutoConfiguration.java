package de.serdioa.ignite.spring.autoconfigure;

import de.serdioa.ignite.spring.config.IgniteCacheRegistry;
import de.serdioa.ignite.spring.config.IgniteCustomizer;
import de.serdioa.ignite.spring.config.IgniteLoadCacheCustomizer;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;


@Slf4j
@AutoConfiguration
@AutoConfigureAfter(IgniteCacheAutoConfiguration.class)
@AutoConfigureBefore(IgniteNodeAutoConfiguration.class)
@ConditionalOnClass(Ignite.class)
public class IgniteLoadCacheAutoConfiguration {

    @Bean
    public IgniteCustomizer igniteLoadCacheCustomizer(final IgniteCacheRegistry registry) {
        return new IgniteLoadCacheCustomizer(registry);
    }
}
