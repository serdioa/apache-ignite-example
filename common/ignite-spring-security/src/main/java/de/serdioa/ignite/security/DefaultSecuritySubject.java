package de.serdioa.ignite.security;

import java.net.InetSocketAddress;
import java.util.UUID;

import lombok.Builder;
import lombok.ToString;
import org.apache.ignite.plugin.security.SecurityPermissionSet;
import org.apache.ignite.plugin.security.SecuritySubject;
import org.apache.ignite.plugin.security.SecuritySubjectType;


@Builder
@ToString
public class DefaultSecuritySubject implements SecuritySubject {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 7318801095243064086L;

    private final UUID id;
    private final SecuritySubjectType type;
    private final Object login;
    private final InetSocketAddress address;

    private final SecurityPermissionSet permissions;


    @Override
    public UUID id() {
        return this.id;
    }


    @Override
    public SecuritySubjectType type() {
        return this.type;
    }


    @Override
    public Object login() {
        return this.login;
    }


    @Override
    public InetSocketAddress address() {
        return this.address;
    }


    @Override
    public SecurityPermissionSet permissions() {
        return this.permissions;
    }
}
