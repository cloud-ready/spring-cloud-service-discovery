package org.springframework.cloud.client.serviceregistry;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * see: {@link org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationConfiguration}
 * @author haolun zhang
 */
@Configuration
@EnableConfigurationProperties(AutoServiceRegistrationProperties.class)
@ConditionalOnProperty(value = "spring.cloud.service-registry.auto-multi-registration.enabled", havingValue = "true")
public class AutoMultiServiceRegistrationConfiguration {
}
