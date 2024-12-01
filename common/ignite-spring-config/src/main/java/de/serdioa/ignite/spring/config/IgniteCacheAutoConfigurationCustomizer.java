package de.serdioa.ignite.spring.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.util.Assert;


@Slf4j
public class IgniteCacheAutoConfigurationCustomizer implements IgniteConfigurationCustomizer {

    private final IgniteCacheRegistry registry;


    public IgniteCacheAutoConfigurationCustomizer(final IgniteCacheRegistry registry) {
        Assert.notNull(registry, "registry is required");

        this.registry = registry;
    }


    @Override
    public void customize(IgniteConfiguration igniteConfiguration) {
        // The IgniteConfiguration interface does not contain methods for adding cache configurations.
        // We have to extract existing configurations (if any), enrich them from the registry, and save back.
        final Map<String, CacheConfiguration<?, ?>> cacheConfigurations =
                new HashMap<>(this.getCacheConfigurations(igniteConfiguration));

        for (IgniteCacheRegistration registration : registry.getAll()) {
            final String name = registration.getName();
            if (cacheConfigurations.containsKey(name)) {
                // The registry prevent multiple cache configurations with the same name. If we have encountered
                // a duplicate, it was already in the IgniteConfiguration.
                log.warn("IgniteConfiguration in Spring context already contains configuration of the "
                        + "Ignite cache '{}'. Ignoring configuration of the Ignite cache '{}' registered by {}.",
                        name, name, registration.getDescription());
            } else {
                log.info("Auto-configuring Ignite cache '{}' registered by {}", name,
                        registration.getDescription());
                final CacheConfiguration<?, ?> cacheConfiguration = registration.getConfiguration();
                cacheConfigurations.put(name, cacheConfiguration);
            }
        }

        this.setCacheConfigurations(cacheConfigurations, igniteConfiguration);
    }


    private Map<String, CacheConfiguration<?, ?>> getCacheConfigurations(IgniteConfiguration configuration) {
        final CacheConfiguration<?, ?>[] cacheConfigurationsArray = configuration.getCacheConfiguration();
        if (cacheConfigurationsArray == null) {
            return Collections.emptyMap();
        } else {
            return Arrays.stream(cacheConfigurationsArray)
                    .collect(Collectors.toMap(CacheConfiguration::getName, Function.identity()));
        }
    }


    private void setCacheConfigurations(Map<String, CacheConfiguration<?, ?>> cacheConfigurations,
            IgniteConfiguration configuration) {
        CacheConfiguration<?, ?>[] cacheConfigurationsArray =
                cacheConfigurations.values().toArray(new CacheConfiguration<?, ?>[0]);
        configuration.setCacheConfiguration(cacheConfigurationsArray);
    }
}
