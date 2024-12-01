package de.serdioa.ignite.spring.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.serdioa.ignite.spring.annotation.IgniteCache;


/**
 * Collection of entities annotated to configure Ignite caches.
 */
public class IgniteCacheAnnotatedEntities {

    private final Map<Class<?>, IgniteCache> annotatedEntities;


    public IgniteCacheAnnotatedEntities(Map<Class<?>, IgniteCache> annotatedEntities) {
        this.annotatedEntities = Collections.unmodifiableMap(new HashMap<>(annotatedEntities));
    }


    public Set<Class<?>> getAnnotatedEntities() {
        return this.annotatedEntities.keySet();
    }


    public boolean isAnnotatedEntity(Class<?> type) {
        return this.annotatedEntities.containsKey(type);
    }


    public IgniteCache getEntityAnnotation(Class<?> type) {
        return this.annotatedEntities.get(type);
    }
}
