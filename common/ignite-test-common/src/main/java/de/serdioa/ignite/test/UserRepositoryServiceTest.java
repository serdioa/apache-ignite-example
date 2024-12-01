package de.serdioa.ignite.test;

import org.springframework.beans.factory.annotation.Autowired;


/**
 * Sample usage of {@link UserRepositoryService}.
 */
public class UserRepositoryServiceTest extends AbstractUserServiceTest {

    @Autowired
    private UserRepositoryService service;


    @Override
    protected UserService getUserService() {
        return this.service;
    }
}
