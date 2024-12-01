package de.serdioa.ignite.security;

import java.util.Collection;
import java.util.UUID;

import org.apache.ignite.Ignite;
import org.apache.ignite.plugin.security.SecuritySubject;


public class SyncIgniteSecuritySubjectService extends AbstractIgniteSecuritySubjectService {

    public SyncIgniteSecuritySubjectService(final Ignite ignite) {
        super(ignite);
    }


    @Override
    public void put(UUID uuid, SecuritySubject securitySubject) {
        super.doPut(uuid, securitySubject);
    }


    @Override
    public boolean remove(UUID uuid) {
        return super.doRemove(uuid);
    }


    @Override
    public SecuritySubject get(UUID uuid) {
        return super.doGet(uuid);
    }


    @Override
    public Collection<SecuritySubject> getAll() {
        return super.doGetAll();
    }
}
