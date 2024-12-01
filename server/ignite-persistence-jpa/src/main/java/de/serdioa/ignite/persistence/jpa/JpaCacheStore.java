package de.serdioa.ignite.persistence.jpa;

import java.util.List;
import java.util.Map;

import javax.cache.Cache;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteException;
import org.apache.ignite.cache.store.CacheStoreAdapter;
import org.apache.ignite.cache.store.CacheStoreSession;
import org.apache.ignite.lang.IgniteBiInClosure;
import org.apache.ignite.resources.CacheStoreSessionResource;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.springframework.util.Assert;


/**
 * Ignite cache store that accesses persistent data over JPA.
 *
 * @param <K> type of keys in the Ignite cache store.
 * @param <V> type of values in the Ignite cache store.
 */
@Slf4j
public class JpaCacheStore<K, V> extends CacheStoreAdapter<K, V> {

    // Key of the Hibernate hint to deactivate dirty checking for affected entities.
    private static final String HIBERNATE_READ_ONLY = "org.hibernate.readOnly";

    // JPA hints for read operations.
    private static final Map<String, Object> JPA_READ_HINTS = Map.of(
            HIBERNATE_READ_ONLY, true);

    /**
     * Auto-injected store session.
     */
    @CacheStoreSessionResource
    private CacheStoreSession session;

    /**
     * Auto injected ignite instance.
     */
    @IgniteInstanceResource
    private Ignite ignite;

    private final Class<V> cachedType;


    public JpaCacheStore(final Class<V> cachedType) {
        Assert.notNull(cachedType, "cachedType cannot be null");

        this.cachedType = cachedType;
    }


    @Override
    public void loadCache(IgniteBiInClosure<K, V> cacheConsumer, Object... args) throws CacheLoaderException {
        if (log.isDebugEnabled()) {
            log.debug("Loading Ignite cache for {}", this.cachedType.getName());
        }

        try {
            final EntityManager entityManager = this.getEntityManager();
            final CriteriaQuery<V> criteria = entityManager.getCriteriaBuilder().createQuery(this.cachedType);
            criteria.from(this.cachedType);

            final TypedQuery<V> query = entityManager.createQuery(criteria);
            for (Map.Entry<String, Object> hint : JPA_READ_HINTS.entrySet()) {
                query.setHint(hint.getKey(), hint.getValue());
            }

            final List<V> entities = query.getResultList();

            final PersistenceUnitUtil puu = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
            entities.forEach(entity -> {
                @SuppressWarnings("unchecked")
                final K key = (K) puu.getIdentifier(entity);
                cacheConsumer.apply(key, entity);
            });
        } catch (Exception ex) {
            throw new CacheLoaderException("Exception when loading Ignite cache for " + this.cachedType.getName(), ex);
        }
    }


    @Override
    public V load(K key) throws CacheLoaderException {
        if (log.isDebugEnabled()) {
            log.debug("Loading object with key '{}' in Ignite cache for {}", key, this.cachedType.getName());
        }

        try {
            final EntityManager entityManager = this.getEntityManager();
            return entityManager.find(this.cachedType, key, JPA_READ_HINTS);
        } catch (Exception ex) {
            throw new CacheLoaderException("Exception when loading object with key '" + key + "' in Ignite cache for "
                    + this.cachedType.getName(), ex);
        }
    }


    @Override
    public void write(Cache.Entry<? extends K, ? extends V> entry) throws CacheWriterException {
        if (log.isTraceEnabled()) {
            log.trace("Writing object with key '{}' in Ignite cache for {}: {}", entry.getKey(),
                    this.cachedType.getName(), entry.getValue());
        } else if (log.isDebugEnabled()) {
            log.debug("Writing object with key '{}' in Ignite cache for {}", entry.getKey(), this.cachedType.getName());
        }

        try {
            final EntityManager entityManager = this.getEntityManager();
            entityManager.merge(entry.getValue());
        } catch (Exception ex) {
            throw new CacheWriterException("Exception when persisting object with key '" + entry.getKey()
                    + "' in Ignite cache for " + this.cachedType.getName(), ex);
        }
    }


    @Override
    public void delete(Object key) throws CacheWriterException {
        if (log.isDebugEnabled()) {
            log.debug("Deleting object with key '{}' from Ignite cache for {}", key, this.cachedType.getName());
        }

        try {
            final EntityManager entityManager = this.getEntityManager();
            final V entity = entityManager.find(this.cachedType, key);
            if (entity != null) {
                entityManager.remove(entity);
            } else {
                throw new CacheWriterException(
                        "Cannot delete object with key '" + key + "' from Ignite cache for "
                        + this.cachedType.getName() + ": object is not found");
            }
        } catch (CacheWriterException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new CacheWriterException("Exception when deleting object with key '" + key
                    + "' from Ignite cache for " + this.cachedType.getName(), ex);
        }
    }


    private EntityManager getEntityManager() {
        final Object attachment = this.session.attachment();
        if (!(attachment instanceof JpaCacheStoreSessionAttachment)) {
            throw new IgniteException(
                    "Unexpected CacheStoreSession attachment: expected JpaCacheStoreSessionAttachment, found "
                    + attachment);
        }

        return ((JpaCacheStoreSessionAttachment) attachment).getEntityManager();
    }
}
