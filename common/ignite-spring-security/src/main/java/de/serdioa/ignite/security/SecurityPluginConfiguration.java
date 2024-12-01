package de.serdioa.ignite.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.apache.ignite.plugin.PluginConfiguration;
import org.apache.ignite.plugin.security.SecurityCredentials;


@AllArgsConstructor
@ToString
public class SecurityPluginConfiguration implements PluginConfiguration {

    @Getter
    private final SecurityCredentials localNodeCredentials;
}
