package cn.home1.cloud.netflix.eureka.client;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

//@EnableEurekaClient
@EnableDiscoveryClient
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@Slf4j
public class EurekaInstanceTestApplication {

  public static void main(final String... args) {
    SpringApplication.run(EurekaInstanceTestApplication.class, args);
  }
}
