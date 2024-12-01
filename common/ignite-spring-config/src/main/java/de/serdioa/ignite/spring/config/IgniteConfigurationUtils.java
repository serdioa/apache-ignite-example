package de.serdioa.ignite.spring.config;

import javax.cache.configuration.Factory;
import org.apache.ignite.cache.store.CacheStoreSessionListener;
import org.apache.ignite.configuration.IgniteConfiguration;


/**
 * Helper methods for manipulating Apache Ignite configuration.
 */
public class IgniteConfigurationUtils {

    private IgniteConfigurationUtils() {
        // The private constructor prevents this class from being instantiated.
    }


    public static void addCacheStoreSessionListenerFactory(IgniteConfiguration configuration,
            Factory<CacheStoreSessionListener> listener) {

        final Factory<CacheStoreSessionListener>[] existingListeners =
                configuration.getCacheStoreSessionListenerFactories();

        // Allocate an array large enough to hold all existing listener, and the new one.
        final int length = (existingListeners == null ? 1 : existingListeners.length + 1);
        @SuppressWarnings({"rawtypes", "unchecked"})
        final Factory<CacheStoreSessionListener>[] newListeners = new Factory[length];

        // Copy all existing listeners into the new array, and add the new listener.
        if (length > 1) {
            System.arraycopy(existingListeners, 0, newListeners, 0, length - 1);
        }
        newListeners[length - 1] = listener;

        configuration.setCacheStoreSessionListenerFactories(newListeners);
    }
}
