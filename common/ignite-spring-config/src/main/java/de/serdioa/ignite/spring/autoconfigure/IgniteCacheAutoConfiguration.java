package de.serdioa.ignite.spring.autoconfigure;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.serdioa.ignite.spring.annotation.IgniteCache;
import de.serdioa.ignite.spring.config.AnnotationIgniteCacheRegistration;
import de.serdioa.ignite.spring.config.IgniteCacheAnnotatedEntities;
import de.serdioa.ignite.spring.config.IgniteCacheAutoConfigurationCustomizer;
import de.serdioa.ignite.spring.config.IgniteCacheProperties;
import de.serdioa.ignite.spring.config.IgniteCacheRegistration;
import de.serdioa.ignite.spring.config.IgniteCacheRegistry;
import de.serdioa.ignite.spring.config.IgniteCacheStoreFactoryConfigurationCustomizer;
import de.serdioa.ignite.spring.config.IgniteConfigurationCustomizer;
import de.serdioa.ignite.spring.config.PropertiesIgniteCacheRegistration;
import de.serdioa.ignite.spring.config.SpringIgniteCacheRegistration;
import de.serdioa.ignite.spring.config.api.CacheStoreFactoryFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScanner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;


@Slf4j
@AutoConfiguration
@AutoConfigureAfter(IgniteJpaPersistenceAutoConfiguration.class)
@ConditionalOnClass(Ignite.class)
public class IgniteCacheAutoConfiguration {

    public static final int IGNITE_CACHE_AUTO_CONFIGURAITON_ORDER = 0;
    public static final int IGNITE_CACHE_STORE_FACTORY_ORDER = 100;


    @Bean
    @ConditionalOnMissingBean(IgniteCacheAnnotatedEntities.class)
    public IgniteCacheAnnotatedEntities igniteCacheAnnotatedEntities(ApplicationContext applicationContext)
            throws ClassNotFoundException {
        final Set<Class<?>> annotatedClasses = new EntityScanner(applicationContext).scan(IgniteCache.class);

        final Map<Class<?>, IgniteCache> annotatedEntities = annotatedClasses.stream()
                .collect(Collectors.toMap(Function.identity(), c -> c.getAnnotation(IgniteCache.class)));
        return new IgniteCacheAnnotatedEntities(annotatedEntities);
    }


    @Bean
    @ConfigurationProperties("ignite.cache")
    public Map<String, IgniteCacheProperties> igniteCacheProperties() {
        return new HashMap<>();
    }


    @Bean
    public IgniteCacheRegistry igniteCacheRegistry(
            Map<String, CacheConfiguration<?, ?>> springContextCacheConfigurations,
            IgniteCacheAnnotatedEntities igniteCacheAnnotatedEntities,
            @Qualifier("igniteCacheProperties") Map<String, IgniteCacheProperties> springPropertiesCacheConfigurations) {

        final IgniteCacheRegistry igniteCacheRegistry = new IgniteCacheRegistry();

        // Register Ignite cache configurations provided directly in the Spring context.
        this.addSpringContextCacheConfigurations(springContextCacheConfigurations, igniteCacheRegistry);

        // Register Ignite cache configurations based on annotated entities.
        this.addIgniteCacheAnnotatedEntities(igniteCacheAnnotatedEntities, igniteCacheRegistry);

        // Use Spring properties to add new or amend existing Ignite cache configurations.
        this.applySpringPropertiesCacheConfigurations(springPropertiesCacheConfigurations, igniteCacheRegistry);

        return igniteCacheRegistry;
    }


    private void addSpringContextCacheConfigurations(
            Map<String, CacheConfiguration<?, ?>> springContextCacheConfigurations, IgniteCacheRegistry registry) {
        for (Map.Entry<String, CacheConfiguration<?, ?>> entry : springContextCacheConfigurations.entrySet()) {
            final String beanName = entry.getKey();
            final CacheConfiguration<?, ?> configuration = entry.getValue();

            final SpringIgniteCacheRegistration registration =
                    new SpringIgniteCacheRegistration(beanName, configuration);
            registry.add(registration);
        }
    }


    private void addIgniteCacheAnnotatedEntities(IgniteCacheAnnotatedEntities igniteCacheAnnotatedEntities,
            IgniteCacheRegistry registry) {
        for (Class<?> annotatedClass : igniteCacheAnnotatedEntities.getAnnotatedEntities()) {
            final AnnotationIgniteCacheRegistration registration = new AnnotationIgniteCacheRegistration(annotatedClass);
            registry.add(registration);
        }
    }


    private void applySpringPropertiesCacheConfigurations(
            Map<String, IgniteCacheProperties> springPropertiesCacheConfigurations, IgniteCacheRegistry registry) {
        for (Map.Entry<String, IgniteCacheProperties> entry : springPropertiesCacheConfigurations.entrySet()) {
            final String cacheName = entry.getKey();
            final IgniteCacheProperties cacheProperties = entry.getValue();

            IgniteCacheRegistration registration = registry.get(cacheName);
            if (registration == null) {
                registration = new PropertiesIgniteCacheRegistration(cacheName);
                registry.add(registration);
            }

            registration.apply(cacheProperties);
        }
    }


    @Bean
    @Order(IGNITE_CACHE_AUTO_CONFIGURAITON_ORDER)
    public IgniteConfigurationCustomizer igniteCacheAutoConfigurationCustomizer(IgniteCacheRegistry registry) {
        return new IgniteCacheAutoConfigurationCustomizer(registry);
    }


    @Bean
    @Order(IGNITE_CACHE_STORE_FACTORY_ORDER)
    @ConditionalOnBean(CacheStoreFactoryFactory.class)
    public IgniteConfigurationCustomizer igniteCacheFactoryBeanAutoConfigurationCustomizer(
            CacheStoreFactoryFactory defaultCacheStoreFactoryFactory,
            Map<String, CacheStoreFactoryFactory> cacheStoreFactoryFactories,
            IgniteCacheRegistry registry) {
        return new IgniteCacheStoreFactoryConfigurationCustomizer(defaultCacheStoreFactoryFactory,
                cacheStoreFactoryFactories, registry);
    }
}
