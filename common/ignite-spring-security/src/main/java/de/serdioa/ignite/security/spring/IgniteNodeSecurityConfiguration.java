package de.serdioa.ignite.security.spring;

import de.serdioa.ignite.security.IgniteClientAuthenticator;
import de.serdioa.ignite.security.IgniteNodeAuthenticator;
import de.serdioa.ignite.security.SecurityPluginConfiguration;
import de.serdioa.ignite.security.SecurityPluginProvider;
import org.apache.ignite.plugin.security.SecurityCredentials;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;


@AutoConfiguration
public class IgniteNodeSecurityConfiguration {

    @Bean
    @ConfigurationProperties("ignite.node.security")
    public IgniteSecurityProperties igniteSecurityProperties() {
        return new IgniteSecurityProperties();
    }


    @Bean
    public SecurityPluginConfiguration igniteSecurityPluginConfiguration(IgniteSecurityProperties securityProperties) {
        SecurityCredentials igniteNodeCredentials = new SecurityCredentials(
                securityProperties.getUsername(), securityProperties.getPassword());
        return new SecurityPluginConfiguration(igniteNodeCredentials);
    }


    @Bean
    public QualifiedSecurityPermissionParser igniteSecurityPermissionParser() {
        return new DefaultQualifiedSecurityPermissionParser();
    }


    @Bean
    public IgniteAuthenticationManagerAuthenticator igniteAuthenticator(AuthenticationManager authenticationManager,
            QualifiedSecurityPermissionParser igniteSecurityPermissionParser) {
        return new IgniteAuthenticationManagerAuthenticator(authenticationManager, igniteSecurityPermissionParser);
    }


    @Bean
    public SecurityPluginProvider igniteSecurityPluginProvider(SecurityPluginConfiguration igniteSecurityPluginConfiguration,
            IgniteNodeAuthenticator igniteNodeAuthenticator, IgniteClientAuthenticator igniteClientAuthenticator) {
        return SecurityPluginProvider.builder()
                .config(igniteSecurityPluginConfiguration)
                .nodeAuthenticator(igniteNodeAuthenticator)
                .clientAuthenticator(igniteClientAuthenticator)
                .build();
    }
}
