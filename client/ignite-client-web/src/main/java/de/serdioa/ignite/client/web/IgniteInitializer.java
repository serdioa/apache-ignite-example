package de.serdioa.ignite.client.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@Import({de.serdioa.ignite.spring.autoconfigure.IgniteNodeAutoConfiguration.class,
    de.serdioa.ignite.springdata.IgniteRepositoryConfiguration.class})
public class IgniteInitializer {

}
