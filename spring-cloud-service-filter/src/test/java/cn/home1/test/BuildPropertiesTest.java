package cn.home1.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import cn.home1.cloud.netflix.eureka.client.EurekaInstanceBuildVersionAutoConfiguration;

import lombok.extern.slf4j.Slf4j;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.info.BuildProperties;

import java.util.Properties;

@Slf4j
public class BuildPropertiesTest {

    private final String dateTimeString = "2018-07-22T23:54:57+0800";
    private BuildProperties buildProperties;
    private EurekaInstanceBuildVersionAutoConfiguration buildVersionAutoConfiguration;

    @Before
    public void setUp() {
        final Properties properties = new Properties();
        properties.setProperty("time", this.dateTimeString);
        this.buildProperties = new BuildProperties(properties);

        this.buildVersionAutoConfiguration = new EurekaInstanceBuildVersionAutoConfiguration();
        this.buildVersionAutoConfiguration.setBuildProperties(this.buildProperties);
    }

    @Test
    public void testBuildTime() {
        assertNull(this.buildProperties.getTime());

        final String buildTime = this.buildVersionAutoConfiguration.getBuildTime();
        log.info("buildTime: {}", buildTime);
        assertEquals("1532274897000", buildTime);
    }
}
