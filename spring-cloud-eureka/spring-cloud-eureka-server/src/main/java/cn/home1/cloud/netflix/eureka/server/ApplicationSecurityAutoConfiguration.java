package cn.home1.cloud.netflix.eureka.server;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.SecurityDataConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.autoconfigure.security.servlet.SpringBootWebSecurityConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.WebSecurityEnablerConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;

/**
 * see: https://github.com/spring-projects/spring-boot/issues/12323
 * see: {@link org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration}
 * see: https://spring.io/blog/2017/09/15/security-changes-in-spring-boot-2-0-m4
 */
@Configuration
@ConditionalOnClass(DefaultAuthenticationEventPublisher.class)
// @ConditionalOnProperty(prefix = "spring.security", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(SecurityProperties.class)
@Import({SpringBootWebSecurityConfiguration.class, WebSecurityEnablerConfiguration.class,
    SecurityDataConfiguration.class})
public class ApplicationSecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(AuthenticationEventPublisher.class)
    public DefaultAuthenticationEventPublisher authenticationEventPublisher(
        ApplicationEventPublisher publisher) {
        return new DefaultAuthenticationEventPublisher(publisher);
    }

    // @ConditionalOnProperty(prefix = "spring.security", name = "enabled", havingValue = "true")
    @Configuration
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    static class ApplicationWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Value("${spring.security.enabled:false}")
        private Boolean enabled;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            //super.configure(http); // default config

            if (this.enabled) {
                // 401/403 issue of Eureka server on spring-cloud Finchley.RELEASE
                // see: https://github.com/spring-cloud/spring-cloud-netflix/issues/2754
                // see: https://github.com/spring-cloud/spring-cloud-netflix/pull/2992
                http = http //
                    .authorizeRequests() //
                    .requestMatchers(EndpointRequest.to("health", "info")).permitAll() //
                    .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole("ACTUATOR") //
                    .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() //
                    .antMatchers("/**").hasRole("USER") //
                    .and() //
                    .httpBasic().and() //
                    .formLogin().disable() //
                    .sessionManagement().sessionCreationPolicy(STATELESS).and() //
                    .exceptionHandling()
                    // .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)) //
                    .authenticationEntryPoint(new BasicAuthenticationEntryPoint()) //
                    .and() //
                ;
            } else {
                http = http //
                    .authorizeRequests() //
                    .antMatchers("/**").permitAll() //
                    .and() //
                ;
            }

            http //
                .csrf().ignoringAntMatchers("/eureka/**") //
            ;
        }

        @Override
        public void configure(final WebSecurity web) throws Exception {
            super.configure(web);
            web.httpFirewall(this.allowUrlEncodedSlashHttpFirewall());
        }

        @Bean
        public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
            // allow urls like "/eureka//apps/SERVICE-FILTER-TEST-APPLICATION"
            // see: https://stackoverflow.com/questions/48453980/spring-5-0-3-requestrejectedexception-the-request-was-rejected-because-the-url
            final DefaultHttpFirewall firewall = new DefaultHttpFirewall();
            // final StrictHttpFirewall firewall = new StrictHttpFirewall();
            // firewall.setAllowUrlEncodedSlash(true);
            return firewall;
        }
    }
}
