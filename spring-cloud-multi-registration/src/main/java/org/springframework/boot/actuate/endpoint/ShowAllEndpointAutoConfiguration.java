package org.springframework.boot.actuate.endpoint;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShowAllEndpointAutoConfiguration {

    @ConditionalOnClass(Endpoint.class)
    protected class ShowAllEndpointConfiguration {

        @Bean
        public ShowAllEndpoint showAllEndpoint(ApplicationContext applicationContext) {
            return new ShowAllEndpoint(applicationContext);
        }
    }
}
