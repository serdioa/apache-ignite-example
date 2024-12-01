package de.serdioa.ignite.spring.autoconfigure;

import java.util.List;

import de.serdioa.ignite.spring.config.IgniteConfigurationCustomizer;
import de.serdioa.ignite.spring.config.IgniteFactoryBean;
import de.serdioa.ignite.spring.config.IgniteLoggerCustomizer;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.configuration.ClientConnectorConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.plugin.PluginProvider;
import org.apache.ignite.spi.communication.CommunicationSpi;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.spi.discovery.DiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.TcpDiscoveryIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;


@Slf4j
@AutoConfiguration
@AutoConfigureAfter(IgniteJpaPersistenceAutoConfiguration.class)
@ConditionalOnClass(Ignite.class)
public class IgniteNodeAutoConfiguration {

    private static final String DEFAULT_INSTANCE_NAME = "<undefined>";


    @Bean
    @ConditionalOnMissingBean(ClientConnectorConfiguration.class)
    public ClientConnectorConfiguration igniteClientConnectorConfiguration() {
        return new ClientConnectorConfiguration();
    }


    @Bean
    @ConditionalOnMissingBean(CommunicationSpi.class)
    public TcpCommunicationSpi igniteCommunicationSpi() {
        return new TcpCommunicationSpi();
    }


    @Bean
    @ConditionalOnMissingBean(TcpDiscoveryIpFinder.class)
    public TcpDiscoveryVmIpFinder igniteDiscoveryIpFinder() {
        return new TcpDiscoveryVmIpFinder();
    }


    @Bean
    @ConditionalOnMissingBean(DiscoverySpi.class)
    public TcpDiscoverySpi igniteDiscoverySpi(TcpDiscoveryIpFinder igniteDiscoveryIpFinder) {
        TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();
        discoverySpi.setIpFinder(igniteDiscoveryIpFinder);

        return discoverySpi;
    }


    @Bean
    public IgniteConfigurationCustomizer igniteLoggerCustomizer() {
        return new IgniteLoggerCustomizer();
    }


    @Bean
    @ConditionalOnMissingBean(IgniteConfiguration.class)
    @ConfigurationProperties("ignite.node")
    public IgniteConfiguration igniteConfiguration(
            ClientConnectorConfiguration clientConnectorConfiguration,
            CommunicationSpi<?> igniteCommunicationSpi,
            DiscoverySpi igniteDiscoverySpi,
            List<PluginProvider<?>> ignitePluginProviders,
            List<IgniteConfigurationCustomizer> igniteConfigurationCustomizers) {
        IgniteConfiguration configuration = new IgniteConfiguration();

        configuration.setClientConnectorConfiguration(clientConnectorConfiguration);
        configuration.setCommunicationSpi(igniteCommunicationSpi);
        configuration.setDiscoverySpi(igniteDiscoverySpi);
        configuration.setPluginProviders(ignitePluginProviders.toArray(new PluginProvider<?>[0]));

        this.customize(configuration, igniteConfigurationCustomizers);

        return configuration;
    }


    private void customize(IgniteConfiguration configuration,
            List<IgniteConfigurationCustomizer> customizers) {
        final String igniteInstanceName = this.getIgniteInstanceName(configuration);

        customizers.forEach(c -> {
            log.info("Applying customizer {} to Ignite configuration for instance {}",
                    c.getClass().getName(), igniteInstanceName);
            c.customize(configuration);
            log.info("Applied customizer {} to Ignite configuration for instance {}",
                    c.getClass().getName(), igniteInstanceName);
        });
    }


    private String getIgniteInstanceName(final IgniteConfiguration configuration) {
        String configuredInstanceName = configuration.getIgniteInstanceName();
        return (configuredInstanceName != null ? configuredInstanceName : DEFAULT_INSTANCE_NAME);
    }


    @Bean
    @ConditionalOnMissingBean(Ignite.class)
    public IgniteFactoryBean ignite(IgniteConfiguration igniteConfiguration) {
        return new IgniteFactoryBean(igniteConfiguration);
    }
}
