package de.serdioa.jpa.hibernate.repository;

import java.util.Optional;

import de.serdioa.ignite.domain.Right;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;


@Repository("jpaRightRepository")
public interface RightRepository extends JpaRepository<Right, Integer>, QuerydslPredicateExecutor<Right> {

    Optional<Right> findByRightName(String rightName);


    @Query("""
           SELECT roleRight
           FROM Role role
           JOIN role.rightIds roleRightId
           JOIN Right roleRight ON roleRightId = roleRight.id
           WHERE role.id = :roleId
           """)
    Iterable<Right> findByRoleId(Integer roleId);


    @Query("""
           SELECT DISTINCT roleRight
           FROM User user
           JOIN user.roleIds roleId
           JOIN Role role ON roleId = role.id
           JOIN role.rightIds rightId
           JOIN Right roleRight ON rightId = roleRight.id
           WHERE user.id = :userId
           """)
    Iterable<Right> findByUserId(Integer userId);
}
