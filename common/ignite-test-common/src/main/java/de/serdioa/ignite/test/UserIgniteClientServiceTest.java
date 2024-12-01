package de.serdioa.ignite.test;

import org.springframework.beans.factory.annotation.Autowired;


/**
 * Sample usage of {@link UserIgniteCacheService}.
 */
public class UserIgniteClientServiceTest extends AbstractUserServiceTest {

    @Autowired
    private UserIgniteClientService service;


    @Override
    protected UserService getUserService() {
        return this.service;
    }
}
