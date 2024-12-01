package de.serdioa.ignite.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.plugin.security.SecuritySubject;
import org.springframework.util.Assert;


@Slf4j
public class DelegatingSecuritySubjectService implements SecuritySubjectService {

    private final List<SecuritySubjectService> delegates;


    public DelegatingSecuritySubjectService(final Collection<SecuritySubjectService> delegates) {
        Assert.notNull(delegates, "delegates cannot be null");
        this.delegates = new ArrayList<>(delegates);
    }


    public DelegatingSecuritySubjectService(final SecuritySubjectService... delegates) {
        this.delegates = Arrays.asList(delegates);
    }


    @Override
    public void put(UUID uuid, SecuritySubject securitySubject) {
        throw new UnsupportedOperationException("Put is not supported by DelegatingSecuritySubjectService. "
                + "Explicitly put new values to a particular delegate.");
    }


    @Override
    public boolean remove(UUID uuid) {
        // Attempt to remove from all delegates, return success if removed at least from one.
        // Removing from multiple makes sense if one delegate is a local in-memory cache, whereas another delegate
        // is a remote storage.
        boolean removed = false;
        for (SecuritySubjectService delegate : this.delegates) {
            removed = removed || delegate.remove(uuid);
        }

        return removed;
    }


    @Override
    public SecuritySubject get(UUID uuid) {
        for (SecuritySubjectService delegate : this.delegates) {
            final SecuritySubject securitySubject = delegate.get(uuid);
            if (securitySubject != null) {
                return securitySubject;
            }
        }

        return null;
    }


    @Override
    public Collection<SecuritySubject> getAll() {
        // Collect from all delegate aggregated by ID.
        // In case of multiple entries, the first delegate wins.
        final Map<UUID, SecuritySubject> securitySubjects = new HashMap<>();

        for (SecuritySubjectService delegate : this.delegates) {
            final Collection<SecuritySubject> delegateSecuritySubjects = delegate.getAll();
            for (SecuritySubject securitySubject : delegateSecuritySubjects) {
                securitySubjects.putIfAbsent(securitySubject.id(), securitySubject);
            }
        }

        return securitySubjects.values();
    }
}
