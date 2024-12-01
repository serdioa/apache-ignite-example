package de.serdioa.ignite.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.plugin.security.SecuritySubject;
import org.springframework.util.Assert;


@Slf4j
public abstract class AbstractIgniteSecuritySubjectService implements SecuritySubjectService {

    // Name of the Ignite cache to store client sessions.
    private static final String IGNITE_CLIENT_SESSION_CACHE_NAME = "ignite-session";

    private final Ignite ignite;
    private final AtomicReference<IgniteCache<UUID, SecuritySubject>> cacheRef = new AtomicReference<>();


    protected AbstractIgniteSecuritySubjectService(final Ignite ignite) {
        Assert.notNull(ignite, "ignite cannot be null");
        this.ignite = ignite;
    }


    private IgniteCache<UUID, SecuritySubject> getSessionCache() {
        // First attempt: if the session cache is already set, just return it.
        IgniteCache<UUID, SecuritySubject> sessionCache = this.cacheRef.get();
        if (sessionCache != null) {
            return sessionCache;
        }

        // Get the session cache and store it in the reference.
        // It is not a problem if multiple threads will attempt to do this simultaneously: Ignite returns the same
        // cache anyway, the local cacheRef variable is just a performance optimization.
        sessionCache = this.getSessionCacheFromIgnite();
        this.cacheRef.set(sessionCache);
        return sessionCache;
    }


    private IgniteCache<UUID, SecuritySubject> getSessionCacheFromIgnite() {
        CacheConfiguration<UUID, SecuritySubject> sessionCacheConfig = new CacheConfiguration<>();
        sessionCacheConfig.setName(IGNITE_CLIENT_SESSION_CACHE_NAME);
//        sessionCacheConfig.setAtomicityMode(CacheAtomicityMode.ATOMIC);
//        sessionCacheConfig.setCacheMode(CacheMode.REPLICATED);

        return this.ignite.getOrCreateCache(sessionCacheConfig);
    }


    protected void doPut(UUID uuid, SecuritySubject securitySubject) {
        log.debug("Put SecuritySubject {}", uuid, securitySubject);

        final IgniteCache<UUID, SecuritySubject> cache = this.getSessionCache();
        cache.put(uuid, securitySubject);

        log.debug("Finished put SecuritySubject {} / {}", uuid, securitySubject);
    }


    protected boolean doRemove(UUID uuid) {
        log.debug("Remove SecuritySubject {}", uuid);

        final IgniteCache<UUID, SecuritySubject> cache = this.getSessionCache();
        final boolean removed = cache.remove(uuid);

        log.debug("Finished remove SecuritySubject {}: {}", uuid, removed);

        return removed;
    }


    protected SecuritySubject doGet(UUID uuid) {
        log.debug("Get SecuritySubject {}", uuid);

        final IgniteCache<UUID, SecuritySubject> cache = this.getSessionCache();
        final SecuritySubject securitySubject = cache.get(uuid);

        log.debug("Finished get SecuritySubject {} / {}", uuid, securitySubject);

        return securitySubject;
    }


    protected Collection<SecuritySubject> doGetAll() {
        log.debug("Get all SecuritySubjects");

        final List<SecuritySubject> securitySubjects = new ArrayList<>();

        final IgniteCache<UUID, SecuritySubject> cache = this.getSessionCache();
        final ScanQuery<UUID, SecuritySubject> findAllQuery = new ScanQuery<>();
        cache.query(findAllQuery).forEach(entry -> securitySubjects.add(entry.getValue()));

        log.debug("Finished get all SecuritySubjects: {} found", securitySubjects.size());

        return securitySubjects;
    }
}
