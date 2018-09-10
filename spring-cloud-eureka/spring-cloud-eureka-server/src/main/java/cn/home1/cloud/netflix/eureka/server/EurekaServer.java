package cn.home1.cloud.netflix.eureka.server;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.netflix.eureka.EurekaClientConfigBean;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.event.EventListener;

/**
 * Created by zhanghaolun on 16/9/26.
 */
@EnableEurekaServer
@SpringBootApplication
@Slf4j
public class EurekaServer {

    @Autowired
    private EurekaClientConfigBean eurekaClientConfig;

    @Autowired
    private EurekaInstanceConfigBean eurekaInstanceConfig;

    public static void main(final String... args) {
        new SpringApplicationBuilder(EurekaServer.class).web(true).run(args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void printImportantInfo() {
        log.info("eureka.client.service-url.defaultZone: {}", this.eurekaClientConfig.getServiceUrl().get("defaultZone"));
        log.info("eureka.instance.hostname: {}", this.eurekaInstanceConfig.getHostname());
    }
}
