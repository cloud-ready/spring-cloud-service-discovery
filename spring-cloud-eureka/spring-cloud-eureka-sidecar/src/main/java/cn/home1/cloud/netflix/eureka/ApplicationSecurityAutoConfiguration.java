package cn.home1.cloud.netflix.eureka;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

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
        protected void configure(final HttpSecurity http) throws Exception {
            //super.configure(http); // default config

            if (this.enabled) {
                http //
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
                ;
            } else {
                http //
                    .authorizeRequests() //
                    .antMatchers("/**").permitAll() //
                ;
            }
        }
    }
}
