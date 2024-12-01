package de.serdioa.dataaccess.ignite.security.spring;

import de.serdioa.ignite.security.spring.DefaultQualifiedSecurityPermissionParser;
import de.serdioa.ignite.security.spring.QualifiedSecurityPermission;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import java.util.stream.Stream;

import org.apache.ignite.plugin.security.SecurityPermission;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;


public class DefaultQualifiedSecurityPermissionParserTest {

    private final DefaultQualifiedSecurityPermissionParser parser = new DefaultQualifiedSecurityPermissionParser();

    @ParameterizedTest
    @MethodSource("testParseArguments")
    public void testParse(String str, Optional<QualifiedSecurityPermission> expected) {
        Optional<QualifiedSecurityPermission> actual = this.parser.parseOptional(str);
        assertEquals(expected, actual);
    }


    public static Stream<Arguments> testParseArguments() {
        return Stream.of(
                // Empty string
                Arguments.of("", Optional.empty()),

                // Permission which is not related to Ignite.
                Arguments.of("non-ignite-permission", Optional.empty()),

                // Unknown permission name.
                Arguments.of("ignite.unknown-permission", Optional.empty()),

                // The prefix indicates that this is an Ignite permission, but an actual permission name is missing.
                Arguments.of("ignite", Optional.empty()),
                Arguments.of("ignite.", Optional.empty()),

                // Valid Ignite permissions.
                Arguments.of("ignite.cache-create", Optional
                        .of(new QualifiedSecurityPermission(SecurityPermission.CACHE_CREATE))),
                Arguments.of("ignite.cache-create:", Optional
                        .of(new QualifiedSecurityPermission(SecurityPermission.CACHE_CREATE))),
                Arguments.of("ignite.cache-create:*", Optional
                        .of(new QualifiedSecurityPermission(SecurityPermission.CACHE_CREATE, "*"))),
                Arguments.of("ignite.cache-create:custom-cache", Optional
                        .of(new QualifiedSecurityPermission(SecurityPermission.CACHE_CREATE, "custom-cache"))),

                // Qualified name is expected.
                Arguments.of("ignite.cache-read", Optional.empty()),
                Arguments.of("ignite.cache-read:", Optional.empty()),
                Arguments.of("ignite.cache-read:   ", Optional.empty()),
                Arguments.of("ignite.cache-read:abc", Optional
                        .of(new QualifiedSecurityPermission(SecurityPermission.CACHE_READ, "abc"))),

                // Qualified name is not expected.
                Arguments.of("ignite.join-as-server:abc", Optional.empty())
        );
    }
}
