package de.serdioa.spring.security.jpa.autoconfigure;

import de.serdioa.spring.security.jpa.JpaUserDetailsService;

import java.util.List;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;


@AutoConfiguration
public class JpaAuthenticationManagerAutoConfiguration {

    @Bean
    public UserDetailsService jpaUserDetailsService() {
        return new JpaUserDetailsService();
    }


    @Bean
    public AuthenticationProvider userDetailsAuthenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);

        return authenticationProvider;
    }


    @Bean
    public AuthenticationManager authenticationManager(List<AuthenticationProvider> authenticationProviders) {
        return new ProviderManager(authenticationProviders);
    }
}
