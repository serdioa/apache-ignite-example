package de.serdioa.ignite.spring.config;

import java.util.Objects;

import de.serdioa.ignite.spring.annotation.IgniteCache;
import de.serdioa.ignite.spring.annotation.LoadCacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.util.StringUtils;


/**
 * Configuration of an Ignite cache registered by applying an annotation {@link IgniteCache} on an entity class.
 */
public class AnnotationIgniteCacheRegistration extends IgniteCacheRegistration {

    private final Class<?> annotatedClass;
    private final IgniteCache annotation;
    private final CacheConfiguration<?, ?> cacheConfiguration;


    public AnnotationIgniteCacheRegistration(final Class<?> annotatedClass) {
        this.annotatedClass = Objects.requireNonNull(annotatedClass);

        this.annotation = this.annotatedClass.getAnnotation(IgniteCache.class);
        if (this.annotation == null) {
            throw new IllegalArgumentException("Class '%s' does not contain annotation '%s'".formatted(
                    this.annotatedClass.getClass().getName(), IgniteCache.class.getName()));
        }

        // Attempt to find the type of the primary key.
        final Class<?> keyType = getKeyType(this.annotatedClass);

        this.cacheConfiguration = this.buildCacheConfiguration(this.annotatedClass, keyType, this.annotation);
    }


    private <K, V> CacheConfiguration<K, V> buildCacheConfiguration(Class<V> annotatedClass, Class<K> keyType,
            IgniteCache annotation) {
        final String cacheName = this.buildCacheName(annotatedClass, annotation);

        CacheConfiguration<K, V> configuration = new CacheConfiguration<>();

        configuration.setName(cacheName);
        configuration.setAtomicityMode(annotation.atomicityMode());
        if (annotation.backups() >= 0) {
            configuration.setBackups(annotation.backups());
        }
        configuration.setCacheMode(annotation.cacheMode());
        if (StringUtils.hasText(annotation.dataRegionName())) {
            configuration.setDataRegionName(annotation.dataRegionName());
        }
        if (StringUtils.hasText(annotation.groupName())) {
            configuration.setGroupName(annotation.groupName());
        }
        if (annotation.maxConcurrentAsyncOperations() > 0) {
            configuration.setMaxConcurrentAsyncOperations(annotation.maxConcurrentAsyncOperations());
        }
        if (annotation.maxQueryIteratorsCount() > 0) {
            configuration.setMaxQueryIteratorsCount(annotation.maxQueryIteratorsCount());
        }
        configuration.setPartitionLossPolicy(annotation.partitionLossPolicy());
        if (annotation.queryParallelism() > 0) {
            configuration.setQueryParallelism(annotation.queryParallelism());
        }
        configuration.setRebalanceMode(annotation.rebalanceMode());
        if (annotation.rebalanceOrder() > Integer.MIN_VALUE) {
            configuration.setRebalanceOrder(annotation.rebalanceOrder());
        }
        configuration.setReadThrough(annotation.readThrough());
        configuration.setWriteThrough(annotation.writeThrough());
        // Shortcut for readThrough and writeThrough.
        if (annotation.externalPersistency()) {
            configuration.setReadThrough(true);
            configuration.setWriteThrough(true);
        }

        // Set the key and value types, if the key type could be determined.
        final Class<K> resolvedKeyType = this.resolveKeyType(keyType, annotation);
        if (resolvedKeyType != null) {
            configuration.setTypes(resolvedKeyType, annotatedClass);
            configuration.setIndexedTypes(resolvedKeyType, annotatedClass);
        }

        if (StringUtils.hasText(annotation.cacheStoreFactoryFactoryBean())) {
            this.cacheStoreFactoryFactoryBean = annotation.cacheStoreFactoryFactoryBean();
        }

        return configuration;
    }


    private String buildCacheName(Class<?> annotatedClass, IgniteCache annotation) {
        if (StringUtils.hasText(annotation.name())) {
            return annotation.name();
        } else {
            return annotatedClass.getSimpleName();
        }
    }


    private <K> Class<K> resolveKeyType(Class<?> keyType, IgniteCache annotation) {
        // The key type from the annotation takes a precedence.
        // If not set, the annotation key type defaults to Void.class, so we ignore this type.
        final Class<?> annotationKeyType = annotation.keyType();
        if (annotationKeyType != null && !Void.class.equals(annotationKeyType)) {
            return (Class<K>) annotationKeyType;
        } else {
            return (Class<K>) keyType;
        }
    }


    @Override
    public String getName() {
        return this.cacheConfiguration.getName();
    }


    @Override
    public Class<?> getValueType() {
        return this.annotatedClass;
    }


    @Override
    public CacheConfiguration<?, ?> getConfiguration() {
        return this.cacheConfiguration;
    }


    @Override
    public String getDescription() {
        return "annotated class '%s'".formatted(this.annotatedClass.getName());
    }


    @Override
    public LoadCacheMode getLoadCacheMode() {
        // The "super" method may return an overwritten value from Spring properties.
        final LoadCacheMode overwriteLoadCacheMode = super.getLoadCacheMode();
        return (overwriteLoadCacheMode != null ? overwriteLoadCacheMode : this.annotation.loadCache());
    }
}
