package de.serdioa.ignite.security;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.internal.GridKernalContext;
import org.apache.ignite.internal.IgniteNodeAttributes;
import org.apache.ignite.internal.processors.GridProcessorAdapter;
import org.apache.ignite.internal.processors.security.GridSecurityProcessor;
import org.apache.ignite.internal.processors.security.SecurityContext;
import org.apache.ignite.internal.util.lang.GridFunc;
import org.apache.ignite.plugin.security.AuthenticationContext;
import org.apache.ignite.plugin.security.SecurityCredentials;
import org.apache.ignite.plugin.security.SecurityException;
import org.apache.ignite.plugin.security.SecurityPermission;
import org.apache.ignite.plugin.security.SecuritySubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

// https://dzone.com/articles/how-to-secure-apache-ignite-from-scratch
// https://aamargajbhiye.medium.com/how-to-secure-apache-ignite-cluster-cd595b99ec5e

// org.apache.ignite.internal.processors.authentication.IgniteAuthenticationProcessor
@Slf4j
@Builder
public class DefaultGridSecurityProcessor extends GridProcessorAdapter implements GridSecurityProcessor {

    // Name of the Ignite cache to store client sessions.
    // private static final String IGNITE_CLIENT_SESSION_CACHE_NAME = "ignite-session";
    private final SecurityPluginConfiguration configuration;

    private final IgniteNodeAuthenticator nodeAuthenticator;

    private final IgniteClientAuthenticator clientAuthenticator;

    private final LocalSecuritySubjectService localSecuritySubjectService;
    private final DelegatingSecuritySubjectService delegatingSecuritySubjectService;


    private DefaultGridSecurityProcessor(GridKernalContext ctx, SecurityPluginConfiguration configuration,
            IgniteNodeAuthenticator nodeAuthenticator, IgniteClientAuthenticator clientAuthenticator) {
        super(ctx);

        Assert.notNull(configuration, "configuration cannot be null");
        Assert.notNull(nodeAuthenticator, "nodeAuthenticator cannot be null");
        Assert.notNull(clientAuthenticator, "clientAuthenticator cannot be null");

        this.configuration = configuration;
        this.nodeAuthenticator = nodeAuthenticator;
        this.clientAuthenticator = clientAuthenticator;
        this.localSecuritySubjectService = new LocalSecuritySubjectService();
        this.delegatingSecuritySubjectService = new DelegatingSecuritySubjectService(
                this.localSecuritySubjectService);
    }


    private SecurityCredentials getLocalNodeCredentials() {
        return this.configuration.getLocalNodeCredentials();
    }


    @Override
    public void start() throws IgniteCheckedException {
        SecurityCredentials localNodeCredentials = this.getLocalNodeCredentials();
        log.info("Starting GridSecurityProcessor: localNode={}, login={}", this.ctx.localNodeId(),
                localNodeCredentials);

        // Configure credentials used by this node when it connects to the Ignite cluster.
        ctx.addNodeAttribute(IgniteNodeAttributes.ATTR_SECURITY_CREDENTIALS, localNodeCredentials);

        super.start();

        log.info("Started GridSecurityProcessor");
    }


    @Override
    public void stop(boolean cancel) throws IgniteCheckedException {
        log.info("Stopping GridSecurityProcessor");

        super.stop(cancel);

        log.info("Stopped GridSecurityProcessor");
    }


    @Override
    @Deprecated
    public boolean enabled() {
        // Security is enabled. This method is deprecated, but we have to provide it to implement the interface.
        return true;
    }


    @Override
    public boolean isGlobalNodeAuthentication() {
        // When a new node joins the Ignite cluster, only coordinator node authenticates the new done.
        return false;
    }


    @Override
    public SecurityContext authenticateNode(ClusterNode node, SecurityCredentials cred) throws IgniteCheckedException {
        // This node authenticates remote node which attempts to join the cluster.

        final UUID remoteNodeId = node.id();
        final String nodeType = (node.isClient() ? "client" : "server");
        final InetSocketAddress address = new InetSocketAddress(GridFunc.first(node.addresses()), 0);
        final String login = (String) cred.getLogin();

        log.info("Authenticating node joining the cluster: localNode={}, remoteNode={},"
                + " type={}, address={}, login={}",
                this.ctx.localNodeId(), remoteNodeId, nodeType, address, login);

        final SecuritySubject securitySubject;
        try {
            securitySubject = this.nodeAuthenticator.authenticate(node, cred);
        } catch (Exception ex) {
            log.error("Exception when authenticating node joining the cluster: localNode={}, remoteNode={}, "
                    + "type={}, address={}, login={}",
                    this.ctx.localNodeId(), remoteNodeId, nodeType, address, login, ex);
            return null;
        }

        if (securitySubject != null) {
            log.info("Authenticated node joining the cluster: localNode={}, remoteNode={},"
                    + " type={}, address={}, login={}, subject={}",
                    this.ctx.localNodeId(), remoteNodeId, nodeType, address, login, securitySubject);

            // Save the authenticated session in the cache.
            this.localSecuritySubjectService.put(remoteNodeId, securitySubject);
//            if (!Objects.equals(this.ctx.localNodeId(), remoteNodeId)) {
//                this.syncIgniteSecuritySubjectService.put(remoteNodeId, securitySubject);
//            }

            return new DefaultSecurityContext(securitySubject);
        } else {
            log.info("Rejected authentication of a node joining the cluster: localNode={}, remoteNode={},"
                    + " type={}, address={}, login={}",
                    this.ctx.localNodeId(), remoteNodeId, nodeType, address, login);
            return null;
        }
    }


    @Override
    public SecurityContext authenticate(AuthenticationContext ctx) throws IgniteCheckedException {
        // Authenticate client connecting to the Ignite cluster.

        final UUID subjectId = ctx.subjectId();
        final InetSocketAddress address = ctx.address();
        final String login = (String) ctx.credentials().getLogin();

        log.info("Authenticating thin client: localNode={}, subjectId={}, address={}, login={}",
                this.ctx.localNodeId(), subjectId, address, login);

        final SecuritySubject securitySubject;
        try {
            securitySubject = this.clientAuthenticator.authenticate(ctx);
        } catch (Exception ex) {
            log.error("Exception when authenticating thin client: localNode={}, subjectId={}, address={}, login={}",
                    this.ctx.localNodeId(), subjectId, address, login, ex);
            return null;
        }

        if (securitySubject != null) {
            log.info("Authenticated thin client: localNode={}, subjectId={}, address={}, login={}, subject={}",
                    this.ctx.localNodeId(), subjectId, address, login, securitySubject);
            // Save the authenticated session in the cache.
            this.localSecuritySubjectService.put(subjectId, securitySubject);

            return new DefaultSecurityContext(securitySubject);
        } else {
            log.info("Rejected authentication of a thin client: localNode={}, subjectId={}, address={}, login={}",
                    this.ctx.localNodeId(), subjectId, address, login);
            return null;
        }
    }


    @Override
    public void onSessionExpired(UUID subjId) {
        // A session with the specified UUID disconnected from the Ignite cluster.

        log.info("Session expired, subjId={}", subjId);

        // Remove the disconnected session from the cache.
        if (this.delegatingSecuritySubjectService.remove(subjId)) {
            log.info("Session expired, removed session with subjId={}", subjId);
        } else {
            log.info("Session expired, cannot find session with subjId={} to remove", subjId);
        }
    }


    @Override
    public SecuritySubject authenticatedSubject(UUID subjId) {
        // Return security subject of the session with the specified UUID, or null if none is found.

        log.debug("Looking up authenticated subject, subjId={}", subjId);

        final SecuritySubject securitySubject = this.delegatingSecuritySubjectService.get(subjId);

        log.debug("Found authenticated subject for subjId={}: subject={}", subjId, securitySubject);

        return securitySubject;
    }


    @Override
    public Collection<SecuritySubject> authenticatedSubjects() {
        // Return security subjects of all client sessions connected to this Ignite cluster.

        log.debug("Looking up all authenticated subjects");

        final Collection<SecuritySubject> securitySubjects = this.delegatingSecuritySubjectService.getAll();

        log.debug("Found authenticated subjects: {}", securitySubjects);

        return securitySubjects;
    }


    @Override
    public SecurityContext securityContext(UUID subjId) {
        log.debug("Looking up security context, subjId={}", subjId);

        final SecuritySubject securitySubject = this.authenticatedSubject(subjId);
        final SecurityContext securityContext =
                (securitySubject != null ? new DefaultSecurityContext(securitySubject) : null);

        log.debug("Found security context for subjId={}: {}", subjId, securityContext);
        return null;
    }


    @Override
    public void authorize(String name, SecurityPermission perm, SecurityContext securityCtx) throws SecurityException {
        log.debug("Authorizing operation: perm={}, name={}, subject={}",
                perm, name, (securityCtx != null ? securityCtx.subject() : null));

        if (securityCtx == null) {
            log.trace("Security subject is null, authorization failed: perm={}, name={}", perm, name);
            throw new SecurityException("Authorization failed: perm=" + perm + ", name=" + name + ", subject=null");
        }

        if (((DefaultSecurityContext) securityCtx).operationAllowed(name, perm)) {
            log.trace("Authorization granted: perm={}, name={}, subject={}",
                    perm, name, securityCtx.subject());
        } else {
            log.trace("Authorization failed: perm={}, name={}, subject={}",
                    perm, name, securityCtx.subject());
            throw new SecurityException("Authorization failed: perm=" + perm + ", name=" + name
                    + ", subject=" + securityCtx.subject());
        }
    }


    // Customizes Lombok-generated builder class. Variables "configuration", "nodeAuthenticator"
    // and "clientAuthenticator" and related setters are generated by Lombok.
    public static class DefaultGridSecurityProcessorBuilder {

        private GridKernalContext context;


        public DefaultGridSecurityProcessorBuilder context(GridKernalContext context) {
            this.context = context;
            return this;
        }


        public DefaultGridSecurityProcessor build() {
            return new DefaultGridSecurityProcessor(this.context, this.configuration,
                    this.nodeAuthenticator, this.clientAuthenticator);
        }
    }
}
