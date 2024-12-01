package de.serdioa.ignite.spring.config;

import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.util.StringUtils;


/**
 * Configuration of an Ignite cache registered by Spring properties.
 */
public class PropertiesIgniteCacheRegistration extends IgniteCacheRegistration {

    private final String name;
    private final CacheConfiguration<?, ?> cacheConfiguration = new CacheConfiguration<>();


    public PropertiesIgniteCacheRegistration(final String name) {
        // Set the cache configuration name to the provided value.
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("name cannot be null or empty");
        }
        this.name = name;
        this.cacheConfiguration.setName(name);
    }


    @Override
    public String getName() {
        return this.cacheConfiguration.getName();
    }


    @Override
    public Class<?> getValueType() {
        return null;
    }


    @Override
    public CacheConfiguration<?, ?> getConfiguration() {
        return this.cacheConfiguration;
    }


    @Override
    public String getDescription() {
        return "Spring properties for the Ignite cache '%s'".formatted(this.getName());
    }
}
