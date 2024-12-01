package de.serdioa.ignite.persistence.jpa;

import javax.cache.configuration.Factory;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.cache.store.CacheStore;
import org.springframework.util.Assert;


/**
 * Factory for an Ignite cache store that accesses persistent data over JPA.
 *
 * @param <K> type of keys in the Ignite cache store created by this factory.
 * @param <V> type of values in the Ignite cache store created by this factory.
 */
@Slf4j
public class JpaCacheStoreFactory<K, V> implements Factory<CacheStore<K, V>> {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 2735308003870932006L;

    private final Class<V> cachedType;


    public JpaCacheStoreFactory(final Class<V> cachedType) {
        Assert.notNull(cachedType, "cachedType cannot be null");

        log.info("Creating JpaCacheStoreFactory for {}", cachedType.getName());

        this.cachedType = cachedType;
    }


    @Override
    public JpaCacheStore<K, V> create() {
        log.info("Creating JpaCacheStore for {}", this.cachedType.getName());

        return new JpaCacheStore<>(this.cachedType);
    }
}
