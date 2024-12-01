package de.serdioa.ignite.security.spring;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import de.serdioa.ignite.security.DefaultSecuritySubject;
import de.serdioa.ignite.security.IgniteClientAuthenticator;
import de.serdioa.ignite.security.IgniteNodeAuthenticator;
import lombok.AllArgsConstructor;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.internal.util.lang.GridFunc;
import org.apache.ignite.plugin.security.AuthenticationContext;
import org.apache.ignite.plugin.security.SecurityCredentials;
import org.apache.ignite.plugin.security.SecurityPermission;
import org.apache.ignite.plugin.security.SecurityPermissionSet;
import org.apache.ignite.plugin.security.SecurityPermissionSetBuilder;
import org.apache.ignite.plugin.security.SecuritySubject;
import org.apache.ignite.plugin.security.SecuritySubjectType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


@AllArgsConstructor
public class IgniteAuthenticationManagerAuthenticator implements IgniteNodeAuthenticator, IgniteClientAuthenticator {

    private static final Logger logger = LoggerFactory.getLogger(IgniteAuthenticationManagerAuthenticator.class);

    private final AuthenticationManager authenticationManager;
    private final QualifiedSecurityPermissionParser securityPermissionParser;


    @Override
    public SecuritySubject authenticate(ClusterNode node, SecurityCredentials cred) {
        final UUID remoteNodeId = node.id();
        final String nodeType = (node.isClient() ? "client" : "server");
        final InetSocketAddress address = new InetSocketAddress(GridFunc.first(node.addresses()), 0);
        final Object login = (String) cred.getLogin();
        final Object password = (String) cred.getPassword();

        final UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(login, password);
        final Authentication authenticated;

        try {
            authenticated = this.authenticationManager.authenticate(authenticationToken);
        } catch (AccountStatusException | BadCredentialsException | UsernameNotFoundException ex) {
            // Common expected types of authentication failure, not related to technical issues.
            // In all these cases we do not need to log the complete stack trace.
            logger.info("Failed to authenticate node joining the cluster: remoteNode={}, type={}, address={},"
                    + " login={}, ex={}, reason={}",
                    remoteNodeId, nodeType, address, login, ex.getClass().getName(), ex.getMessage());
            return null;

            // Any other exception, such as connection errors, are propagated to the caller and are logged by it.
        }

        // Transform Spring granted authorities to Ignite security permissions.
        SecurityPermissionSet permissions = this.toSecurityPermissions(authenticated.getAuthorities());

        // Build and return Ignite Security Subject representing an authenticated user.
        return DefaultSecuritySubject.builder()
                .id(remoteNodeId)
                .login(login)
                .address(address)
                .type(SecuritySubjectType.REMOTE_NODE)
                .permissions(permissions)
                .build();
    }


    @Override
    public SecuritySubject authenticate(AuthenticationContext ctx) {
        final UUID subjectId = ctx.subjectId();
        final InetSocketAddress address = ctx.address();
        final Object login = ctx.credentials().getLogin();
        final Object password = ctx.credentials().getPassword();

        final UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(login, password);
        final Authentication authenticated;

        try {
            authenticated = this.authenticationManager.authenticate(authenticationToken);
        } catch (AccountStatusException | BadCredentialsException | UsernameNotFoundException ex) {
            // Common expected types of authentication failure, not related to technical issues.
            // In all these cases we do not need to log the complete stack trace.
            logger.info("Failed to authenticate thin client joining the cluster: subjectId={}, address={},"
                    + " login={}, ex={}, reason={}",
                    subjectId, address, login, ex.getClass().getName(), ex.getMessage());
            return null;

            // Any other exception, such as connection errors, are propagated to the caller and are logged by it.
        }

        // Transform Spring granted authorities to Ignite security permissions.
        SecurityPermissionSet permissions = this.toSecurityPermissions(authenticated.getAuthorities());

        return DefaultSecuritySubject.builder()
                .id(subjectId)
                .login(login)
                .address(address)
                .type(SecuritySubjectType.REMOTE_CLIENT)
                .permissions(permissions)
                .build();
    }


    private SecurityPermissionSet toSecurityPermissions(Collection<? extends GrantedAuthority> authorities) {
        SecurityPermissionSetBuilder builder = new SecurityPermissionSetBuilder();

        for (GrantedAuthority authority : authorities) {
            this.addSecurityPermission(authority, builder);
        }

        return builder.build();
    }


    private void addSecurityPermission(GrantedAuthority authority, SecurityPermissionSetBuilder builder) {
        String authorityStr = authority.getAuthority();

        // We expect a string representation of a GrantedAuthority, but generally speaking it is optional.
        // If we encounter an unexpected GrantedAuthority, just skip it.
        if (authorityStr == null) {
            return;
        }

        Optional<QualifiedSecurityPermission> qualifiedPermission =
                this.securityPermissionParser.parseOptional(authorityStr);

        // The parser returns an empty Optional if the string representation cannot be parsed,
        // for example if the Granted Authority is not related to Ignite.
        // Skip such cases.
        if (qualifiedPermission.isEmpty()) {
            return;
        }

        SecurityPermission permission = qualifiedPermission.get().getPermission();
        String name = qualifiedPermission.get().getName();

        // Cache permissions shall be registered as system permissions, if no cache name is specified.
        if (this.isCachePermission(permission) && name != null) {
            builder.appendCachePermissions(name, permission);
        } else if (this.isServicePermission(permission)) {
            builder.appendServicePermissions(name, permission);
        } else if (this.isTaskPermission(permission)) {
            builder.appendTaskPermissions(name, permission);
        } else {
            // System permission, or a cache permission without qualified name,
            // such as a permission to create any cache.
            builder.appendSystemPermissions(permission);
        }
    }


    private boolean isCachePermission(SecurityPermission permission) {
        return permission.name().startsWith("CACHE_");
    }


    private boolean isServicePermission(SecurityPermission permission) {
        return permission.name().startsWith("SERVICE_");
    }


    private boolean isTaskPermission(SecurityPermission permission) {
        return permission.name().startsWith("TASK_");
    }
}
