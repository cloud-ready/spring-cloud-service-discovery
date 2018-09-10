package cn.home1.cloud;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

import org.junit.Test;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

/**
 * see {@link org.springframework.cloud.commons.util.InetUtils#isPreferredAddress(InetAddress)}
 * see {@link org.springframework.cloud.commons.util.InetUtils#ignoreInterface(String)}
 */
@Slf4j
public class CloudInetUtilsTest {

    @Test
    public void testFindNonLoopbackAddress() throws SocketException {
        final List<String> found = Lists.newLinkedList();

        int lowest = Integer.MAX_VALUE;
        for (final Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces(); nics.hasMoreElements(); ) {
            final NetworkInterface ifc = nics.nextElement();
            if (ifc.isUp()) {
                log.info("interface: {} is up", ifc.getDisplayName());

                if (ifc.getIndex() < lowest) {
                    lowest = ifc.getIndex();
                }

                //if (!ignoreInterface(ifc.getDisplayName())) {
                for (final Enumeration<InetAddress> addrs = ifc.getInetAddresses(); addrs.hasMoreElements(); ) {
                    final InetAddress address = addrs.nextElement();
                    if (address instanceof Inet4Address && !address.isLoopbackAddress()) { //&& isPreferredAddress(address)
                        log.info("Found non-loopback interface: {}, address: {}, host: {}",
                            ifc.getDisplayName(), address.getHostAddress(), address.getHostName());
                        found.add(address.getHostAddress() + " " + address.getHostName());
                    }
                }
                //}
            } else {
                log.info("interface: {} is down", ifc.getDisplayName());
            }
        }
    }

    @Test
    public void testPreferredNetworks() {
        // IsPreferredAddress
        assertTrue("192.168.2.10".matches("^192\\.168\\..+"));
        assertTrue("192.168.1.10".matches("^192\\.168\\..+"));

        assertTrue("172.30.1.10".matches("^172\\.30\\..+"));
        assertFalse("172.17.0.10".matches("^172\\.30\\..+"));

        assertTrue("10.17.0.10".matches("^10\\..+"));
        assertTrue("10.18.0.10".matches("^10\\..+"));
    }

    @Test
    public void testIgnoredInterfaces() {
        // ignoreInterface
        assertTrue("utun2".matches("^[a-z]?tun[0-9]*"));
        assertTrue("tun0".matches("^[a-z]?tun[0-9]*"));

        assertTrue("awdl0".matches("^awdl[0-9]*"));

        assertTrue("lo0".matches("^lo[0-9]*"));
        assertTrue("lo".matches("^lo[0-9]*"));
    }
}
