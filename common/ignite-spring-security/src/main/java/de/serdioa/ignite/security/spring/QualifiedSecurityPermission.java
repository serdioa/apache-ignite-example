package de.serdioa.ignite.security.spring;

import java.util.EnumSet;
import java.util.Set;

import lombok.Value;
import org.apache.ignite.plugin.security.SecurityPermission;
import org.springframework.util.StringUtils;


@Value
public class QualifiedSecurityPermission {

    // Security permissions with mandatory name.
    private static final Set<SecurityPermission> NAME_REQUIRED = EnumSet.of(
            SecurityPermission.CACHE_PUT,
            SecurityPermission.CACHE_READ,
            SecurityPermission.CACHE_REMOVE,
            SecurityPermission.SERVICE_CANCEL,
            SecurityPermission.SERVICE_DEPLOY,
            SecurityPermission.SERVICE_INVOKE,
            SecurityPermission.TASK_CANCEL,
            SecurityPermission.TASK_EXECUTE
    );

    // Security permissions with optional name.
    private static final Set<SecurityPermission> NAME_OPTIONAL = EnumSet.of(
            SecurityPermission.CACHE_CREATE,
            SecurityPermission.CACHE_DESTROY
    );

    // For the rest of permissions the name is not permitted.
    private final SecurityPermission permission;
    private final String name;


    public QualifiedSecurityPermission(SecurityPermission permission) {
        this(permission, null);
    }


    public QualifiedSecurityPermission(SecurityPermission permission, String name) {
        if (isNameRequired(permission)) {
            // If the specified security permission requires a mandatory name, check that the name is available.
            if (!StringUtils.hasText(name)) {
                throw new IllegalArgumentException("SecurityPermission " + permission + " requires qualified name or * for all");
            }
        } else if (isNameOptional(permission)) {
            // The name for this security permission is optional, but if the name is available, it shall not be a blank string.
            if (name != null && !StringUtils.hasText(name)) {
                throw new IllegalArgumentException("SecurityPermission " + permission + " has an optional qualified name, but the name cannot be an empty string");
            }
        } else {
            // This security permission does not expect a name.
            if (name != null) {
                throw new IllegalArgumentException("SecurityPermission " + permission + " does not expect qualified name");
            }
        }

        this.permission = permission;
        this.name = name;
    }


    private static boolean isNameRequired(SecurityPermission permission) {
        return NAME_REQUIRED.contains(permission);
    }


    private static boolean isNameOptional(SecurityPermission permission) {
        return NAME_OPTIONAL.contains(permission);
    }
}
