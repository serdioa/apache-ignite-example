package de.serdioa.ignite.test;

import org.springframework.beans.factory.annotation.Autowired;


/**
 * Sample usage of {@link UserIgniteCacheService}.
 */
public class UserIgniteCacheServiceTest extends AbstractUserServiceTest {

    @Autowired
    private UserIgniteCacheService service;


    @Override
    public UserService getUserService() {
        return this.service;
    }
}
