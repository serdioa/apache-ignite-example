package de.serdioa.ignite.spring.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.BeanInitializationException;


/**
 * Configurations of Ignite caches collected during initialization of Spring Boot.
 */
public class IgniteCacheRegistry {

    private final Map<String, IgniteCacheRegistration> igniteCacheRegistrations = new HashMap<>();


    public void add(final IgniteCacheRegistration igniteCacheRegistration) {
        final String name = igniteCacheRegistration.getName();

        final IgniteCacheRegistration existingRegistration = this.igniteCacheRegistrations.get(name);
        if (existingRegistration != null) {
            throw new BeanInitializationException(
                    "Attempting to register Ignite cache '%s' by %s, but it is already registered by %s".formatted(
                            name, igniteCacheRegistration.getDescription(), existingRegistration.getDescription()));
        }
        this.igniteCacheRegistrations.put(name, igniteCacheRegistration);
    }


    public IgniteCacheRegistration remove(final String name) {
        return this.igniteCacheRegistrations.remove(name);
    }


    public IgniteCacheRegistration get(final String name) {
        return this.igniteCacheRegistrations.get(name);
    }


    public Collection<IgniteCacheRegistration> getAll() {
        return this.igniteCacheRegistrations.values();
    }


    public Set<String> getNames() {
        return this.igniteCacheRegistrations.keySet();
    }
}
