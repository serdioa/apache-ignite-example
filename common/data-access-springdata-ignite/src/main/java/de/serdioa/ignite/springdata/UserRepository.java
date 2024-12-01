package de.serdioa.ignite.springdata;

import java.util.Optional;

import de.serdioa.ignite.domain.User;
import org.apache.ignite.springdata.repository.IgniteRepository;
import org.apache.ignite.springdata.repository.config.RepositoryConfig;


@RepositoryConfig(cacheName = "User")
public interface UserRepository extends IgniteRepository<User, Integer> {

    Optional<User> findByUsername(String username);


    Iterable<User> findByPassword(String password);


    Iterable<User> findByLocked(Boolean locked);


    Iterable<User> findByUsernameLikeAndLocked(String password, Boolean locked);


    @SuppressWarnings("unchecked")
    @Override
    default User save(User user) {
        return this.save(user.getId(), user);
    }
}
