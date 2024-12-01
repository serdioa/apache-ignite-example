package de.serdioa.ignite.springdata;

import org.apache.ignite.springdata.repository.config.EnableIgniteRepositories;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableIgniteRepositories("de.serdioa.ignite.springdata")
public class IgniteRepositoryConfiguration {

}
