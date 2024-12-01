package de.serdioa.ignite.security;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.plugin.security.SecuritySubject;
import org.springframework.util.Assert;


@Slf4j
public class AsyncIgniteSecuritySubjectService extends AbstractIgniteSecuritySubjectService {

    private ExecutorService executorService;
    private final long timeout = 10;
    private final TimeUnit timeoutUnit = TimeUnit.SECONDS;


    public AsyncIgniteSecuritySubjectService(final Ignite ignite) {
        super(ignite);

        final ThreadFactory tf = Thread.ofPlatform().name("AsyncIgniteSecuritySubjectService-worker").factory();
        this.executorService = Executors.newSingleThreadExecutor(tf);
    }


    public AsyncIgniteSecuritySubjectService(final Ignite ignite, final ExecutorService executorService) {
        super(ignite);

        Assert.notNull(executorService, "executorService cannot be null");
        this.executorService = executorService;
    }


    public void stop() {
        if (this.executorService != null) {
            this.executorService.shutdownNow();
            this.executorService = null;
        }
    }


    @Override
    public void put(UUID uuid, SecuritySubject securitySubject) {
        log.debug("Async put SecuritySubject {} / {}", uuid, securitySubject);

        this.executorService.submit(() -> {
            this.doPut(uuid, securitySubject);
        });
    }


    @Override
    public boolean remove(UUID uuid) {
        log.debug("Async remove SecuritySubject {}", uuid);

        final Future<Boolean> future = this.executorService.submit(() -> {
            return this.doRemove(uuid);
        });

        try {
            return future.get(this.timeout, this.timeoutUnit);
        } catch (InterruptedException ex) {
            log.error("Interrupted while waiting for remove SecuritySubject");
            Thread.currentThread().interrupt();
            return false;
        } catch (TimeoutException ex) {
            log.error("Timeout while waiting for remove SecuritySubject", ex);
            return false;
        } catch (ExecutionException ex) {
            log.error("Exception while waiting for remove SecuritySubject", ex);
            return false;
        }
    }


    @Override
    public SecuritySubject get(UUID uuid) {
        log.debug("Async get SecuritySubject {}", uuid);

        final Future<SecuritySubject> future = this.executorService.submit(() -> {
            return this.doGet(uuid);
        });

        try {
            return future.get(this.timeout, this.timeoutUnit);
        } catch (InterruptedException ex) {
            log.error("Interrupted while waiting for get SecuritySubject");
            Thread.currentThread().interrupt();
            return null;
        } catch (TimeoutException ex) {
            log.error("Timeout while waiting for get SecuritySubject", ex);
            return null;
        } catch (ExecutionException ex) {
            log.error("Exception while waiting for get SecuritySubject", ex);
            return null;
        }
    }


    @Override
    public Collection<SecuritySubject> getAll() {
        log.debug("Async get all SecuritySubjects");

        final Future<Collection<SecuritySubject>> future = this.executorService.submit(() -> {
            return this.doGetAll();
        });

        try {
            return future.get(this.timeout, this.timeoutUnit);
        } catch (InterruptedException ex) {
            log.error("Interrupted while waiting for get all SecuritySubjects");
            Thread.currentThread().interrupt();
            return Collections.emptyList();
        } catch (TimeoutException ex) {
            log.error("Timeout while waiting for get all SecuritySubjects", ex);
            return Collections.emptyList();
        } catch (ExecutionException ex) {
            log.error("Exception while waiting for get all SecuritySubjects", ex);
            return Collections.emptyList();
        }
    }
}
