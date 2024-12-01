package de.serdioa.ignite.client.cmd;

import de.serdioa.ignite.test.UserRepositoryServiceTest;
import de.serdioa.ignite.test.UserIgniteCacheServiceTest;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class ApplicationMainRunner {

    @Autowired
    private UserIgniteCacheServiceTest userIgniteCacheServiceTest;

    @Autowired
    private UserRepositoryServiceTest UserRepositoryServiceTest;


    @PostConstruct
    public void afterPropertiesSet() {
        this.testUserIgniteCacheService();
        this.testUserRepositoryService();
    }


    private void testUserIgniteCacheService() {
        this.userIgniteCacheServiceTest.testFindAll();
        this.userIgniteCacheServiceTest.testFindById();
        this.userIgniteCacheServiceTest.testFindByUsername();
        this.userIgniteCacheServiceTest.testFindByUsernameLikeAndLocked();
    }


    private void testUserRepositoryService() {
        this.UserRepositoryServiceTest.testFindAll();
        this.UserRepositoryServiceTest.testFindById();
        this.UserRepositoryServiceTest.testFindByUsername();
        this.UserRepositoryServiceTest.testFindByUsernameLikeAndLocked();
    }
}
