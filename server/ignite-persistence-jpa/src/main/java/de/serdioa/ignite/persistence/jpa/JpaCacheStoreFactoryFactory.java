package de.serdioa.ignite.persistence.jpa;

import de.serdioa.ignite.spring.config.api.CacheStoreFactoryFactory;
import javax.cache.configuration.Factory;
import org.apache.ignite.cache.store.CacheStore;


/**
 * Factory for {@link JpaCacheStoreFactory}.
 */
public class JpaCacheStoreFactoryFactory implements CacheStoreFactoryFactory {

    @Override
    public <K, V> Factory<CacheStore<K, V>> create(Class<V> cachedType) {
        return new JpaCacheStoreFactory<>(cachedType);
    }
}
