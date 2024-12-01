package de.serdioa.ignite.security;

import java.util.Collection;
import java.util.UUID;

import org.apache.ignite.plugin.security.SecuritySubject;


public interface SecuritySubjectService {

    void put(UUID uuid, SecuritySubject securitySubject);


    boolean remove(UUID uuid);


    SecuritySubject get(UUID uuid);


    Collection<SecuritySubject> getAll();
}
