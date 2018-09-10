package org.springframework.cloud.client.serviceregistry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.Endpoint;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.client.serviceregistry.endpoint.MultiServiceRegistryEndpoint;
import org.springframework.cloud.consul.serviceregistry.ConsulServiceRegistryAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * http://host:port/server-context-path/management-context-path/service-registry/instance-status
 *
 * in /META-INF/spring.factories
 * see: {@link org.springframework.cloud.client.serviceregistry.ServiceRegistryAutoConfiguration}
 *
 * @author haolun zhang
 */
@Configuration
@AutoConfigureAfter({EurekaClientAutoConfiguration.class, ConsulServiceRegistryAutoConfiguration.class})
public class MultiServiceRegistryAutoConfiguration {

//  @ConditionalOnBean(ServiceRegistry.class)
//  @ConditionalOnClass(Endpoint.class)
//  protected class ServiceRegistryEndpointConfiguration {
//
//    @Autowired(required = false)
//    private Registration registration;
//
//    @Bean
//    public ServiceRegistryEndpoint serviceRegistryEndpoint(final ServiceRegistry serviceRegistry) {
//      ServiceRegistryEndpoint endpoint = new ServiceRegistryEndpoint(serviceRegistry);
//      endpoint.setRegistration(registration);
//      return endpoint;
//    }
//  }
//
//  @AutoConfigureAfter(ConsulServiceRegistryEndpointConfiguration.class)
//  @ConditionalOnBean(EurekaServiceRegistry.class)
//  @ConditionalOnClass(Endpoint.class)
//  @ConditionalOnMissingBean(ServiceRegistryEndpoint.class)
//  protected class EurekaServiceRegistryEndpointConfiguration {
//
//    @Autowired(required = false)
//    private EurekaRegistration registration;
//
//    @Bean
//    public ServiceRegistryEndpoint eurekaServiceRegistryEndpoint(final EurekaServiceRegistry serviceRegistry) {
//      ServiceRegistryEndpoint endpoint = new ServiceRegistryEndpoint(serviceRegistry);
//      endpoint.setRegistration(registration);
//      return endpoint;
//    }
//  }
//
//  @AutoConfigureBefore(EurekaServiceRegistryEndpointConfiguration.class)
//  @ConditionalOnBean(ConsulServiceRegistry.class)
//  @ConditionalOnClass(Endpoint.class)
//  @ConditionalOnMissingBean(ServiceRegistryEndpoint.class)
//  protected class ConsulServiceRegistryEndpointConfiguration {
//
//    @Autowired(required = false)
//    private ConsulRegistration registration;
//
//    @Bean
//    public ServiceRegistryEndpoint consulServiceRegistryEndpoint(final ConsulServiceRegistry serviceRegistry) {
//      ServiceRegistryEndpoint endpoint = new ServiceRegistryEndpoint(serviceRegistry);
//      endpoint.setRegistration(registration);
//      return endpoint;
//    }
//  }

    @ConditionalOnBean(ServiceRegistry.class)
    @ConditionalOnClass(Endpoint.class)
    protected class MultiServiceRegistryEndpointConfiguration {

        @Autowired(required = false)
        private List<Registration> registrations;

        @Bean
        public MultiServiceRegistryEndpoint serviceRegistryEndpoint(final List<ServiceRegistry> serviceRegistries) {
            MultiServiceRegistryEndpoint endpoint = new MultiServiceRegistryEndpoint(serviceRegistries);
            endpoint.setRegistrations(this.registrations);
            return endpoint;
        }
    }
}
