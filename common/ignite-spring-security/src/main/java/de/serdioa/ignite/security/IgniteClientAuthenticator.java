package de.serdioa.ignite.security;

import org.apache.ignite.plugin.security.AuthenticationContext;
import org.apache.ignite.plugin.security.SecuritySubject;


@FunctionalInterface
public interface IgniteClientAuthenticator {

    SecuritySubject authenticate(AuthenticationContext ctx);
}
