package de.serdioa.ignite.security;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.internal.GridKernalContext;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.internal.processors.security.GridSecurityProcessor;
import org.apache.ignite.plugin.CachePluginContext;
import org.apache.ignite.plugin.CachePluginProvider;
import org.apache.ignite.plugin.ExtensionRegistry;
import org.apache.ignite.plugin.IgnitePlugin;
import org.apache.ignite.plugin.PluginContext;
import org.apache.ignite.plugin.PluginProvider;
import org.apache.ignite.plugin.PluginValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@AllArgsConstructor
@Builder
public class SecurityPluginProvider implements PluginProvider<SecurityPluginConfiguration> {

    private final SecurityPlugin securityPlugin = new SecurityPlugin();

    private final SecurityPluginConfiguration config;

    private final IgniteNodeAuthenticator nodeAuthenticator;

    private final IgniteClientAuthenticator clientAuthenticator;


    @Override
    public String name() {
        return "SecurityPlugin";
    }


    @Override
    public String version() {
        return "1.0";
    }


    @Override
    public String copyright() {
        return "Public Domain";
    }


    @Override
    public <T extends IgnitePlugin> T plugin() {
        return (T) this.securityPlugin;
    }


    @Override
    public <T> T createComponent(PluginContext ctx, Class<T> cls) {
        if (cls.isAssignableFrom(GridSecurityProcessor.class)) {
            GridKernalContext gridKernalContext = ((IgniteEx) ctx.grid()).context();

            return (T) DefaultGridSecurityProcessor.builder()
                    .context(gridKernalContext)
                    .configuration(this.config)
                    .nodeAuthenticator(this.nodeAuthenticator)
                    .clientAuthenticator(this.clientAuthenticator)
                    .build();
        } else {
            return null;
        }
    }


    @Override
    public void initExtensions(PluginContext ctx, ExtensionRegistry registry) throws IgniteCheckedException {
    }


    @Override
    public CachePluginProvider<?> createCacheProvider(CachePluginContext ctx) {
        return null;
    }


    @Override
    public void start(PluginContext ctx) throws IgniteCheckedException {
    }


    @Override
    public void stop(boolean cancel) throws IgniteCheckedException {
    }


    @Override
    public void onIgniteStart() throws IgniteCheckedException {
    }


    @Override
    public void onIgniteStop(boolean cancel) {
    }


    @Override
    public Serializable provideDiscoveryData(UUID nodeId) {
        return null;
    }


    @Override
    public void receiveDiscoveryData(UUID nodeId, Serializable data) {
    }


    @Override
    @Deprecated
    public void validateNewNode(ClusterNode node) throws PluginValidationException {
    }
}
