package cn.home1.test;

import static java.lang.Integer.parseInt;
import static lombok.AccessLevel.PRIVATE;
import static org.joda.time.Seconds.secondsBetween;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.joda.time.DateTime;

import java.util.concurrent.TimeUnit;

@NoArgsConstructor(access = PRIVATE)
@Slf4j
public final class WaitForIt {

  public static boolean waitForIt(final String port, final int timeoutInSeconds) {
    return waitForIt(parseInt(port), timeoutInSeconds);
  }

  public static boolean waitForIt(final int port, final int timeoutInSeconds) {
    final String hostName = "127.0.0.1";
    final DateTime from = DateTime.now();
    final DateTime till = from.plusSeconds(timeoutInSeconds);

    DateTime now;
    while ((now = DateTime.now()).isBefore(till)) {
      log.info("trying to connect to {}:{}", hostName, port);
      if (IsAlive.isSocketAlive(hostName, port)) {
        log.info("connect to {}:{} succeed in {} seconds",
            hostName, port, secondsBetween(from, now).getSeconds());
        return true;
      } else {
        try {
          final int waitSeconds = 5;
          log.info("wait {} seconds to connect to {}:{}", waitSeconds, hostName, port);
          Thread.sleep(TimeUnit.SECONDS.toMillis(waitSeconds));
        } catch (final InterruptedException ex) {
          Thread.currentThread().interrupt();
        }
      }
    }
    log.info("connect to {}:{} failed", hostName, port);

    return false;
  }
}
