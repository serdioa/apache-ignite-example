package de.serdioa.jpa.hibernate.repository;

import java.util.Optional;

import de.serdioa.ignite.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;


@Repository("jpaRoleRepository")
public interface RoleRepository extends JpaRepository<Role, Integer>, QuerydslPredicateExecutor<Role> {

    Optional<Role> findByRoleName(String roleName);


    @Query("""
           SELECT role
           FROM User user
           JOIN user.roleIds userRoleId
           JOIN Role role ON userRoleId = role.id
           WHERE user.id = :userId
           """)
    Iterable<Role> findByUserId(Integer userId);
}
