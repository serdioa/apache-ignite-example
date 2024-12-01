package de.serdioa.ignite.thinclient.cmd;

import de.serdioa.ignite.test.UserRepositoryServiceTest;
import de.serdioa.ignite.test.UserIgniteClientServiceTest;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class ApplicationMainRunner {

    @Autowired
    private UserIgniteClientServiceTest userIgniteClientServiceTest;

    @Autowired
    private UserRepositoryServiceTest UserRepositoryServiceTest;


    @PostConstruct
    public void afterPropertiesSet() {
        this.testUserIgniteClientService();
        this.testUserRepositoryService();
    }


    private void testUserIgniteClientService() {
        this.userIgniteClientServiceTest.testFindAll();
        this.userIgniteClientServiceTest.testFindById();
        this.userIgniteClientServiceTest.testFindByUsername();
        this.userIgniteClientServiceTest.testFindByUsernameLikeAndLocked();
    }


    private void testUserRepositoryService() {
        this.UserRepositoryServiceTest.testFindAll();
        this.UserRepositoryServiceTest.testFindById();
        this.UserRepositoryServiceTest.testFindByUsername();
        this.UserRepositoryServiceTest.testFindByUsernameLikeAndLocked();
    }
}
