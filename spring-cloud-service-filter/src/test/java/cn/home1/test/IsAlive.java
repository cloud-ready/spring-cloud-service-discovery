package cn.home1.test;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor(access = PRIVATE)
@Slf4j
public final class IsAlive {

    public static boolean isSocketAlive(final String hostName, final int port) {
        return isSocketAlive(hostName, port, 2);
    }

    public static boolean isSocketAlive(final String hostName, final int port, final int timeoutInSeconds) {
        boolean isAlive = false;

        // Creates a socket address from a hostname and a port number
        final SocketAddress socketAddress = new InetSocketAddress(hostName, port);
        final Socket socket = new Socket();

        // Timeout required - it's in milliseconds
        int timeoutInMillis = (int) TimeUnit.SECONDS.toMillis(timeoutInSeconds);

        log.debug("isSocketAlive hostName: {}, port: {}, timeoutInSeconds: {}", hostName, port, timeoutInSeconds);
        try {
            socket.connect(socketAddress, timeoutInMillis);
            socket.close();
            isAlive = socket.isConnected();
        } catch (final SocketTimeoutException exception) {
            log.debug("isSocketAlive SocketTimeoutException {}:{}. {}", hostName, port, exception.getMessage());
        } catch (final IOException exception) {
            log.debug("isSocketAlive IOException - Unable to connect to {}:{}. {}", hostName, port, exception.getMessage());
        }

        log.debug("isSocketAlive hostName: {}, port: {}, result: {}", hostName, port, isAlive);
        return isAlive;
    }
}
