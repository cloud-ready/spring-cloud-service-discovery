package cn.home1.cloud.netflix.eureka.client;

import static java.time.Instant.ofEpochMilli;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.discovery.EurekaClient;

import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
    @Setter
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

        final String buildTime = this.getBuildTime();
        final String buildVersion = this.buildProperties.getVersion();

        log.info("build-time: {}", buildTime);
        log.info("build-version: {}", buildVersion);

        this.instanceConfig.getMetadataMap().put(this.buildTimeKey, buildTime);
        this.instanceConfig.getMetadataMap().put(this.buildVersionKey, buildVersion);
    }

    public static final DateTimeFormatter BUILD_TIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ");

    @SneakyThrows
    public String getBuildTime() {
        final java.time.Instant buildTime;

        final String buildTimeString = this.buildProperties.get("time");
        if (this.buildProperties.getTime() != null) {
            buildTime = this.buildProperties.getTime();
        } else if (isNotBlank(buildTimeString)) {
            final DateTime dateTime = BUILD_TIME_FORMAT.parseDateTime(buildTimeString);
            buildTime = ofEpochMilli(dateTime.getMillis());
        } else {
            buildTime = null;
        }

        return "" + buildTime.toEpochMilli();
    }
}
