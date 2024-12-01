package de.serdioa.ignite.security.spring;

import java.util.Optional;

import org.apache.ignite.plugin.security.SecurityPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;


public class DefaultQualifiedSecurityPermissionParser implements QualifiedSecurityPermissionParser {

    private static final Logger logger = LoggerFactory.getLogger(DefaultQualifiedSecurityPermissionParser.class);

    private static final String SECURITY_PERMISSION_PREFIX = "ignite.";
    private static final int SECURITY_PERMISSION_PREFIX_LENGTH = SECURITY_PERMISSION_PREFIX.length();


    @Override
    public Optional<QualifiedSecurityPermission> parseOptional(String str) {
        if (str == null) {
            // Can't parse a null name.
            return Optional.empty();
        }

        // Does the name contains the expected prefix?
        if (!str.startsWith(SECURITY_PERMISSION_PREFIX)) {
            return Optional.empty();
        }
        String strWithoutQualifier = str.substring(SECURITY_PERMISSION_PREFIX_LENGTH);

        // Does the name contains a separator before the qualifier?
        int qualifierSeparatorIndex = strWithoutQualifier.indexOf(':');
        String securityPermissionName;
        String name;
        if (qualifierSeparatorIndex < 0) {
            securityPermissionName = strWithoutQualifier;
            name = null;
        } else {
            securityPermissionName = strWithoutQualifier.substring(0, qualifierSeparatorIndex);
            name = strWithoutQualifier.substring(qualifierSeparatorIndex + 1).trim();
            if (!StringUtils.hasText(name)) {
                name = null;
            }
        }

        // Replace dashes ('-') with underscores ('_'), and transform to the upper case.
        securityPermissionName = securityPermissionName.replace('-', '_').toUpperCase();

        try {
            SecurityPermission permission = SecurityPermission.valueOf(securityPermissionName);
            return Optional.of(new QualifiedSecurityPermission(permission, name));
        } catch (IllegalArgumentException ex) {
            logger.warn("Cannot parse presumed Ignite security permission: '{}'", str, ex);
            return Optional.empty();
        }
    }
}
