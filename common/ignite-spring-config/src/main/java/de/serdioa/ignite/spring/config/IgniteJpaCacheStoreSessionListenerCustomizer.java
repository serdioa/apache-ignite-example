package de.serdioa.ignite.spring.config;

import de.serdioa.ignite.persistence.jpa.JpaCacheStoreSessionListener;
import javax.cache.configuration.Factory;
import org.apache.ignite.cache.store.CacheStoreSessionListener;
import org.apache.ignite.configuration.IgniteConfiguration;


public class IgniteJpaCacheStoreSessionListenerCustomizer implements IgniteConfigurationCustomizer {

    @Override
    public void customize(IgniteConfiguration igniteConfiguration) {
        final Factory<CacheStoreSessionListener> jpaCacheStoreSessionListenerFactory =
                JpaCacheStoreSessionListener::new;

        IgniteConfigurationUtils.addCacheStoreSessionListenerFactory(igniteConfiguration,
                jpaCacheStoreSessionListenerFactory);
    }
}
