package org.springframework.cloud.client.serviceregistry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

import javax.annotation.PostConstruct;

/**
 * in /META-INF/spring.factories
 * see: {@link org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationAutoConfiguration}
 *
 * @author haolun zhang
 */
@Configuration
@Import(AutoMultiServiceRegistrationConfiguration.class)
@ConditionalOnProperty(value = "spring.cloud.service-registry.auto-multi-registration.enabled", havingValue = "true")
public class AutoMultiServiceRegistrationAutoConfiguration {

  /**
   * i.e. EurekaAutoServiceRegistration, ConsulAutoServiceRegistration ...
   */
  @Autowired(required = false)
  private List<AutoServiceRegistration> autoServiceRegistrations;

  @Autowired
  private AutoServiceRegistrationProperties properties;

  @PostConstruct
  protected void init() {
    if ((autoServiceRegistrations == null || autoServiceRegistrations.size() < 1) && this.properties.isFailFast()) {
      throw new IllegalStateException("Auto Multi Service Registration has been requested, but there is no AutoServiceRegistration bean");
    }
  }
}
