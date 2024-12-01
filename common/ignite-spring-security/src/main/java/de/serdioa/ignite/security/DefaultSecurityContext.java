package de.serdioa.ignite.security;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.ToString;
import org.apache.ignite.internal.processors.security.SecurityContext;
import org.apache.ignite.plugin.security.SecurityPermission;
import org.apache.ignite.plugin.security.SecuritySubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@AllArgsConstructor
@ToString
public class DefaultSecurityContext implements SecurityContext, Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -3980436630803668718L;

    private static final Logger logger = LoggerFactory.getLogger(DefaultSecurityContext.class);

    private final SecuritySubject subject;


    @Override
    public SecuritySubject subject() {
        return this.subject;
    }


    public boolean operationAllowed(String name, SecurityPermission perm) {
        switch (perm) {
            // Create and restroy cache may be cache permissions (i.e. for a particular cache),
            // or system permissions (i.e. for any cache).
            case CACHE_CREATE:
            case CACHE_DESTROY:
                return this.cacheOperationAllowed(name, perm) || this.systemOperationAllowed(perm);

            // Operations on particular caches.
            case CACHE_PUT:
            case CACHE_READ:
            case CACHE_REMOVE:
                return this.cacheOperationAllowed(name, perm);

            // Operations on Ignite services.
            case SERVICE_CANCEL:
            case SERVICE_DEPLOY:
            case SERVICE_INVOKE:
                return this.serviceOperationAllowed(name, perm);

            // Operations on Ignite tasks.
            case TASK_CANCEL:
            case TASK_EXECUTE:
                return this.taskOperationAllowed(name, perm);

            default:
                return this.systemOperationAllowed(perm);
        }
    }


    @Override
    public boolean taskOperationAllowed(String taskClsName, SecurityPermission perm) {
        logger.trace("Validating taskOperationAllowed for subject {}: taskClsName={}, permission={}",
                this.subject, taskClsName, perm);

        Map<String, Collection<SecurityPermission>> taskPermissions = this.subject.permissions().taskPermissions();
        boolean result = this.hasPermission(taskPermissions, taskClsName, perm);

        logger.trace("Validated taskOperationAllowed for subject {}: taskClsName={}, permission={}, result={}",
                this.subject, taskClsName, perm, result);

        return result;
    }


    @Override
    public boolean cacheOperationAllowed(String cacheName, SecurityPermission perm) {
        logger.trace("Validating cacheOperationAllowed for subject {}: cacheName={}, permission={}",
                this.subject, cacheName, perm);

        Map<String, Collection<SecurityPermission>> cachePermissions = this.subject.permissions().cachePermissions();
        boolean result = this.hasPermission(cachePermissions, cacheName, perm);

        logger.trace("Validated cacheOperationAllowed for subject {}: cacheName={}, permission={}, result={}",
                this.subject, cacheName, perm, result);

        return result;
    }


    @Override
    public boolean serviceOperationAllowed(String srvcName, SecurityPermission perm) {
        logger.trace("Validating serviceOperationAllowed for subject {}: srvcName={}, permission={}",
                this.subject, srvcName, perm);

        Map<String, Collection<SecurityPermission>> servicePermissions = this.subject.permissions().servicePermissions();
        boolean result = this.hasPermission(servicePermissions, srvcName, perm);

        logger.trace("Validated serviceOperationAllowed for subject {}: srvcName={}, permission={}, result={}",
                this.subject, srvcName, perm, result);

        return result;
    }


    @Override
    public boolean systemOperationAllowed(SecurityPermission perm) {
        logger.trace("Validating systemOperationAllowed for subject {}: permission={}", this.subject, perm);

        Collection<SecurityPermission> systemPermissions = this.subject.permissions().systemPermissions();
        boolean result = this.hasPermission(systemPermissions, perm);

        logger.trace("Validated systemOperationAllowed for subject {}: permission={}, result={}",
                this.subject, perm, result);

        return result;
    }


    private boolean hasPermission(Map<String, Collection<SecurityPermission>> permissions,
            String name, SecurityPermission perm) {
        // A key of the map may be an exact match, or it may contain a wildcard "*" at the end to match any characters.

        // First try for an exact match.
        Collection<SecurityPermission> exactMatch = permissions.get(name);
        if (exactMatch != null && this.hasPermission(exactMatch, perm)) {
            return true;
        }

        // Try match with a wildcard.
        for (Map.Entry<String, Collection<SecurityPermission>> entry : permissions.entrySet()) {
            if (this.isWildcardMatch(entry.getKey(), name) && this.hasPermission(entry.getValue(), perm)) {
                return true;
            }
        }

        return this.subject.permissions().defaultAllowAll();
    }


    private boolean hasPermission(Collection<SecurityPermission> permissions, SecurityPermission perm) {
        if (permissions == null) {
            return this.subject.permissions().defaultAllowAll();
        }

        return permissions.contains(perm);
    }


    // Checks if the name matches the key, allowing for a wildcard "*" at the end of the key.
    private boolean isWildcardMatch(String key, String name) {
        // Support a wildcard only at the end of the key.
        if (!key.endsWith("*")) {
            return false;
        }

        String keyPrefix = key.substring(0, key.length() - 1);
        return name.startsWith(keyPrefix);
    }
}
