package de.serdioa.ignite.persistence.jpa;

import javax.persistence.EntityManager;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.IgniteException;
import org.apache.ignite.cache.store.CacheStoreSession;
import org.apache.ignite.cache.store.CacheStoreSessionListener;
import org.apache.ignite.lifecycle.LifecycleAware;
import org.apache.ignite.resources.SpringResource;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;


@Slf4j
@NoArgsConstructor
public class JpaCacheStoreSessionListener implements CacheStoreSessionListener, LifecycleAware {

    @SpringResource(resourceClass = EntityManager.class)
    private EntityManager entityManager;

    @SpringResource(resourceClass = TransactionTemplate.class)
    private TransactionTemplate transactionTemplate;


    @Override
    public void start() throws IgniteException {
        log.info("Starting JpaCacheStoreSessionListener");

        Assert.notNull(this.entityManager, "entityManager is required");
        Assert.notNull(this.transactionTemplate, "transactionTemplate is required");

        log.info("Started JpaCacheStoreSessionListener");
    }


    @Override
    public void stop() throws IgniteException {
        log.info("Stopping JpaCacheStoreSessionListener");

        log.info("Stopped JpaCacheStoreSessionListener");
    }


    @Override
    public void onSessionStart(CacheStoreSession session) {
        log.info("JpaCacheStoreSessionListener: onSessionStart()");

        final Object existingAttachment = session.attachment();
        if (existingAttachment == null) {
            final TransactionStatus transactionStatus = this.beginTransaction();
            final JpaCacheStoreSessionAttachment attachment =
                    new JpaCacheStoreSessionAttachment(entityManager, transactionTemplate, transactionStatus);

            session.attach(attachment);
        } else {
            log.warn("CacheStoreSession already has attachment: " + existingAttachment);
        }
    }


    @Override
    public void onSessionEnd(CacheStoreSession session, boolean commit) {
        log.info("JpaCacheStoreSessionListener: onSessionEnd()");

        final Object attachment = session.attachment();
        if (attachment instanceof JpaCacheStoreSessionAttachment) {
            try {
                final TransactionStatus transactionStatus =
                        ((JpaCacheStoreSessionAttachment) attachment).getTransactionStatus();
                if (commit) {
                    this.commitTransaction(transactionStatus);
                } else {
                    this.rollbackTransaction(transactionStatus);
                }
            } finally {
                session.attach(null);
            }
        } else {
            log.warn("Unexpected CacheStoreSession attachment: {}", attachment);
        }
    }


    private TransactionStatus beginTransaction() {
        try {
            final PlatformTransactionManager transactionManager = this.transactionTemplate.getTransactionManager();
            return transactionManager.getTransaction(this.transactionTemplate);
        } catch (TransactionException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new CannotCreateTransactionException("RuntimeException when creating transaction", ex);
        } catch (Throwable ex) {
            throw new CannotCreateTransactionException("Exception when creating transaction", ex);
        }
    }


    private void commitTransaction(TransactionStatus transactionStatus) {
        try {
            final PlatformTransactionManager transactionManager = this.transactionTemplate.getTransactionManager();
            transactionManager.commit(transactionStatus);
        } catch (TransactionException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new TransactionSystemException("RuntimeException when commiting transaction", ex);
        } catch (Throwable ex) {
            throw new TransactionSystemException("Exception when commiting transaction", ex);
        }
    }


    private void rollbackTransaction(TransactionStatus transactionStatus) {
        try {
            final PlatformTransactionManager transactionManager = this.transactionTemplate.getTransactionManager();
            transactionManager.rollback(transactionStatus);
        } catch (TransactionException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new TransactionSystemException("RuntimeException when rolling back transaction", ex);
        } catch (Throwable ex) {
            throw new TransactionSystemException("Exception when rolling back transaction", ex);
        }
    }
}
