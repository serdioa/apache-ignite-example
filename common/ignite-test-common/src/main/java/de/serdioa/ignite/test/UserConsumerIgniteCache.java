package de.serdioa.ignite.test;

import de.serdioa.ignite.domain.User;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.cache.Cache;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.EventType;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.ContinuousQuery;
import org.apache.ignite.cache.query.QueryCursor;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Listens on {@code User} objects based on node Ignite API.
 */
@Slf4j
public class UserConsumerIgniteCache {

    @Autowired
    private Ignite ignite;

    private IgniteCache<Integer, User> userCache;

    private ContinuousQuery<Integer, User> userContinousQuery;
    private QueryCursor<Cache.Entry<Integer, User>> userContinuousQueryCursor;


    @PostConstruct
    public void afterPropertiesSet() {
        log.info("Starting {}", this.getClass().getSimpleName());

        log.info("Creating userCache");
        this.userCache = this.ignite.getOrCreateCache(User.class.getSimpleName());
        log.info("Created userCache");

        log.info("Creating a continuous query");
        this.createContinuousQuery();
        log.info("Created a continuous query");

        log.info("Started {}", this.getClass().getSimpleName());
    }


    @PreDestroy
    public void destroy() {
        if (this.userContinuousQueryCursor != null) {
            this.userContinuousQueryCursor.close();
            this.userContinuousQueryCursor = null;
        }
        this.userContinousQuery = null;
    }


    private void createContinuousQuery() {
        this.userContinousQuery = new ContinuousQuery<>();
        this.userContinousQuery.setLocalListener(this::onContinuousQueryEvent);
        this.userContinuousQueryCursor = this.userCache.query(this.userContinousQuery);
    }


    private void onContinuousQueryEvent(final Iterable<CacheEntryEvent<? extends Integer, ? extends User>> events) {
        for (CacheEntryEvent<? extends Integer, ? extends User> event : events) {
            final EventType eventType = event.getEventType();
            final Integer userId = event.getKey();
            final User oldUser = event.getOldValue();
            final User newUser = event.getValue();

            log.info("Received {} event for the user {}: old={}, new={}", eventType, userId, oldUser, newUser);
        }
    }
}
