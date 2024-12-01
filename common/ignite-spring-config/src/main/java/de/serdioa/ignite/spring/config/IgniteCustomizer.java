package de.serdioa.ignite.spring.config;

import org.apache.ignite.Ignite;


/**
 * Implementations of this interface may customize an Ignite instance.
 */
@FunctionalInterface
public interface IgniteCustomizer {

    void customize(Ignite ignite);
}
