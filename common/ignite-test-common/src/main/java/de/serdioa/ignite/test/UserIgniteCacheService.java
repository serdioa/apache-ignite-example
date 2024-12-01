package de.serdioa.ignite.test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import de.serdioa.ignite.domain.User;
import javax.annotation.PostConstruct;
import javax.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;


/**
 * Provides access to {@code User} objects directly based on node Ignite API.
 *
 * @see UserRepositoryService
 */
@Slf4j
public class UserIgniteCacheService implements UserService {

    @Autowired
    private Ignite ignite;

    private IgniteCache<Integer, User> userCache;


    @PostConstruct
    public void afterPropertiesSet() {
        log.info("Starting {}", this.getClass().getSimpleName());

        log.info("Creating userCache");
        this.userCache = this.ignite.getOrCreateCache(User.class.getSimpleName());
        log.info("Created userCache");

        log.info("Started {}", this.getClass().getSimpleName());
    }


    @Override
    public List<User> findAll() {
        log.debug("UserIgniteCacheService.findAllUsers()");

        final ScanQuery<Integer, User> query = new ScanQuery<>();
        try (QueryCursor<Cache.Entry<Integer, User>> queryCursor = this.userCache.query(query)) {
            // Get users sorted by ID.
            return StreamSupport.stream(queryCursor.spliterator(), false)
                    .map(Cache.Entry::getValue)
                    .sorted(Comparator.comparingInt(User::getId))
                    .toList();
        }
    }


    @Override
    public Optional<User> findById(final Integer userId) {
        log.debug("UserIgniteCacheService.findById({})", userId);

        final User user = this.userCache.get(userId);
        return Optional.ofNullable(user);
    }


    @Override
    public Optional<User> findByUsername(final String username) {
        log.debug("UserIgniteCacheService.findByUsername({})", username);

        final SqlFieldsQuery query = new SqlFieldsQuery("select _val from User where username = ?")
                .setArgs(username);

        Optional<User> result = Optional.empty();
        int rowCount = 0;
        try (QueryCursor<List<?>> queryCursor = this.userCache.query(query)) {
            for (List<?> lst : queryCursor) {
                rowCount++;
                if (rowCount > 1) {
                    throw new NonUniqueResultException("Non-unique result when searching " + User.class
                            + " by username");
                }

                if (lst.size() == 1) {
                    final Object val = lst.get(0);
                    if (val instanceof User user) {
                        result = Optional.of(user);
                    } else {
                        // Unexpected object type in the result.
                        throw new PersistenceException("Found " + (val == null ? null : val.getClass())
                                + " when reading " + User.class);
                    }
                } else {
                    throw new PersistenceException("Found " + lst.size() + " items when reading " + User.class
                            + ", expeced 1");
                }
            }
        }

        return result;
    }


    public List<User> findByUsernameLikeAndLocked(final String usernamePattern, final boolean locked) {
        log.debug("UserIgniteCacheService.findByUsernameLikeAndLocked({}, {})", usernamePattern, locked);

        final SqlFieldsQuery query =
                new SqlFieldsQuery("select _val from User where (username like ?) and (locked = ?)")
                        .setArgs(usernamePattern, locked);

        final List<User> result = new ArrayList<>();
        try (QueryCursor<List<?>> queryCursor = this.userCache.query(query)) {
            for (List<?> lst : queryCursor) {
                if (lst.size() == 1) {
                    final Object val = lst.get(0);
                    if (val instanceof User user) {
                        result.add(user);
                    } else {
                        // Unexpected object type in the result.
                        throw new PersistenceException("Found " + (val == null ? null : val.getClass())
                                + " when reading " + User.class);
                    }
                } else {
                    throw new PersistenceException("Found " + lst.size() + " items when reading " + User.class
                            + ", expeced 1");
                }
            }
        }

        return result;
    }


    @Override
    public User save(User user) {
        log.debug("UserIgniteCacheService.save({})", user);

        this.userCache.put(user.getId(), user);

        return user;
    }


    @Override
    public void deleteById(Integer userId) {
        log.debug("UserIgniteCacheService.deleteById({})", userId);

        this.userCache.remove(userId);
    }
}
