package de.serdioa.ignite.dataaccess.test;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@Configuration
@EntityScan("de.serdioa.ignite.domain")
@EnableJpaRepositories("de.serdioa.jpa.hibernate.repository")
public class JpaConfiguration {

}
