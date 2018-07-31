package cn.home1.test;

import lombok.extern.slf4j.Slf4j;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

@Slf4j
public class DemoIntegrationTestWithDocker {

  private static EurekaTestContainer eurekaTestContainer;

  @BeforeClass
  public static void eurekaStart() throws Exception {
    final EurekaTestContainer eurekaTestContainer = new EurekaTestContainer();
    eurekaTestContainer.start();

    DemoIntegrationTestWithDocker.eurekaTestContainer = eurekaTestContainer;
  }

  @AfterClass
  public static void eurekaRemove() {
    DemoIntegrationTestWithDocker.eurekaTestContainer.remove();
  }

  @Test
  public void testEurekaClient() {
    log.info("testEurekaClient");
  }
}
