package cn.home1.cloud.netflix.eureka.server;

import com.netflix.appinfo.InstanceInfo;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * see: https://stackoverflow.com/questions/42296407/spring-cloud-discovery-for-multiple-service-versions
 */
@Configuration
@Aspect
public class EurekaDashboardLabeler {

  @Value("${eureka.instance.metadata.keys.instance-group:instance-group}")
  private String instanceGroupKey;

  @Around("execution(public * com.netflix.appinfo.InstanceInfo.getId())")
  public String versionLabelAppInstances(final ProceedingJoinPoint jp) throws Throwable {
    final String instanceId = (String) jp.proceed();
    for (final StackTraceElement ste : Thread.currentThread().getStackTrace()) {
      // limit to EurekaController#populateApps in order to avoid side effects
      if (ste.getClassName().contains("EurekaController")) {
        final InstanceInfo info = (InstanceInfo) jp.getThis();
        final String instanceGroup = info.getMetadata().get(this.instanceGroupKey);
        if (StringUtils.hasText(instanceGroup)) {
          return String.format("%s [%s]", instanceId, instanceGroup);
        }
        break;
      }
    }
    return instanceId;
  }

  @Bean("post-construct-labeler")
  public EurekaDashboardLabeler init() {
    return EurekaDashboardLabeler.aspectOf();
  }

  private static EurekaDashboardLabeler instance = new EurekaDashboardLabeler();

  /**
   * Singleton pattern used by LTW then Spring
   */
  public static EurekaDashboardLabeler aspectOf() {
    return instance;
  }
}
