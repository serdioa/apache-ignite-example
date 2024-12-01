package de.serdioa.ignite.spring.config;

import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.IgniteSpring;
import org.apache.ignite.cluster.ClusterState;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;


@Slf4j
public class IgniteFactoryBean implements FactoryBean<Ignite>, ApplicationContextAware {

    private static final String DEFAULT_INSTANCE_NAME = "<undefined>";

    @Setter
    private ApplicationContext applicationContext;

    // The Ignite configuration used to create the Ignite instance provided by this factory.
    private final IgniteConfiguration igniteConfiguration;

    // The Ignite instance provided by this factory.
    private Ignite ignite;

    // The unique ID of the local Ignite node.
    private UUID localNodeId;


    public IgniteFactoryBean(final IgniteConfiguration igniteConfiguration) {
        Assert.notNull(igniteConfiguration, "igniteConfiguration cannot be null");

        this.igniteConfiguration = igniteConfiguration;
        this.igniteConfiguration.setGridLogger(new org.apache.ignite.logger.slf4j.Slf4jLogger());
    }


    @PostConstruct
    public void afterPropertiesSet() throws IgniteCheckedException {
        final String igniteInstanceName = this.getIgniteInstanceName();

        log.info("Starting Ignite node for instance {}", igniteInstanceName);
        this.ignite = IgniteSpring.start(this.igniteConfiguration, this.applicationContext);
        this.localNodeId = this.ignite.cluster().localNode().id();

        log.info("Started Ignite node {} for instance {}", this.localNodeId, igniteInstanceName);

        // Apply customizers, if any.
        this.customize();

        ClusterState clusterState = this.ignite.cluster().state();
        log.info("Ignite instance {} cluster state: {}", igniteInstanceName, clusterState);
    }


    @PreDestroy
    public void destroy() {
        final String igniteInstanceName = this.getIgniteInstanceName();

        log.info("Stopping Ignite node {} for instance {}", this.localNodeId, igniteInstanceName);

        if (this.ignite != null) {
            this.ignite.close();
        }

        log.info("Stopped Ignite node {} for instance {}", this.localNodeId, igniteInstanceName);
    }


    private void customize() {
        final String igniteInstanceName = this.getIgniteInstanceName();

        ObjectProvider<IgniteCustomizer> customizers = this.applicationContext.getBeanProvider(IgniteCustomizer.class);
        customizers.forEach(c -> {
            log.info("Applying customizer {} to Ignite node {} for instance {}",
                    c.getClass().getName(), this.localNodeId, igniteInstanceName);
            c.customize(this.ignite);
            log.info("Applied customizer {} to Ignite node {} for instance {}",
                    c.getClass().getName(), this.localNodeId, igniteInstanceName);
        });
    }


    private String getIgniteInstanceName() {
        String configuredInstanceName = this.igniteConfiguration.getIgniteInstanceName();
        return (configuredInstanceName != null ? configuredInstanceName : DEFAULT_INSTANCE_NAME);
    }


    @Override
    public Ignite getObject() throws Exception {
        return this.ignite;
    }


    @Override
    public Class<?> getObjectType() {
        return Ignite.class;
    }
}
