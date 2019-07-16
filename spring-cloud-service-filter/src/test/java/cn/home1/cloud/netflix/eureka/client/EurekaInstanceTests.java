package cn.home1.cloud.netflix.eureka.client;

import cn.home1.test.EurekaTestContainer;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.shared.Application;

import java.net.ServerSocket;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

import org.joda.time.DateTime;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.netflix.eureka.CloudEurekaClient;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest(properties = "", webEnvironment = WebEnvironment.RANDOM_PORT)
public class EurekaInstanceTests {

    private final static EurekaTestContainer eurekaContainer;

    static {
        final ServerSocket serverPort = EurekaTestContainer.freePort();
        eurekaContainer = new EurekaTestContainer(serverPort);

        System.setProperty("local.eureka.client.service-url.defaultZone", eurekaContainer.getDefaultZone());
    }

    private final String springApplicationName = "service-filter-test-application";
    @Autowired
    private ApplicationContext applicationContext;

    @BeforeClass
    public static void eurekaStart() throws Exception {
        EurekaInstanceTests.eurekaContainer.start();
    }

    @AfterClass
    public static void eurekaRemove() {
        try {
            EurekaInstanceTests.eurekaContainer.remove();
        } catch (final Exception ex) {
            // ignored
        }
    }

    public DiscoveryClient getDiscoveryClient() {
        final CloudEurekaClient eurekaClient = this.applicationContext.getBean(CloudEurekaClient.class);
        return eurekaClient;
    }

    @Test
    public void testEurekaInstance() {
        log.info("testEurekaClient");

        final DateTime from = DateTime.now();
        final DateTime till = from.plusSeconds(80);

        final DiscoveryClient client = this.getDiscoveryClient();

        while (!Thread.currentThread().isInterrupted()) {
            try {
                final Application application = client.getApplication(this.springApplicationName);
                if (application != null) {
                    final List<InstanceInfo> instanceInfos = application.getInstances();
                    instanceInfos.forEach(instance -> {
                        log.info(String.format("instance %s: %s", instance.getId(), instance.getHomePageUrl()));
                        for (Entry<String, String> md : instance.getMetadata().entrySet()) {
                            log.info(String.format("  metaData %s: %s", md.getKey(), md.getValue()));
                        }
                    });

                    if (instanceInfos.size() > 0) {
                        return;
                    }
                }

                if (DateTime.now().isBefore(till)) {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(5));
                } else {
                    throw new RuntimeException("no application and instanceInfos found");
                }
            } catch (final InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
