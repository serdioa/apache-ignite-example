package de.serdioa.ignite.server;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@EntityScan("de.serdioa.ignite.domain")
public class JpaInitializer {

}
