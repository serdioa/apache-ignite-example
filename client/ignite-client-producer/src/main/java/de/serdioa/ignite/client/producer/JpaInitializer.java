package de.serdioa.ignite.client.producer;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@EntityScan("de.serdioa.ignite.domain")
public class JpaInitializer {

}
