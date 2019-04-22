package cn.home1.cloud.netflix.eureka;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.netflix.sidecar.EnableSidecar;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * The @EnableSidecar annotation includes @EnableCircuitBreaker, @EnableDiscoveryClient, and @EnableZuulProxy
 */
@EnableSidecar
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@Slf4j
public class EurekaSidecarApplication {

    @Autowired
    private Environment environment;

    @Autowired
    private SecurityProperties securityProperties;

    public static void main(final String... args) {
        final ConfigurableApplicationContext context = SpringApplication.run(EurekaSidecarApplication.class, args);

        final EurekaSidecarApplication application = context.getBean(EurekaSidecarApplication.class);
        application.printInfo();
    }

    /**
     * see: {@link org.springframework.security.provisioning.InMemoryUserDetailsManager#createUser(org.springframework.security.core.userdetails.UserDetails)}
     */
    public void printInfo() {
        log.info("server.servlet.context-path (server.context-path deprecated since spring-boot 2.x): {}",
            this.environment.getProperty("server.servlet.context-path"));
        log.info("management.endpoints.web.base-path (management.context-path deprecated since spring-boot 2.x): {}",
            this.environment.getProperty("management.endpoints.web.base-path"));

        final String passwordFromSystemEnv = System.getenv("SPRING_SECURITY_USER_PASSWORD");
        if (StringUtils.isEmpty(passwordFromSystemEnv)) {
            final String username = this.securityProperties.getUser().getName();
            final String password = this.securityProperties.getUser().getPassword();

            log.info("username: {}, randomly generated password: {}", username, password);
        }
    }
}
