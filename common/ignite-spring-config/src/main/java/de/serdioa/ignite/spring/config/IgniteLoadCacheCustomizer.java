package de.serdioa.ignite.spring.config;

import de.serdioa.ignite.spring.annotation.LoadCacheMode;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.util.Assert;


@Slf4j
public class IgniteLoadCacheCustomizer implements IgniteCustomizer {

    private final IgniteCacheRegistry registry;


    public IgniteLoadCacheCustomizer(final IgniteCacheRegistry registry) {
        Assert.notNull(registry, "registry is required");
        this.registry = registry;
    }


    @Override
    public void customize(Ignite ignite) {
        for (String cacheName : ignite.cacheNames()) {
            final IgniteCache<?, ?> cache = ignite.getOrCreateCache(cacheName);
            if (this.isLoadCache(cache)) {
                this.loadCache(cache);
            }
        }
    }


    private <K, V> boolean isLoadCache(final IgniteCache<K, V> cache) {
        final LoadCacheMode mode = this.getLoadCacheMode(cache);
        switch (mode) {
            case LOAD:
                return true;
            case NOT_LOAD:
                return false;
            case DEFAULT:
                return this.isReadThrough(cache);
            default:
                throw new IllegalArgumentException("Unexpected LoadCacheMode: " + mode);
        }
    }


    private LoadCacheMode getLoadCacheMode(final IgniteCache<?, ?> cache) {
        final String cacheName = cache.getName();
        final IgniteCacheRegistration cacheRegistration = this.registry.get(cacheName);
        final LoadCacheMode mode = (cacheRegistration != null ? cacheRegistration.getLoadCacheMode() : null);

        return (mode != null ? mode : LoadCacheMode.DEFAULT);
    }


    private boolean isReadThrough(final IgniteCache<?, ?> cache) {
        @SuppressWarnings("unchecked")
        final CacheConfiguration<?, ?> config = cache.getConfiguration(CacheConfiguration.class);

        return config.isReadThrough();
    }


    private void loadCache(final IgniteCache<?, ?> cache) {
        log.info("Loading Ignite cache {}...", cache.getName());
        cache.loadCache(null);
        final int size = cache.size();
        log.info("Finished loading Ignite cache {}: {} entries in the cache", cache.getName(), size);
    }
}
