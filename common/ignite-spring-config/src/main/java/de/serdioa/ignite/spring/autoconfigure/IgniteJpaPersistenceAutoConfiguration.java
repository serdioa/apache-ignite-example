package de.serdioa.ignite.spring.autoconfigure;

import de.serdioa.ignite.persistence.jpa.JpaCacheStoreFactoryFactory;
import de.serdioa.ignite.persistence.jpa.JpaCacheStoreSessionListener;
import de.serdioa.ignite.spring.config.IgniteJpaCacheStoreSessionListenerCustomizer;
import de.serdioa.ignite.spring.config.api.CacheStoreFactoryFactory;
import org.apache.ignite.Ignite;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@AutoConfiguration
@ConditionalOnClass(Ignite.class)
public class IgniteJpaPersistenceAutoConfiguration {

    @Configuration
    @ConditionalOnClass(JpaCacheStoreFactoryFactory.class)
    public static class JpaCacheStoreFactoryFactoryConfiguration {

        @Bean
        @ConditionalOnMissingBean(CacheStoreFactoryFactory.class)
        public CacheStoreFactoryFactory jpaCacheStoreFactoryFactory() {
            return new JpaCacheStoreFactoryFactory();
        }
    }


    @Configuration
    @ConditionalOnClass(JpaCacheStoreSessionListener.class)
    public static class JpaCacheStoreSessionListenerConfiguration {

        @Bean
        @ConditionalOnMissingBean(IgniteJpaCacheStoreSessionListenerCustomizer.class)
        public IgniteJpaCacheStoreSessionListenerCustomizer jpaCacheStoreSessionListener() {
            return new IgniteJpaCacheStoreSessionListenerCustomizer();
        }
    }
}
