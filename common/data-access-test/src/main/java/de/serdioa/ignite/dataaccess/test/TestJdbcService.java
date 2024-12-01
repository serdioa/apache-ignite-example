package de.serdioa.ignite.dataaccess.test;

import java.sql.Date;
import java.sql.Timestamp;

import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;


@Slf4j
@Component
public class TestJdbcService {

    private final PlatformTransactionManager tm;
    private final TransactionTemplate tt;
    private final JdbcTemplate jdbcTemplate;


    public TestJdbcService(final DataSource dataSource, PlatformTransactionManager platformTransactionManager) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.tm = platformTransactionManager;
        this.tt = new TransactionTemplate(this.tm);

        log.info("Starting {}", this.getClass().getSimpleName());
    }


    public void testReadUsers() {
        this.tt.executeWithoutResult(ts -> {
            this.jdbcTemplate.query("SELECT User_Id, Username, Password, Password_Changed_On, Expire_On, Locked FROM t_User", rs -> {
                System.out.println("Users found:");

                int count = 0;
                while (rs.next()) {
                    count++;

                    final Integer id = rs.getInt("User_Id");
                    final String username = rs.getString("Username");
                    final String password = rs.getString("Password");
                    final Timestamp passwordChangedOn = rs.getTimestamp("Password_Changed_On");
                    final Date expireOn = rs.getDate("Expire_On");
                    final Boolean locked = rs.getBoolean("Locked");

                    System.out.printf("    %d -> username=%s, password=%s, passwordChangedOn=%s, expireOn=%s, locked=%s\n",
                            id, username, password, passwordChangedOn, expireOn, locked);
                }

                System.out.printf("Total: %d Users\n", count);
            });
        });
    }


    public void testReadRoles() {
        this.tt.executeWithoutResult(ts -> {
            this.jdbcTemplate.query("SELECT Role_Id, Role_Name, Description FROM t_Role", rs -> {
                System.out.println("Roles found:");

                int count = 0;
                while (rs.next()) {
                    count++;

                    final Integer id = rs.getInt("Role_Id");
                    final String name = rs.getString("Role_Name");
                    final String description = rs.getString("Description");

                    System.out.printf("    %d -> name=%s, description=%s\n", id, name, description);
                }

                System.out.printf("Total: %d Roles\n", count);
            });
        });
    }


    public void testReadRights() {
        this.tt.executeWithoutResult(ts -> {
            this.jdbcTemplate.query("SELECT Right_Id, Right_Name, Description FROM t_Right", rs -> {
                System.out.println("Rights found:");

                int count = 0;
                while (rs.next()) {
                    count++;

                    final Integer id = rs.getInt("Right_Id");
                    final String name = rs.getString("Right_Name");
                    final String description = rs.getString("Description");

                    System.out.printf("    %d -> name=%s, description=%s\n", id, name, description);
                }

                System.out.printf("Total: %d Rights\n", count);
            });
        });
    }
}
