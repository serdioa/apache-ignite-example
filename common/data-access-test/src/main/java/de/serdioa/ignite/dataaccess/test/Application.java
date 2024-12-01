package de.serdioa.ignite.dataaccess.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        final Application application = new Application();
        application.run(args);
    }


    public void run(final String[] args) {
        final SpringApplication application = new SpringApplication(Application.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        final ApplicationContext ctx = application.run(args);

//        this.testJdbcService(ctx);
//        this.testHibernateService(ctx);
        this.testHibernateJpaService(ctx);
    }


    private void testJdbcService(final ApplicationContext ctx) {
        final TestJdbcService service = ctx.getBean(TestJdbcService.class);

        service.testReadUsers();
        service.testReadRoles();
        service.testReadRights();
    }


    private void testHibernateService(final ApplicationContext ctx) {
        final TestHibernateService service = ctx.getBean(TestHibernateService.class);

        service.testReadUsers();
        service.testReadRoles();
        service.testReadRights();

        service.testReadUsersByQuery();
        service.testReadRolesByQuery();
        service.testReadRightsByQuery();
    }


    private void testHibernateJpaService(final ApplicationContext ctx) {
        final TestHibernateJpaService service = ctx.getBean(TestHibernateJpaService.class);

        service.testReadUsers();
        service.testReadRoles();
        service.testReadRights();

        service.testReadRolesByUserId();
        service.testReadRightsByRoleId();
        service.testReadRightsByUserId();

        service.testReadUsersQuerydsl();
        service.testReadRolesQuerydsl();

        // TODO: it looks like "right" is the reserved word. Queries are not working when "right" is used.
        // It has to be replaced by something else, for example "roleRight".
        // service.testReadRightsQuerydsl();
    }
}
