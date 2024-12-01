package de.serdioa.ignite.spring.config.api;

import javax.cache.configuration.Factory;
import org.apache.ignite.cache.store.CacheStore;


/**
 * Factory for creating Ignite cache store factories.
 */
public interface CacheStoreFactoryFactory {

    /**
     * Constructs and returns a factory that in turn constructs and returns instances of {@link CacheStore} for Ignite
     * cache containing instances of the specified type.
     *
     * @param <K> the type of keys in the Ignite cache store to be created by the factory returned by this method.
     * @param <V> the type of objects in the Ignite cache store to be created by the factory returned by this method.
     *
     * @param cachedType the type of objects in the Ignite cache store to be created by the factory returned by this
     * method.
     *
     * @return a factory that constructs and returns instances of {@link CacheStore} for Ignite cache containing
     * instances of the specified type.
     */
    <K, V> Factory<CacheStore<K, V>> create(Class<V> cachedType);
}
