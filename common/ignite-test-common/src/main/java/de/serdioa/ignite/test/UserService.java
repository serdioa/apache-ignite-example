package de.serdioa.ignite.test;

import java.util.List;
import java.util.Optional;

import de.serdioa.ignite.domain.User;


public interface UserService {

    List<User> findAll();


    Optional<User> findById(Integer userId);


    Optional<User> findByUsername(String username);


    List<User> findByUsernameLikeAndLocked(String usernamePattern, boolean locked);


    User save(User user);


    default void delete(User user) {
        deleteById(user.getId());
    }


    void deleteById(Integer userId);
}
