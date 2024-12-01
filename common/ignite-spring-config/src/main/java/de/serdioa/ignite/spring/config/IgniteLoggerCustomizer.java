package de.serdioa.ignite.spring.config;

import org.apache.ignite.configuration.IgniteConfiguration;


public class IgniteLoggerCustomizer implements IgniteConfigurationCustomizer {

    @Override
    public void customize(IgniteConfiguration igniteConfiguration) {
        igniteConfiguration.setGridLogger(new org.apache.ignite.logger.slf4j.Slf4jLogger());
    }
}
