package de.serdioa.ignite.security;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.plugin.security.SecuritySubject;


@Slf4j
public class LocalSecuritySubjectService implements SecuritySubjectService {

    private final ConcurrentMap<UUID, SecuritySubject> cache = new ConcurrentHashMap<>();


    @Override
    public void put(UUID uuid, SecuritySubject securitySubject) {
        log.debug("Put SecuritySubject {}", uuid, securitySubject);

        this.cache.put(uuid, securitySubject);
    }


    @Override
    public boolean remove(UUID uuid) {
        log.debug("Remove SecuritySubject {}", uuid);

        return (this.cache.remove(uuid) != null);
    }


    @Override
    public SecuritySubject get(UUID uuid) {
        log.debug("Get SecuritySubject {}", uuid);

        return this.cache.get(uuid);
    }


    @Override
    public Collection<SecuritySubject> getAll() {
        log.debug("Get all SecuritySubjects");

        return this.cache.values();
    }
}
