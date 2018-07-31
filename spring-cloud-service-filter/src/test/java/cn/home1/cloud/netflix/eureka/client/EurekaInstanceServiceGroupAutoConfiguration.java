package cn.home1.cloud.netflix.eureka.client;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.discovery.EurekaClient;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

@Configuration
@ConditionalOnClass({EurekaInstanceConfigBean.class, EurekaClient.class})
@Slf4j
public class EurekaInstanceServiceGroupAutoConfiguration {

  @Autowired
  private Environment environment;

  @Autowired(required = false)
  private EurekaInstanceConfig instanceConfig;

  @Value("${eureka.instance.metadata.keys.instance-group:instance-group}")
  private String instanceGroupKey;

  @PostConstruct
  public void init() {
    if (this.instanceConfig == null) {
      return;
    }

    final String instanceGroupEnvVariable = this.environment.getProperty("INSTANCE_GROUP", "");
    final String instanceGroupProperty = this.environment.getProperty("instance.group", ".*");
    final String instanceGroup = isNotBlank(instanceGroupEnvVariable) ? instanceGroupEnvVariable : instanceGroupProperty;

    log.info("instance-group: {}", instanceGroup);

    this.instanceConfig.getMetadataMap().put(this.instanceGroupKey, instanceGroup);
  }
}
