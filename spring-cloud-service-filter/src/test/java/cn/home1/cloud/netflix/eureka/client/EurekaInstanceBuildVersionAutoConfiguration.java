package cn.home1.cloud.netflix.eureka.client;

import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.discovery.EurekaClient;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.info.BuildProperties;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@ConditionalOnClass({EurekaInstanceConfigBean.class, EurekaClient.class})
@Slf4j
public class EurekaInstanceBuildVersionAutoConfiguration {

  @Autowired(required = false)
  private EurekaInstanceConfig instanceConfig;

  @Autowired(required = false)
  private BuildProperties buildProperties;

  @Value("${eureka.instance.metadata.keys.build-time:build-time}")
  private String buildTimeKey;

  @Value("${eureka.instance.metadata.keys.build-version:build-version}")
  private String buildVersionKey;

  @PostConstruct
  public void init() {
    if (this.instanceConfig == null || this.buildProperties == null) {
      return;
    }

    final String buildTime = this.buildProperties.getTime().toString();
    final String buildVersion = this.buildProperties.getVersion();

    log.info("build-time: {}", buildTime);
    log.info("build-version: {}", buildVersion);

    this.instanceConfig.getMetadataMap().put(this.buildTimeKey, buildTime);
    this.instanceConfig.getMetadataMap().put(this.buildVersionKey, buildVersion);
  }
}
