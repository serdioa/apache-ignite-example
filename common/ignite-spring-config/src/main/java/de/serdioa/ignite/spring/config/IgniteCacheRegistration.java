package de.serdioa.ignite.spring.config;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import de.serdioa.ignite.spring.annotation.LoadCacheMode;
import javax.persistence.Id;
import lombok.Getter;
import org.apache.ignite.configuration.CacheConfiguration;


/**
 * A registered configuration of an Ignite cache. Registrations are used during initialization of Spring Boot to collect
 * configurations of Ignite caches from various sources, and resolve possible conflicts.
 */
public abstract class IgniteCacheRegistration {

    @Getter
    protected String cacheStoreFactoryFactoryBean;

    @Getter
    private LoadCacheMode loadCacheMode = LoadCacheMode.DEFAULT;


    public abstract String getName();


    public abstract Class<?> getValueType();


    public abstract String getDescription();


    public abstract CacheConfiguration<?, ?> getConfiguration();


    public void apply(IgniteCacheProperties properties) {
        final CacheConfiguration<?, ?> configuration = this.getConfiguration();

        if (properties.getAtomicityMode() != null) {
            configuration.setAtomicityMode(properties.getAtomicityMode());
        }
        if (properties.getBackups() != null) {
            configuration.setBackups(properties.getBackups());
        }
        if (properties.getCacheMode() != null) {
            configuration.setCacheMode(properties.getCacheMode());
        }
        if (properties.getDataRegionName() != null) {
            configuration.setDataRegionName(properties.getDataRegionName());
        }
        if (properties.getGroupName() != null) {
            configuration.setGroupName(properties.getGroupName());
        }
        if (properties.getMaxConcurrentAsyncOperations() != null) {
            configuration.setMaxConcurrentAsyncOperations(properties.getMaxConcurrentAsyncOperations());
        }
        if (properties.getMaxQueryIteratorsCount() != null) {
            configuration.setMaxQueryIteratorsCount(properties.getMaxQueryIteratorsCount());
        }
        if (properties.getPartitionLossPolicy() != null) {
            configuration.setPartitionLossPolicy(properties.getPartitionLossPolicy());
        }
        if (properties.getQueryParallelism() != null) {
            configuration.setQueryParallelism(properties.getQueryParallelism());
        }
        if (properties.getRebalanceMode() != null) {
            configuration.setRebalanceMode(properties.getRebalanceMode());
        }
        if (properties.getRebalanceOrder() != null) {
            configuration.setRebalanceOrder(properties.getRebalanceOrder());
        }

        final Boolean resolvedReadThrough = this.resolveReadWriteThrough(
                properties.getReadThrough(), properties.getExternalPersistency());
        if (resolvedReadThrough != null) {
            configuration.setReadThrough(resolvedReadThrough);
        }

        final Boolean resolvedWriteThrough = this.resolveReadWriteThrough(
                properties.getWriteThrough(), properties.getExternalPersistency());
        if (resolvedWriteThrough != null) {
            configuration.setWriteThrough(resolvedWriteThrough);
        }

        if (properties.getCacheStoreFactoryFactoryBean() != null) {
            this.cacheStoreFactoryFactoryBean = properties.getCacheStoreFactoryFactoryBean();
        }
        if (properties.getLoadCache() != null) {
            this.loadCacheMode = properties.getLoadCache();
        }

        this.configureTypes(properties, configuration);
    }


    private Boolean resolveReadWriteThrough(Boolean readWriteThrough, Boolean externalPersistency) {
        if (readWriteThrough != null && externalPersistency != null) {
            return readWriteThrough || externalPersistency;
        } else if (readWriteThrough != null) {
            return readWriteThrough;
        } else {
            return externalPersistency;
        }
    }


    private <K, V> void configureTypes(IgniteCacheProperties properties, CacheConfiguration<K, V> configuration) {
        // Types set in properties.
        final Class<?> propertiesKeyType = properties.getKeyType();
        final Class<?> propertiesValueType = properties.getValueType();

        // Types already configured in the configuration.
        final Class<V> configurationValueType = configuration.getValueType();
        final Class<K> configurationKeyType = configuration.getKeyType();

        // Resolve the value type: either from properties (higher priority), or already configured one.
        final Class<V> resolvedValueType =
                (Class<V>) (propertiesValueType != null ? propertiesValueType : configurationValueType);

        // Resolve the key type.
        final Class<K> resolvedKeyType;
        if (propertiesKeyType != null) {
            // Priority 1: key type set on properties.
            resolvedKeyType = (Class<K>) propertiesKeyType;
        } else if (configurationKeyType != null && !Object.class.equals(configurationKeyType)) {
            // Priority 2: key type already set on the configuration.
            // We ignore the case when the key type is Object, because it is a default configuraiton that means
            // that no key type has been actually set.
            resolvedKeyType = configurationKeyType;
        } else if (resolvedValueType != null) {
            // Priority 3: attempt to get the key type by analyzing the value type.
            resolvedKeyType = (Class<K>) getKeyType(resolvedValueType);
        } else {
            // Cannot determine the key type.
            resolvedKeyType = null;
        }

        // Configure the types, if they are available.
        if (resolvedKeyType != null && resolvedValueType != null) {
            configuration.setTypes(resolvedKeyType, resolvedValueType);
            configuration.setIndexedTypes(resolvedKeyType, resolvedValueType);
        }
    }


    protected static Class<?> getKeyType(Class<?> entityType) {
        // Attempt to get the type of the primary key from a method of the entity class.
        final Class<?> methodKeyType = getKeyTypeFromMethod(entityType);
        if (methodKeyType != null) {
            return methodKeyType;
        }

        // Attempt to get the type of the primary key from a field of the entity class.
        final Class<?> fieldKeyType = getKeyTypeFromField(entityType);
        if (fieldKeyType != null) {
            return fieldKeyType;
        }

        // Use recursion on the parent class, if it is available. When we get to the root of the class hierarchy,
        // that is Object.class, the parent class is not available and the recursion stops.
        final Class<?> parentType = entityType.getSuperclass();
        if (parentType != null) {
            return getKeyType(parentType);
        } else {
            // End of recursion: we have not found a primary key.
            return null;
        }
    }


    private static Class<?> getKeyTypeFromMethod(Class<?> entityType) {
        // Look up a method annotated with the JPA @Id.
        for (Method m : entityType.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Id.class)) {
                return m.getReturnType();
            }
        }

        // Cannot find JPA annotation @Id on any method.
        return null;
    }


    private static Class<?> getKeyTypeFromField(Class<?> entityType) {
        // Look up a field annotated with the JPA @Id.
        for (Field f : entityType.getDeclaredFields()) {
            if (f.isAnnotationPresent(Id.class)) {
                return f.getType();
            }
        }

        // Cannot find JPA annotation @Id on any field.
        return null;
    }
}
