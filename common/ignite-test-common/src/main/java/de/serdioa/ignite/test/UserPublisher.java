package de.serdioa.ignite.test;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;

import de.serdioa.ignite.domain.User;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.Assert;


@Slf4j
public class UserPublisher {

    // The name of this publisher may be set to distinguish several different publishers.
    @Setter
    private String name;

    // The number of users created by this publisher to keep before starting to delete them.
    // A value <= 0 means to not delete users.
    @Setter
    private int keepCount;

    // The User Service to be used to create / delete users.
    private UserService userService;

    // Users created by this User Service, if we track created users to be deleted.
    private Queue<User> createdUsers = new ArrayDeque<>();


    public UserPublisher(UserService userService) {
        Assert.notNull(userService, "userService is required");
        this.userService = userService;
    }


    @Scheduled(fixedDelay = 5000)
    public void execute() {
        // Create a new user and put the user in the cache.
        final User user = this.createUser();
        this.cacheUser(user);

        // Delete a cached user, if we already have more cached users as configured.
        this.deleteCachedUser();
    }


    private User createUser() {
        final int userId = this.getNextUserId();
        final String username = this.getUsername(userId);
        final String password = "{noop}" + username + "Password";

        final User user = new User();
        user.setId(userId);
        user.setUsername(username);
        user.setPassword(password);
        user.setLocked(false);

        log.info("UserPublisher[{}] attempts to create the user {} / {}", this.getNameForLog(), userId, username);
        this.userService.save(user);
        log.info("UserPublisher[{}] created the user {} / {}", this.getNameForLog(), userId, username);

        return user;
    }


    private void cacheUser(final User user) {
        if (this.keepCount > 0) {
            this.createdUsers.add(user);
        }
    }


    private void deleteCachedUser() {
        if (this.keepCount > 0) {
            while (this.createdUsers.size() > this.keepCount) {
                final User user = this.createdUsers.poll();
                this.deleteUser(user);
            }
        }
    }


    private void deleteUser(final User user) {
        log.info("UserPublisher[{}] attempts to delete the user {} / {}", this.getNameForLog(),
                user.getId(), user.getUsername());

        this.userService.delete(user);

        log.info("UserPublisher[{}] deleted the user {} / {}", this.getNameForLog(),
                user.getId(), user.getUsername());
    }


    private String getUsername(final int userId) {
        final StringBuilder sb = new StringBuilder("user_");
        if (this.name != null) {
            sb.append(this.name).append("_");
        }
        sb.append(userId);

        return sb.toString();
    }


    // Return ID of the next user to be created.
    private int getNextUserId() {
        // Very naive and error-prone implementation: just select all users, return maxium + 1.
        // This implementation is only for test purposes, it is not suitable for parallel execution because multiple
        // clients may attempt to create users with the same ID.
        final List<User> users = this.userService.findAll();
        final int maxId = users.stream().map(User::getId).max(Comparator.naturalOrder()).orElse(0);
        return maxId + 1;
    }


    private String getNameForLog() {
        return (this.name != null ? this.name : "<unnamed>");
    }
}
