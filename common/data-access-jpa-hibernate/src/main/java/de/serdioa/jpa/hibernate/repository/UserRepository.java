package de.serdioa.jpa.hibernate.repository;

import java.util.Optional;

import de.serdioa.ignite.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository("jpaUserRepository")
public interface UserRepository extends JpaRepository<User, Integer>, QuerydslPredicateExecutor<User> {

    Optional<User> findByUsername(String username);


    @Query("SELECT u FROM User u WHERE u.locked = true")
    Iterable<User> findLocked();


    @Query("SELECT u FROM User u WHERE u.username like :username")
    Iterable<User> findSimilarName(@Param("username") String username);
}
