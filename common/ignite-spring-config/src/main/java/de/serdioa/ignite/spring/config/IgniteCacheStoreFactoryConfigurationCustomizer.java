package de.serdioa.ignite.spring.config;

import java.util.Map;

import de.serdioa.ignite.spring.config.api.CacheStoreFactoryFactory;
import javax.cache.configuration.Factory;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.cache.store.CacheStore;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.util.Assert;


@Slf4j
public class IgniteCacheStoreFactoryConfigurationCustomizer implements IgniteConfigurationCustomizer {

    private final CacheStoreFactoryFactory defaultCacheStoreFactoryFactory;
    private final Map<String, CacheStoreFactoryFactory> cacheStoreFactoryFactories;
    private final IgniteCacheRegistry registry;


    public IgniteCacheStoreFactoryConfigurationCustomizer(
            final CacheStoreFactoryFactory defaultCacheStoreFactoryFactory,
            final Map<String, CacheStoreFactoryFactory> cacheStoreFactoryFactories,
            final IgniteCacheRegistry registry) {
        // defaultCacheStoreFactoryFactory is optional (may be null).
        Assert.notNull(cacheStoreFactoryFactories, "cacheStoreFactoryFactories is required");
        Assert.notNull(registry, "registry is required");

        this.defaultCacheStoreFactoryFactory = defaultCacheStoreFactoryFactory;
        this.cacheStoreFactoryFactories = cacheStoreFactoryFactories;
        this.registry = registry;
    }


    @Override
    public void customize(IgniteConfiguration igniteConfiguration) {
        for (CacheConfiguration<?, ?> configuration : igniteConfiguration.getCacheConfiguration()) {
            if (!this.isExternalStorage(configuration)) {
                // This cache configuration does not use an external storage and thus does not require
                // any CacheStoreFactory to set up a connection to an external database.
                log.debug("Ignite cache '{}' does not use external storage, skipping configuring CacheStoreFactory",
                        configuration.getName());
            } else if (configuration.getCacheStoreFactory() != null) {
                // This cache configuration already has a CacheStoreFactory set up.
                log.debug(
                        "Ignite cache '{}' use an external storage, but already has a CacheStoreFactory {}' configured",
                        configuration.getName(), configuration.getCacheStoreFactory());
            } else {
                this.configureCacheStoreFactory(configuration);
            }
        }
    }


    // Is this cache configuration supports at all an external storage that requires a CacheStoreFactory?
    private boolean isExternalStorage(CacheConfiguration<?, ?> configuration) {
        return configuration.isReadThrough() || configuration.isWriteThrough();
    }


    private <K, V> void configureCacheStoreFactory(CacheConfiguration<K, V> configuration) {
        final Factory<CacheStore<K, V>> cacheStoreFactory = this.getCacheStoreFactory(configuration);
        configuration.setCacheStoreFactory(cacheStoreFactory);

        log.debug("Ignite cache '{}' use an external storage, configuring CacheStoreFactory {}'",
                configuration.getName(), cacheStoreFactory);
    }


    private <K, V> Factory<CacheStore<K, V>> getCacheStoreFactory(CacheConfiguration<K, V> configuration) {
        final CacheStoreFactoryFactory cacheStoreFactoryFactory = this.getCacheStoreFactoryFactory(configuration);
        final Class<V> valueType = this.getValueType(configuration);

        return cacheStoreFactoryFactory.create(valueType);
    }


    private CacheStoreFactoryFactory getCacheStoreFactoryFactory(CacheConfiguration<?, ?> configuration) {
        // Check if a custom CacheStoreFactoryFactory is configured for this cache, and return it if it exist.
        final CacheStoreFactoryFactory customCacheStoreFactoryFactory =
                this.getCustomCacheStoreFactoryFactory(configuration);
        if (customCacheStoreFactoryFactory != null) {
            return customCacheStoreFactoryFactory;
        }

        // Check if the default CacheStoreFactoryFactory is available, and return it if it exist.
        if (this.defaultCacheStoreFactoryFactory != null) {
            return this.defaultCacheStoreFactoryFactory;
        }

        // Cannot get a CacheStoreFactoryFactory for this cache configuration.
        throw new BeanInitializationException(
                "Ignite cache configuration '%s' use external persistence and does not configure a custom CacheStoreFactoryFactory, but no default CacheStoreFactoryFactory is available in the Spring context"
                        .formatted(configuration.getName()));
    }


    private CacheStoreFactoryFactory getCustomCacheStoreFactoryFactory(CacheConfiguration<?, ?> configuration) {
        // A custom CacheStoreFactoryFactory may be available only through the registration. If this configuration
        // is not registered (i.e. it was added to the IgniteConfiguration directly), there is no customization
        // available.
        final IgniteCacheRegistration registration = this.registry.get(configuration.getName());
        if (registration == null) {
            return null;
        }

        // Get the name of the CacheStoreFactoryFactory bean from the registration.
        final String beanName = registration.getCacheStoreFactoryFactoryBean();
        if (beanName == null) {
            // No custom CacheStoreFactoryFactory is configured for this cache.
            return null;
        }

        // Attemp to get the CacheStoreFactoryFactory bean by the name.
        final CacheStoreFactoryFactory bean = this.cacheStoreFactoryFactories.get(beanName);
        if (bean != null) {
            return bean;
        }

        throw new BeanInitializationException(
                "Ignite cache configuration '%s' requires CacheStoreFactoryFactory '%s', no Spring bean '%s' is available"
                        .formatted(configuration.getName(), beanName, beanName));
    }


    private <K, V> Class<V> getValueType(CacheConfiguration<K, V> configuration) {
        // If the cache configuration directly provides the value type, just return it.
        final Class<V> configurationValueType = configuration.getValueType();
        if (configurationValueType != null && !Object.class.equals(configurationValueType)) {
            log.trace("Ignite cache configuration '{}' provides value type {}", configuration.getName(),
                    configurationValueType.getName());
            return configurationValueType;
        }

        // Check if the value type is available in the cache configuration registry.
        final IgniteCacheRegistration registration = this.registry.get(configuration.getName());
        if (registration != null) {
            @SuppressWarnings("unchecked")
            final Class<V> registrationValueType = (Class<V>) registration.getValueType();
            if (registrationValueType != null) {
                log.trace("Ignite cache configuration '{}' is registered for value type {}", configuration.getName(),
                        registrationValueType.getName());
                return registrationValueType;
            }
        }

        throw new BeanInitializationException(
                "Ignite cache configuration '%s' use external storage, but cannot find value type of the cache to create a CacheStoreFactory"
                        .formatted(configuration.getName()));
    }
}
