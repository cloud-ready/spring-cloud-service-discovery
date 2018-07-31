package cn.home1.test;

import static cn.home1.test.IsAlive.isSocketAlive;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;

import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;

@Slf4j
public class IsAliveTest {

  @Test
  public void testIsAlive() throws IOException {
    assertFalse(isSocketAlive("127.0.0.1", 12345));

    final ServerSocket randomServerSocket = new ServerSocket(0);
    assertTrue(isSocketAlive("127.0.0.1", randomServerSocket.getLocalPort()));
    randomServerSocket.close();
    assertFalse(isSocketAlive("127.0.0.1", randomServerSocket.getLocalPort()));
  }
}
