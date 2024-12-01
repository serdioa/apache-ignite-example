package de.serdioa.ignite.test;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import de.serdioa.ignite.domain.User;
import javax.annotation.PostConstruct;
import javax.cache.Cache;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class UserReader {

    @Autowired
    private Ignite ignite;

    @Setter
    private Integer consumerId;

    @Setter
    private boolean local = false;

    private IgniteCache<Integer, User> userCache;


    @PostConstruct
    public void afterPropertiesSet() {
        log.info("Starting {}", this.getClass().getSimpleName());
        log.info("    {}: consumerId={}", this.getClass().getSimpleName(), this.consumerId);
        log.info("    {}: local={}", this.getClass().getSimpleName(), this.local);

        log.info("Creating userCache");
        this.userCache = this.ignite.getOrCreateCache(User.class.getSimpleName());
        log.info("Created userCache");

        log.info("Started {}", this.getClass().getSimpleName());
    }


    @Scheduled(fixedRate = 10000L)
    public void findAllScanQuery() {
        log.info("Reading all users with Ignite ScanQuery()");

        ScanQuery<Integer, User> query = new ScanQuery<>();
        if (this.local) {
            query.setLocal(true);
        }

        try (QueryCursor<Cache.Entry<Integer, User>> queryCursor = userCache.query(query)) {
            // Get users sorted by ID.
            List<User> users = StreamSupport.stream(queryCursor.spliterator(), false)
                    .map(Cache.Entry::getValue)
                    .sorted(Comparator.comparingInt(User::getId))
                    .collect(Collectors.toList());

            System.out.printf("Found %d users:\n", users.size());
            for (User user : users) {
                System.out.println("    " + user);
            }
        }
    }
}
