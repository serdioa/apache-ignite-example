package de.serdioa.ignite.security;

import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.plugin.security.SecurityCredentials;
import org.apache.ignite.plugin.security.SecuritySubject;


@FunctionalInterface
public interface IgniteNodeAuthenticator {

    SecuritySubject authenticate(ClusterNode node, SecurityCredentials cred);
}
