package de.serdioa.ignite.security.spring;

import java.util.Optional;


public interface QualifiedSecurityPermissionParser {

    Optional<QualifiedSecurityPermission> parseOptional(String name);


    default QualifiedSecurityPermission parse(String name) {
        return this.parseOptional(name).orElseThrow(
                () -> new IllegalArgumentException("Cannot parse security permission '" + name + "'"));
    }
}
