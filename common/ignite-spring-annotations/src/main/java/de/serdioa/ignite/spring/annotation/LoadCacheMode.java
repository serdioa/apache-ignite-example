package de.serdioa.ignite.spring.annotation;


public enum LoadCacheMode {
    /**
     * Automatically load Apache Ignite cache during startup if the cache property read-through is {@code true}.
     */
    DEFAULT,
    /**
     * Automatically load Apache Ignite cache during startup.
     */
    LOAD,
    /**
     * Do not automatically load Apache Ignite cache during startup.
     */
    NOT_LOAD
}
