package de.serdioa.ignite.dataaccess.test;

import java.util.List;

import de.serdioa.ignite.domain.Right;
import de.serdioa.ignite.domain.Role;
import de.serdioa.ignite.domain.User;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;


@Slf4j
@Component
public class TestHibernateService {

    private final EntityManager em;
    private final TransactionTemplate tt;


    public TestHibernateService(final EntityManager entityManager, final TransactionTemplate transactionTemplate) {
        this.em = entityManager;
        this.tt = transactionTemplate;

        log.info("Starting {}", this.getClass().getSimpleName());
    }


    public void testReadUsers() {
        System.out.println("Reading all Users by criteria");
        CriteriaQuery<User> criteria = this.em.getCriteriaBuilder().createQuery(User.class);
        criteria.from(User.class);

        List<User> result = this.em.createQuery(criteria).getResultList();

        result.forEach(user -> {
            System.out.printf("    %d -> %s\n", user.getId(), user);
        });
        System.out.printf("Finished reading all Users by criteria: total %d\n", result.size());
    }


    public void testReadRoles() {
        System.out.println("Reading all Roles by criteria");
        CriteriaQuery<Role> criteria = this.em.getCriteriaBuilder().createQuery(Role.class);
        criteria.from(Role.class);

        List<Role> result = this.em.createQuery(criteria).getResultList();

        result.forEach(role -> {
            System.out.printf("    %d -> %s\n", role.getId(), role);
        });
        System.out.printf("Finished reading all Roles by criteria: total %d\n", result.size());
    }


    public void testReadRights() {
        System.out.println("Reading all Rights by criteria");
        CriteriaQuery<Right> criteria = this.em.getCriteriaBuilder().createQuery(Right.class);
        criteria.from(Right.class);

        List<Right> result = this.em.createQuery(criteria).getResultList();

        result.forEach(right -> {
            System.out.printf("    %d -> %s\n", right.getId(), right);
        });
        System.out.printf("Finished reading all Rights by criteria: total %d\n", result.size());
    }


    public void testReadUsersByQuery() {
        System.out.println("Reading all Users by query");

        TypedQuery<User> query = this.em.createQuery("SELECT user FROM User user where user.id > ?1", User.class);
        query.setParameter(1, 0);

        List<User> result = query.getResultList();

        result.forEach(user -> {
            System.out.printf("    %d -> %s\n", user.getId(), user);
        });
        System.out.printf("Finished reading all Users by query: total %d\n", result.size());
    }


    public void testReadRolesByQuery() {
        System.out.println("Reading all Roles by query");

        TypedQuery<Role> query = this.em.createQuery("SELECT role FROM Role role where role.id > ?1", Role.class);
        query.setParameter(1, 0);

        List<Role> result = query.getResultList();

        result.forEach(role -> {
            System.out.printf("    %d -> %s\n", role.getId(), role);
        });
        System.out.printf("Finished reading all Roles by query: total %d\n", result.size());
    }


    public void testReadRightsByQuery() {
        System.out.println("Reading all Rights by query");

        TypedQuery<Right> query = this.em.createQuery("SELECT r FROM Right r where r.id > ?1", Right.class);
        query.setParameter(1, 0);

        List<Right> result = query.getResultList();

        result.forEach(right -> {
            System.out.printf("    %d -> %s\n", right.getId(), right);
        });
        System.out.printf("Finished reading all Rights by query: total %d\n", result.size());
    }
}
