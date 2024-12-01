package de.serdioa.ignite.test;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import de.serdioa.ignite.domain.User;
import de.serdioa.ignite.springdata.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Provides access to {@code User} objects based on Spring Data API.
 *
 * @see UserIgniteCacheService
 */
@Slf4j
public class UserRepositoryService implements UserService {

    @Autowired
    private UserRepository userRepository;


    @Override
    public List<User> findAll() {
        log.debug("UserRepositoryService.findAllUsers()");

        final Iterable<User> users = this.userRepository.findAll();
        return StreamSupport.stream(users.spliterator(), false).toList();
    }


    @Override
    public Optional<User> findById(Integer userId) {
        log.debug("UserRepositoryService.findById({})", userId);

        return this.userRepository.findById(userId);
    }


    @Override
    public Optional<User> findByUsername(String username) {
        log.debug("UserRepositoryService.findByUsername({})", username);

        return this.userRepository.findByUsername(username);
    }


    @Override
    public List<User> findByUsernameLikeAndLocked(String usernamePattern, boolean locked) {
        log.debug("UserRepositoryService.findByUsernameLikeAndLocked({}, {})", usernamePattern, locked);

        final Iterable<User> users = this.userRepository.findByUsernameLikeAndLocked(usernamePattern, locked);
        return StreamSupport.stream(users.spliterator(), false).toList();
    }


    @Override
    public User save(User user) {
        log.debug("UserRepositoryService.save({})", user);

        return this.userRepository.save(user.getId(), user);
    }


    @Override
    public void deleteById(Integer userId) {
        log.debug("UserRepositoryService.deleteById({})", userId);

        this.userRepository.deleteById(userId);
    }
}
