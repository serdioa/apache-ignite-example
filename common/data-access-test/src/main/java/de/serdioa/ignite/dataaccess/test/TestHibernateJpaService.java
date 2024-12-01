package de.serdioa.ignite.dataaccess.test;

import java.util.List;
import java.util.stream.StreamSupport;

import de.serdioa.ignite.domain.QRight;
import de.serdioa.ignite.domain.QRole;
import de.serdioa.ignite.domain.QUser;
import de.serdioa.ignite.domain.Right;
import de.serdioa.ignite.domain.Role;
import de.serdioa.ignite.domain.User;
import de.serdioa.jpa.hibernate.repository.RightRepository;
import de.serdioa.jpa.hibernate.repository.RoleRepository;
import de.serdioa.jpa.hibernate.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;


@Slf4j
@Component
public class TestHibernateJpaService {

    private final TransactionTemplate tt;
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final RightRepository rightRepo;


    public TestHibernateJpaService(final TransactionTemplate transactionTemplate,
            final UserRepository userRepo, final RoleRepository roleRepo, final RightRepository rightRepo) {
        this.tt = transactionTemplate;
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.rightRepo = rightRepo;

        log.info("Starting {}", this.getClass().getSimpleName());
    }


    public void testReadUsers() {
        System.out.println("Reading all Users by Spring Data repository");
        final List<User> result = this.tt.execute(ts -> {
            return this.userRepo.findAll();
        });

        result.forEach(user -> {
            System.out.printf("    %d -> %s\n", user.getId(), user);
        });
        System.out.printf("Finished reading all Users by Spring Data repository: total %d\n", result.size());
    }


    public void testReadRoles() {
        System.out.println("Reading all Roles by Spring Data repository");
        final List<Role> result = this.tt.execute(ts -> {
            return this.roleRepo.findAll();
        });

        result.forEach(role -> {
            System.out.printf("    %d -> %s\n", role.getId(), role);
        });
        System.out.printf("Finished reading all Roles by Spring Data repository: total %d\n", result.size());
    }


    public void testReadRights() {
        System.out.println("Reading all Rights by Spring Data repository");
        final List<Right> result = this.tt.execute(ts -> {
            return this.rightRepo.findAll();
        });

        result.forEach(right -> {
            System.out.printf("    %d -> %s\n", right.getId(), right);
        });
        System.out.printf("Finished reading all Rights by Spring Data repository: total %d\n", result.size());
    }


    public void testReadRolesByUserId() {
        System.out.println("Reading Roles by user ID from Spring Data repository");

        final List<User> users = this.tt.execute(ts -> {
            return this.userRepo.findAll();
        });
        for (User user : users) {
            final List<Role> userRoles = StreamSupport.stream(
                    this.roleRepo.findByUserId(user.getId()).spliterator(), false).toList();
            System.out.printf("    %d (%s) -> %s\n", user.getId(), user.getUsername(), userRoles);
        }

        System.out.println("Finished reading Roles by user ID from Spring Data repository");
    }


    public void testReadRightsByRoleId() {
        System.out.println("Reading Rights by role ID from Spring Data repository");

        final List<Role> roles = this.tt.execute(ts -> {
            return this.roleRepo.findAll();
        });
        for (Role role : roles) {
            final List<Right> roleRights = StreamSupport.stream(
                    this.rightRepo.findByRoleId(role.getId()).spliterator(), false).toList();
            System.out.printf("    %d (%s) -> %s\n", role.getId(), role.getRoleName(), roleRights);
        }

        System.out.println("Finished reading Roles by user ID from Spring Data repository");
    }


    public void testReadRightsByUserId() {
        System.out.println("Reading Rights by user ID from Spring Data repository");

        final List<User> users = this.tt.execute(ts -> {
            return this.userRepo.findAll();
        });
        for (User user : users) {
            final List<Right> userRights = StreamSupport.stream(
                    this.rightRepo.findByUserId(user.getId()).spliterator(), false).toList();
            System.out.printf("    %d (%s) -> %s\n", user.getId(), user.getUsername(), userRights);
        }

        System.out.println("Finished reading Reading by user ID from Spring Data repository");
    }


    public void testReadUsersQuerydsl() {
        System.out.println("Reading all Users by QueryDSL");
        final Iterable<User> result = this.tt.execute(ts -> {
            return this.userRepo.findAll(QUser.user.id.goe(0));
        });

        result.forEach(user -> {
            System.out.printf("    %d -> %s\n", user.getId(), user);
        });
        System.out.printf("Finished reading all Users by QueryDSL\n");
    }


    public void testReadRolesQuerydsl() {
        System.out.println("Reading all Roles by QueryDSL");
        final Iterable<Role> result = this.tt.execute(ts -> {
            return this.roleRepo.findAll(QRole.role.id.goe(0));
        });

        result.forEach(role -> {
            System.out.printf("    %d -> %s\n", role.getId(), role);
        });
        System.out.printf("Finished reading all Roles by QueryDSL\n");
    }


    public void testReadRightsQuerydsl() {
        System.out.println("Reading all Rights by QueryDSL");
        final Iterable<Right> result = this.tt.execute(ts -> {
            return this.rightRepo.findAll(QRight.right.id.goe(0));
        });

        result.forEach(right -> {
            System.out.printf("    %d -> %s\n", right.getId(), right);
        });
        System.out.printf("Finished reading all Rights by QueryDSL\n");
    }
}
