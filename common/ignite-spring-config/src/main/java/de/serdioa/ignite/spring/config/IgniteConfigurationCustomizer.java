package de.serdioa.ignite.spring.config;

import org.apache.ignite.configuration.IgniteConfiguration;


/**
 * Implementations of this interface may customize an Ignite configuration.
 */
@FunctionalInterface
public interface IgniteConfigurationCustomizer {

    void customize(IgniteConfiguration igniteConfiguration);
}
