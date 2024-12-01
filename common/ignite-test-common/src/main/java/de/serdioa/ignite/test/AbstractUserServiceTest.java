package de.serdioa.ignite.test;

import java.util.List;
import java.util.Optional;

import de.serdioa.ignite.domain.User;


public abstract class AbstractUserServiceTest {

    protected abstract UserService getUserService();


    public void testFindAll() {
        System.out.println("Testing " + this.getClass().getSimpleName() + ".findAll()");
        List<User> users = this.getUserService().findAll();

        System.out.println("Found users: " + users.size());
        users.forEach(System.out::println);
    }


    public void testFindById() {
        System.out.println("Testing " + this.getClass().getSimpleName() + ".findById()");

        System.out.println("Looking up existing user with id = 1");
        Optional<User> existingUser = this.getUserService().findById(1);
        if (existingUser.isPresent()) {
            System.out.println("OK: user #1 is found: " + existingUser);
        } else {
            System.out.println("ERROR: user #1 is not found");
        }

        System.out.println("Looking up non-existing user with id = -1");
        Optional<User> nonExistingUser = this.getUserService().findById(-1);
        if (nonExistingUser.isPresent()) {
            System.out.println("ERROR: user #-1 is found: " + nonExistingUser);
        } else {
            System.out.println("OK: user #1 is not found");
        }
    }


    public void testFindByUsername() {
        System.out.println("Testing " + this.getClass().getSimpleName() + ".findByUsername()");

        System.out.println("Looking up existing user with username = 'user_1'");
        Optional<User> existingUser = this.getUserService().findByUsername("user_1");
        if (existingUser.isPresent()) {
            System.out.println("OK: 'user_1' is found: " + existingUser);
        } else {
            System.out.println("ERROR: 'user_1' is not found");
        }

        System.out.println("Looking up non-existing user with username = 'nonexisting'");
        Optional<User> nonExistingUser = this.getUserService().findByUsername("nonexisting");
        if (nonExistingUser.isPresent()) {
            System.out.println("ERROR: user 'nonexisting' is found: " + nonExistingUser);
        } else {
            System.out.println("OK: user 'nonexisting' is not found");
        }
    }


    public void testFindByUsernameLikeAndLocked() {
        System.out.println("Testing " + this.getClass().getSimpleName() + ".findByUsernameLikeAndLocked()");
        List<User> users = this.getUserService().findByUsernameLikeAndLocked("user_1%", true);

        System.out.println("Found users: " + users.size());
        users.forEach(System.out::println);
    }
}
