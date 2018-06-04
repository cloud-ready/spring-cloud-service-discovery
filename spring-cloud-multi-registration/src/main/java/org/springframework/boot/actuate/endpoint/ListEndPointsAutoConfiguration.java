package org.springframework.boot.actuate.endpoint;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ListEndPointsAutoConfiguration {
  
  @Bean
  public ListEndPoints listEndPoints(List<Endpoint> endpoints) {
    return new ListEndPoints(endpoints);
  }
}
