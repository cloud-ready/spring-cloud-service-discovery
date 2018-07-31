package cn.home1.cloud.netflix.eureka.server;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

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

/**
 * see: https://github.com/spring-projects/spring-boot/issues/12323
 * see: {@link org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration}
 * see: https://spring.io/blog/2017/09/15/security-changes-in-spring-boot-2-0-m4
 */
@Configuration
@ConditionalOnClass(DefaultAuthenticationEventPublisher.class)
@ConditionalOnProperty(prefix = "spring.security", name = "enabled", havingValue = "true")
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

  @ConditionalOnProperty(prefix = "spring.security", name = "enabled", havingValue = "true")
  @Configuration
  @Order(SecurityProperties.BASIC_AUTH_ORDER)
  static class ApplicationWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
      //super.configure(http); // default config
      http //
          .authorizeRequests() //
          .requestMatchers(EndpointRequest.to("health", "info")).permitAll() //
          .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole("ACTUATOR") //
          .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() //
          .antMatchers("/**").hasRole("USER") //
          .and() //
          // 401/403 issue of Eureka server on spring-cloud Finchley.RELEASE
          // see: https://github.com/spring-cloud/spring-cloud-netflix/issues/2754
          // see: https://github.com/spring-cloud/spring-cloud-netflix/pull/2992
          .csrf().ignoringAntMatchers("/eureka/**").and() //
          .formLogin().disable() //
          .httpBasic().and() //
          .sessionManagement().sessionCreationPolicy(STATELESS).and() //
      ;
    }
  }
}
