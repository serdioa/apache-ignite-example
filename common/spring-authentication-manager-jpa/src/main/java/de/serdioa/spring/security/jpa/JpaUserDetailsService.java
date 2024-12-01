package de.serdioa.spring.security.jpa;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import de.serdioa.ignite.domain.Right;
import de.serdioa.ignite.domain.User;
import de.serdioa.jpa.hibernate.repository.RightRepository;
import de.serdioa.jpa.hibernate.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


@Slf4j
public class JpaUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RightRepository rightRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user: '{}'", username);

        final Optional<User> userHolder = this.userRepository.findByUsername(username);
        if (userHolder.isEmpty()) {
            log.trace("User not found: '{}'", username);
            throw new UsernameNotFoundException("User not found: '" + username + "'");
        }
        final User user = userHolder.get();
        final Iterable<Right> rights = this.rightRepository.findByUserId(user.getId());

        final Set<GrantedAuthority> authorities = StreamSupport.stream(rights.spliterator(), false)
                .map(Right::getRightName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        // TODO: validate account expiration, password expiration, set corresponding properties on the builder.
        final UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .accountLocked(Boolean.TRUE.equals(user.getLocked()))
                .authorities(authorities)
                .build();
        log.trace("Found user '{}': {}", username, userDetails);

        return userDetails;
    }
}
