package org.springframework.boot.actuate.endpoint;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShowAllEndpointAutoConfiguration {

    @ConditionalOnClass(Endpoint.class)
    @SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC")
    protected static class ShowAllEndpointConfiguration {

        @Bean
        public ShowAllEndpoint showAllEndpoint(ApplicationContext applicationContext) {
            return new ShowAllEndpoint(applicationContext);
        }
    }
}
