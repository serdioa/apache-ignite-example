package de.serdioa.ignite.rest.server.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import de.serdioa.ignite.rest.server.api.UserApiDelegate;
import de.serdioa.ignite.rest.server.model.User;
import de.serdioa.ignite.springdata.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;


@Service
@Slf4j
public class UserApiImpl implements UserApiDelegate {

    private final NativeWebRequest request;

    @Autowired
    private UserRepository userRepository;


    public UserApiImpl(NativeWebRequest request) {
        this.request = request;
    }


    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(this.request);
    }


    @Override
    public ResponseEntity<List<User>> findAllUsers() {
        log.debug("UserApiImpl.findAllUsers()");

        final Iterable<de.serdioa.ignite.domain.User> users = this.userRepository.findAll();
        final List<User> restObjects = toRestObjects(users);

        return ResponseEntity.ok(restObjects);
    }


    @Override
    public ResponseEntity<User> findUserById(Integer userId) {
        log.debug("UserApiImpl.findUserById({})", userId);

        final Optional<de.serdioa.ignite.domain.User> user = this.userRepository.findById(userId);
        if (user.isPresent()) {
            final User restUser = toRestObject(user.get());
            return ResponseEntity.ok(restUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @Override
    public ResponseEntity<User> createUser(User user) {
        log.debug("UserApiImpl.createUser({})", user);

        // Check if the user already exist.
        final Integer userId = user.getId();
        final Optional<de.serdioa.ignite.domain.User> existingUser = this.userRepository.findById(userId);
        if (existingUser.isPresent()) {
            return (ResponseEntity) (ResponseEntity.badRequest().body("User " + userId + " already exist"));
        }
        
        // Create the user.
        final de.serdioa.ignite.domain.User newUser = this.toDomainObject(userId, user);
        final de.serdioa.ignite.domain.User createdUser = this.userRepository.save(newUser);
        
        final User createdRestUser = this.toRestObject(createdUser);
        return ResponseEntity.ok(createdRestUser);
    }


    @Override
    public ResponseEntity<Void> updateUserById(Integer userId, User user) {
        log.debug("UserApiImpl.updateUserById({}, {})", userId, user);
        
        if (user.getId() != null && !Objects.equals(userId, user.getId())) {
            return ResponseEntity.badRequest().build();
        }

        final Optional<de.serdioa.ignite.domain.User> existingDomainObject = this.userRepository.findById(userId);
        if (existingDomainObject.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        final de.serdioa.ignite.domain.User updatedDomainObject = this.toDomainObject(userId, user);
        this.userRepository.save(updatedDomainObject);

        return ResponseEntity.ok().build();
    }


    @Override
    public ResponseEntity<Void> deleteUserById(Integer userId) {
        log.debug("UserApiImpl.deleteUserById({})", userId);
        
        final Optional<de.serdioa.ignite.domain.User> existingDomainObject = this.userRepository.findById(userId);
        if (existingDomainObject.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        this.userRepository.deleteById(userId);

        return ResponseEntity.ok().build();
    }


    private List<User> toRestObjects(Iterable<de.serdioa.ignite.domain.User> domainObjects) {
        return StreamSupport.stream(domainObjects.spliterator(), false)
                .map(this::toRestObject).collect(Collectors.toList());
    }


    private User toRestObject(de.serdioa.ignite.domain.User domainObject) {
        final User restObject = new User();
        restObject.setId(domainObject.getId());
        restObject.setUsername(domainObject.getUsername());
        restObject.setPassword(domainObject.getPassword());
        restObject.setLocked(domainObject.getLocked());

        return restObject;
    }


    private de.serdioa.ignite.domain.User toDomainObject(Integer userId, User user) {
        de.serdioa.ignite.domain.User domainObject = new de.serdioa.ignite.domain.User();

        domainObject.setId(userId);
        domainObject.setUsername(user.getUsername());
        domainObject.setPassword(user.getPassword());
        domainObject.setLocked(user.getLocked());

        return domainObject;
    }
}
