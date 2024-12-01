package de.serdioa.ignite.spring.config;

import java.util.Objects;

import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.util.StringUtils;


/**
 * Configuration of an Ignite cache registered by a Spring bean.
 */
public class SpringIgniteCacheRegistration extends IgniteCacheRegistration {

    private final String beanName;
    private final CacheConfiguration<?, ?> cacheConfiguration;


    public SpringIgniteCacheRegistration(final String beanName, final CacheConfiguration<?, ?> cacheConfiguration) {
        if (!StringUtils.hasText(beanName)) {
            throw new IllegalArgumentException("beanName cannot be null or empty");
        }
        this.beanName = beanName;
        this.cacheConfiguration = Objects.requireNonNull(cacheConfiguration);

        // If the cache name is not defined, use the bean name.
        if (!StringUtils.hasText(this.cacheConfiguration.getName())) {
            this.cacheConfiguration.setName(this.beanName);
        }
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
        return "Spring bean '%s'".formatted(this.beanName);
    }
}
